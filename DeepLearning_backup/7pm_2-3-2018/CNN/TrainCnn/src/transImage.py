from PIL import Image
import numpy as np
import sys
import glob,os
import cntk as C
import _cntk_py



def load_image() :
	file=open("train_test.txt","w")
	for types in range(3):
		for img_num in range(30):
			try:
				img = Image.open("image/"+str(types)+"/"+str(img_num)+".png")
				img.load()
				data = np.asarray( img, dtype="int32" )
				if types==0:
					file.write("|labels 1 0 0 0 0 0 0 0 0 |features ")
				if types==1:
					file.write("|labels 0 1 0 0 0 0 0 0 0 |features ")
				if types==2:
					file.write("|labels 0 0 1 0 0 0 0 0 0 |features ")
				for d in range(3):
					for x in range(32):
						for y in range(32):
							file.write(str(data[x][y][d])+" ")
				file.write("\n")
				
			except:
				if None:
					print("exception")
	file.close()

				
if __name__=='__main__':
    load_image()
	
	
	
	
	



