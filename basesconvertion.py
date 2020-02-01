#!/usr/bin/python
# -*- coding: latin-1 -*-

# CONVERSIÓN DE BASES 

import sys
import random

terminal = sys.argv[1:]
num = int(terminal[0])	#String	
base = int(terminal[1])	#¿Y cómo sería con bases de más de 2 dígitos? 


class conversiondebases():

	contador = 0
	def _init_(self):
		self.version = contador 
		print "Version " + str(contador)
		contador = contador + 1

	#Objetivo:	Convertir cifra A en base B, a cifra DEC en base 10
	def todec(self,a,b):			#a: Número, b: Base original
		dec = 0
		i = 0	#Termina siendo >1 al len(init), éste no se corre en el loop
		for x in str(a):
			
			#print "Dígito: " + x
			#print "Base: " + str(b) + ", elevado a la  " + str(len(str(a))-1-i)  
			#print "\n"

			dec = dec + int(x)*(b**(len(str(a))-1-i))
			i = i+1

		#print str(a) + " en base " + str(b) + " es " + str(dec)
		return dec	#Ahora en base 10

	#Objetivo:	Convertir cifra A en base 10, a cifra OUTPUT en base B
	def tonew(self,a,b):					#b especifica la nueva BASE
		initial = a				#Se hace una copia para conservar la cifra original
		output = []	
		while a >= (b-1):			#El rango del dígito debe ser [0,(base-1)]
			digit = a % b			#En la primera corrida se consiguen las unidades
			a = (a - digit)/b
			output.append(str(digit))	#Manejar strings es más fácil
		output.reverse()			#Para tener la cifra con el orden correcto de los dígitos	
		result = "".join(output)
		
		print str(initial) + " es " + str(result) + " en base " + str(b)
		return result


#print "TONEW:\n4 en base 2 es: " + str(tonew(4,2)) + "\n"
#print "TODEC\n100 en base 2, a base 10 es: " + str(todec(100,2)) + "\n"

function = raw_input("Tondec (0) or Tonew (1)?: ")

version0 = conversiondebases()

if function == str(0):
	version0.todec(num, base)	
else:
	version0.tonew(num, base)

	
