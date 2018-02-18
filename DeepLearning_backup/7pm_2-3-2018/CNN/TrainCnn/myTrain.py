# Copyright (c) Microsoft. All rights reserved.

# Licensed under the MIT license. See LICENSE.md file in the project root
# for full license information.
# ==============================================================================

from __future__ import print_function
import numpy as np
import sys
import os
import cntk as C
import _cntk_py

# Paths relative to current python file.
abs_path   = os.path.dirname(os.path.abspath(__file__))
data_path  = ""
model_path = os.path.join(abs_path, "Models")


# Define the reader for both training and evaluation action.
def create_reader(path, is_training, input_dim, label_dim):
    return C.io.MinibatchSource(C.io.CTFDeserializer(path, C.io.StreamDefs(
        features  = C.io.StreamDef(field='features', shape=input_dim),
        labels    = C.io.StreamDef(field='labels',   shape=label_dim)
    )), randomize=is_training, max_sweeps = C.io.INFINITELY_REPEAT if is_training else 1)


# Creates and trains a feedforward classification model for MNIST images
def convnet_cifar10(debug_output=False, data_path=data_path, epoch_size=30, minibatch_size=4, max_epochs=30):
    _cntk_py.set_computation_network_trace_level(0)

    image_height = 32
    image_width  = 32
    num_channels = 3
    input_dim = image_height * image_width * num_channels
    num_output_classes = 3

    # Input variables denoting the features and label data
    input_var = C.ops.input_variable((num_channels, image_height, image_width), np.float32)
    label_var = C.ops.input_variable(num_output_classes, np.float32)

    # Instantiate the feedforward classification model
    input_removemean = C.ops.minus(input_var, C.ops.constant(128))
    scaled_input = C.ops.element_times(C.ops.constant(0.00390625), input_removemean)

    with C.layers.default_options(activation=C.ops.relu, pad=True):
        z = C.layers.Sequential([
            C.layers.For(range(2), lambda : [
                C.layers.Convolution2D((3,3), 64),
                C.layers.Convolution2D((3,3), 64),
                C.layers.MaxPooling((3,3), (2,2))
            ]),
            C.layers.For(range(2), lambda i: [
                C.layers.Dense([256,128][i]),
                C.layers.Dropout(0.5)
            ]),
            C.layers.Dense(num_output_classes, activation=None)
        ])(scaled_input)

    ce = C.losses.cross_entropy_with_softmax(z, label_var)
    pe = C.metrics.classification_error(z, label_var)

    reader_train = create_reader(os.path.join('train_test.txt'), True, input_dim, num_output_classes)

    # Set learning parameters
    lr_per_sample          = [0.0015625]*10 + [0.00046875]*10 + [0.00015625]
    lr_schedule            = C.learning_parameter_schedule(lr_per_sample, minibatch_size=1, epoch_size=epoch_size)
    mm                     = [0.9] * 20
    mm_schedule            = C.learners.momentum_schedule(mm, epoch_size=epoch_size, minibatch_size=minibatch_size)
    l2_reg_weight          = 0.002

    # Instantiate the trainer object to drive the model training
    learner = C.learners.momentum_sgd(z.parameters, lr_schedule, mm_schedule, minibatch_size=minibatch_size,
                                        l2_regularization_weight = l2_reg_weight)
    progress_printer = C.logging.ProgressPrinter(tag='Training', num_epochs=max_epochs)
    trainer = C.Trainer(z, (ce, pe), learner, progress_printer)

    # define mapping from reader streams to network inputs
    input_map = {
        input_var  : reader_train.streams.features,
        label_var  : reader_train.streams.labels
    }

    C.logging.log_number_of_parameters(z) ; print()

    # Get minibatches of images to train with and perform model training
    for epoch in range(max_epochs):       # loop over epochs
        sample_count = 0
        while sample_count < epoch_size:  # loop over minibatches in the epoch
            data = reader_train.next_minibatch(min(minibatch_size, epoch_size - sample_count), input_map=input_map) # fetch minibatch.
            trainer.train_minibatch(data)                                   # update model with it
            sample_count += trainer.previous_minibatch_sample_count         # count samples processed so far

        trainer.summarize_training_progress()
        z.save(os.path.join(model_path, "ConvNet_mytrain_{}.dnn".format(epoch)))

 
    print ("_________the trainning is finished___________")

if __name__=='__main__':
    convnet_cifar10()

