import sys, time

for x in range(100):
	time.sleep(0.01)
	sys.stdout.write(u"\u001b[10D\u001b[38;5;" + str(x) + "m" + str(x) + "%")
	sys.stdout.flush()
print "\nCOLORS"

for x in range(16):
	for y in range(16):
		num = str(16*x + y)	
		sys.stdout.write(u"\u001b[38;5;" + num + "m" + num.ljust(4))
	print 
	
print u"\u001b[0m"

