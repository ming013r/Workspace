from PIL import Image
import glob, os


image_size=32,32

def resize(f_num):
	folder=str(f_num)
	filename_0=1
	filename_1=1
	filename_2=1
	for infile in glob.glob("image_raw/"+folder+"/*.PNG"):
		print (infile)
		img = Image.open(infile)
		img.load()
		img=img.resize(image_size)
		if f_num==filename_0:
			img.save("image/"+str(filename)+".png")
			filename=int(filename)+1
		if f_num==filename_0:
			img.save("image/"+str(filename)+".png")
			filename=int(filename)+1
		os.remove(infile)
	
def ToPNG(f_num):
	folder=str(f_num)
	directory = ""
	for infile in glob.glob("image_raw/"+folder+"/*.JPG"):
		file, ext = os.path.splitext(infile)
		im = Image.open(infile)
		rgb_im = im.convert('RGB')
		rgb_im.save(directory + file + ".png", "PNG")
	for infile in glob.glob("image_raw/"+folder+"/*.jpg"):
		file, ext = os.path.splitext(infile)
		im = Image.open(infile)
		rgb_im = im.convert('RGB')
		rgb_im.save(directory + file + ".png", "PNG") 
	for infile in glob.glob("image_raw/"+folder+"/*.JPEG"):
		file, ext = os.path.splitext(infile)
		im = Image.open(infile)
		rgb_im = im.convert('RGB')
		rgb_im.save(directory + file + ".png", "PNG")
	for infile in glob.glob("image_raw*/"+folder+"/.jpeg"):
		file, ext = os.path.splitext(infile)
		im = Image.open(infile)
		rgb_im = im.convert('RGB')
		rgb_im.save(directory + file + ".png", "PNG")



if __name__=='__main__':
	for folder in range(2):
		ToPNG(folder)
	resize()