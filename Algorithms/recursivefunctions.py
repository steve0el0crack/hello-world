
#RETURN in RECURSIVE FUNCTIONS --------> They are only NESTED / EMBBEBED functions, that RETURN to its very next upper level

def test(x):
	if x > 9 : 
        	print x
		return test(x - 10)
	else:
		return x

x = int(input())
print('this should be real value', test(x))
