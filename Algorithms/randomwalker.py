import sys
import random
import functools

#FUNCTION FRAMEWORK
def present(x):
	print str(x)

def presentarray(array):
	map(lambda x: present(x), array)

def addkey(dicc, key, value):
	dicc[key] = value
	present(dicc)
	
def renderpos(x):
	print "-"

def renderwalker():
	print "X"

xs = []
ys = []
def getindexs(history):	
	xs.append(history[":x"])
	ys.append(history[":y"])

#VARIABLES
x = int(sys.argv[1])
y = int(sys.argv[2])
origin = [x,y]
pacenum = int(sys.argv[3])

def change():
	auxiliar = [-1,1]
	change = []
	change.append(random.randint(-1,1))
	if change[0] == 0:	# (0, 0)
		change.append(auxiliar[random.randint(0,1)])
	else:
		change.append(random.randint(-1,1))
	return change

def action(origin, paceq):
	print "ORIGIN -------> " + str(origin)
	origincopy = origin[:]
	theway = []
	theway.append({":x" : origincopy[0], ":y" : origincopy[1]})		#FINISH PLACE
	for pace in range(paceq):	
		changevector = change()
		for axis in range(len(origincopy)):				#(x, y, z, ...)	--------> (x + k, y, z) / (x , y + k, z) /  (x , y, z + k)
			
			origincopy[axis] = origincopy[axis] + changevector[axis]	#AXIS TRANSFORMATION
		theway.append({":x" : origincopy[0], ":y" : origincopy[1]})	#FINISH PLACE
		print "Pace " + str(pace + 1) + " -------> " + str(origincopy)
	finalpos = origincopy
	return theway
theway = action(origin, pacenum)	
print "//////// THE WAY //////////"
present(theway)		#THE WAY						
print "///////////////////////////"

# INITIALIZING  WORLD
map(lambda history: getindexs(history), theway)
map(lambda tosort: tosort.sort(), [xs, ys])	
print xs
print ys
xlowest = xs[0]
xmaximal = xs[-1]
ylowest = ys[0]
ymaximal = ys[-1]
print "The lowest X / Y: " + str(xlowest) + " " + str(ylowest)
print "The maximal X / Y: " + str(xmaximal) + " " + str(ymaximal) 
		 
wdims = [xmaximal - xlowest + 1, ymaximal - ylowest + 1]	#DIMENSIONS of world BASED on THE WAY WALKED 	
print str(wdims[0]) + " x " + str(wdims[1])
tmpworld = []							#ROH WORLD
map(lambda reihe: map(lambda column: map(lambda pair: tmpworld.append(pair),[{":y" : reihe, ":x" : column}]), range(xlowest, wdims[0])), range(ylowest, wdims[1]))

world = []
for ydimension in range(wdims[1]):
	tmp = []
	xdimension = wdims[0]
	world.append(tmpworld[ydimension*xdimension: (ydimension+1)*xdimension])	#[1, 2, 3, 4][0 : 2] == [1, 2] 
print "%%%%%%%%%% THE WORLD %%%%%%%%%%"
presentarray(world)						#MATRIXed WORLD
print "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"


def worldfilter(pace):
	print "MAKING FILTER"
	return filter(lambda x: x == pace, world)
	
#map lambda x: worldfilter(x), 
#WALKED WORLD	-------->	("-" or "X")

#present(worldfilter())


#map( lambda history: map(lambda y: addkey(y, "ontheway", True), worldfilter(history)), retained)

#map(lambda history: addkey(history, ":ontheway", True), retained)
#presentarray(world)





