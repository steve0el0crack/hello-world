import sys
import random

# WORLD -------> (WIDTH, HEIGTH) = (X, Y)
x = int(sys.argv[1])
y = int(sys.argv[2])
origin = [x,y]

print "----      ORIGIN\n" + str(origin) + "\n----"
def action(origin):
	origincopy = origin[:]
	theway = []
	theway.append({":x" : origincopy[0], ":y" : origincopy[1]})
	
	print range(int(sys.argv[3]))
	print "ENTERING LOOP"	
	for pace in range(int(sys.argv[3])):	# PACEs number
		print "******" + str(pace) + "*******"
		for axis in range(len(origincopy)):			#(x, y, z, ...)	--------> (x + k, y, z) / (x , y + k, z) /  (x , y, z + k)
			
			change = random.randint(-1,1)			#DIRECTION	--------> +-(k, K)
			origincopy[axis] = origincopy[axis] + change	#TRANSFORMATION
			print "......\n" + str(origincopy) + "\n......"
		theway.append({":x" : origincopy[0], ":y" : origincopy[1]})	
		print "FINAL -------> " + str(origincopy)
	print "OUT OF LOOP"
	finalpos = origincopy
	return finalpos, theway
action(origin)

#FUNCTION FRAMEWORK
def present(x):
	print str(x)

def renderpos(x):
	print "-"

def renderwalker():
	print "X"

#map(lambda x: present(x[":x"]), action(origin)[1])

#renderwalker()

#To create places {":reihe": K, ":column": K} -----> coor = [reihe, column]
#map(lambda reihe: map(lambda column: map(lambda pair: present(pair),[{":reihe" : reihe, ":column" : column}]), range(coor[1])), range(coor[0]))
