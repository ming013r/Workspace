from cntk.ops.functions import load_model
from PIL import Image 
import numpy as np

modelName="ConvNet_mytrain_0.dnn"
z = load_model(modelName)

#########################
count_total=0
count_correct=0


for type0 in range(1,31):
	rgb_image = np.asarray(Image.open("0/"+str(type0)+".png"), dtype=np.float32) - 128
	bgr_image = rgb_image[..., [2, 1, 0]]
	pic = np.ascontiguousarray(np.rollaxis(bgr_image, 2))
	predictions = np.squeeze(z.eval({z.arguments[0]:[pic]}))
	top_class = np.argmax(predictions)
	print("This is predictions")
	print(predictions)
	print("Most possible class for \"file name :"+"0/"+str(type0)+".png"+"\"")
	print (top_class)
	
	if(top_class==0):
		count_correct+=1
	count_total+=1
for type1 in range(1,31):
	rgb_image = np.asarray(Image.open("1/"+str(type1)+".png"), dtype=np.float32) - 128
	bgr_image = rgb_image[..., [2, 1, 0]]
	pic = np.ascontiguousarray(np.rollaxis(bgr_image, 2))
	predictions = np.squeeze(z.eval({z.arguments[0]:[pic]}))
	top_class = np.argmax(predictions)
	print("This is predictions")
	print(predictions)
	print("Most possible class for \"file name :"+"1/"+str(type1)+".png"+"\"")
	print (top_class)
	
	if(top_class==1):
		count_correct+=1
	count_total+=1
for type2 in range(1,31):
	rgb_image = np.asarray(Image.open("2/"+str(type2)+".png"), dtype=np.float32) - 128
	bgr_image = rgb_image[..., [2, 1, 0]]
	pic = np.ascontiguousarray(np.rollaxis(bgr_image, 2))
	predictions = np.squeeze(z.eval({z.arguments[0]:[pic]}))
	top_class = np.argmax(predictions)
	print("This is predictions")
	print(predictions)
	print("Most possible class for \"file name :"+"2/"+str(type2)+".png"+"\"")
	print (top_class)
	
	if(top_class==2):
		count_correct+=1
	count_total+=1

print("correct:"+str(count_correct)+", total:"+str(count_total))
rate = count_correct/count_total
print("Finished")
print("Correct Rate:"+ str(round(rate,2)*100) +"%")
print("Using model :" +modelName)