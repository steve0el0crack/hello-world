#!/usr/bin/python
# -*- coding: latin-1 -*-

# CONVERSIÓN DE BASES 

import sys
import random

# ------> NUMBER, BASE
terminal = sys.argv[1:]
num = int(terminal[0])	#String	
base = int(terminal[1])	#¿Y cómo sería con bases de más de 2 dígitos? 

#Objetivo:	A <> b ----> DEC <> 10
def todec(a,b):			
	dec = 0
	i = 0	#Termina siendo >1 al len(init), éste no se corre en el loop
	for x in str(a):
		dec = dec + int(x)*(b**(len(str(a))-1-i))
		i = i+1
	return dec	#Ahora en base 10

#Objetivo:	Convertir cifra A en base 10, a cifra OUTPUT en base B
def tonew(a,b):				
	initial = a				
	output = []	
	while a >= (b-1):			
		digit = a % b			#First iteration -----> UNITS
		a = (a - digit)/b
		output.append(str(digit))	
	output.reverse()			#2 [0, 1] -----> 2 [1, 0]	
	result = "".join(output)
	
	print str(initial) + " es " + str(result) + " en base " + str(b)
	return result



function = raw_input("Tondec (0) or Tonew (1)?: ")

if function == str(0):
	print todec(num, base)	
else:
	print tonew(num, base)

	
