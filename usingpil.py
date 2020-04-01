import os
from PIL import Image, ImageFilter

#Read image
im = Image.open("./fotos/Image1.png")

#Display image
im.save("tmp.jpg")
os.system("open tmp.jpg")


