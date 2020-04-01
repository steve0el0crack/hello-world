import os
from PIL import Image, ImageFilter

#Read image
im = Image.open("./fotos/Image1.png")

#Display image
def display(holder, name):
	holder.save(name)
	os.system("open " + name)
display(im, "hola.png")

#Creating Thumbnails
tmb_size = (128, 128)
im.thumbnail(tmb_size)	#Opposite to save, does not generate a new file containing the image. But instead modify it.

display(im, "mmodified.png")
