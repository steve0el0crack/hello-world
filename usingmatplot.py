from PIL import Image
from matplotlib.pylab import *

#read image to array
im = array(Image.open("./fotos/Image1.png").convert("L"))

#create a new figure
figure()

#do not use colors
gray()

# show contours with origin upper left corner
contour(im, origin="image") 
axis("equal")
axis("off")

figure()
hist(im.flatten(),128)

show()
