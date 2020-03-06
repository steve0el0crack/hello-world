import sys
import random

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

print "----      ORIGIN\n" + str(origin) + "\n----"
def action(origin):
	origincopy = origin[:]
	theway = []
	theway.append({":x" : origincopy[0], ":y" : origincopy[1]})		#FINISH PLACE
	for pace in range(pacenum):	
		print "******" + str(pace) + "*******"
		changevector = change()
		for axis in range(len(origincopy)):				#(x, y, z, ...)	--------> (x + k, y, z) / (x , y + k, z) /  (x , y, z + k)
			
			origincopy[axis] = origincopy[axis] + changevector[axis]	#AXIS TRANSFORMATION
			print "......\n" + str(origincopy) + "\n......"
		theway.append({":x" : origincopy[0], ":y" : origincopy[1]})	#FINISH PLACE
		print "Pace " + str(pace + 1) + " -------> " + str(origincopy)
	finalpos = origincopy
	return theway
#action(origin)

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

retained = []
def retain(x):
	retained.append(x) 

xs = []
ys = []
def getindexs(history):
	xs.append(history[":x"])
	ys.append(history[":y"])
	return xs, ys

#map(lambda x: present(x[":x"]), action(origin)[1])

map(lambda returned: retain(returned), action(origin))	#To RETAIN a value -------> [{:x - , :y - }, {...}, ...]	THE WAY
map(lambda history: getindexs(history), retained)	#To SEPARATE {:x k1, :y k2} -----> [k1, ...] / [k2, ...]	INDEXs
map(lambda tosort: tosort.sort(), [xs, ys])		#To LOCATE (X/Y) - (max/min)					WORLDs DIMENSIONs
print "Xs"
present(xs)
print "Ys"
present(ys)

present(retained)

xlowest = xs[0]
xmaximal = xs[-1]
ylowest = ys[0]
ymaximal = ys[-1]
print "The lowest X / Y: " + str(xlowest) + " " + str(ylowest)
print "The maximal X / Y: " + str(xmaximal) + " " + str(ymaximal) 
#renderwalker()

print "%%%%%%%%%% THE WORLD %%%%%%%%%%"		 
meassures = [xs[-1] - xs[0], ys[-1] - ys[0]]	#DIMENSIONS of world DEPEND on THE WAY WALKED 	
world = []
map(lambda reihe: map(lambda column: map(lambda pair: world.append(pair),[{":y" : reihe, ":x" : column}]), range(xs[0], meassures[0] + 1)), range(ys[1], meassures[1] + 1))				#To create places {":reihe": K, ":column": K} -----> coor = [reihe, column]
#presentarray(world)

def worldfilter(history):
	print "MAKING FILTER"
	return filter(lambda x: x == history, world)


#WALKED WORLD	-------->	("-" or "X")

worldfilter


#map( lambda history: map(lambda y: addkey(y, "ontheway", True), worldfilter(history)), retained)

#map(lambda history: addkey(history, ":ontheway", True), retained)
#presentarray(world)





