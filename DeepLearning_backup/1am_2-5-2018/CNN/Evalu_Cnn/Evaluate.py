from cntk.ops.functions import load_model
from PIL import Image 
import numpy as np

z = load_model("ConvNet_mytrain_29.dnn")
rgb_image = np.asarray(Image.open("img.png"), dtype=np.float32) - 128
#########################


bgr_image = rgb_image[..., [2, 1, 0]]
pic = np.ascontiguousarray(np.rollaxis(bgr_image, 2))

predictions = np.squeeze(z.eval({z.arguments[0]:[pic]}))
top_class = np.argmax(predictions)


print("This is predictions")
print(predictions)
print("Most possible class")
print (top_class)