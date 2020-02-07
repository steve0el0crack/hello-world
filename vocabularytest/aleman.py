import random
import sys

#Se leen los diccionarios creados en los idiomas A y B
espanol = open('aleman_dic.txt','r')
aleman = open('aleman_div_espa.txt','r') 
esp=espanol.read().split('\n')
ale=aleman.read().split("\n")

puntaje=0

#print("hi")

#Comienza el QUIZ con una cantidad X de preguntas
for a in range(int(sys.argv[1])):
	crear = (random.randint(0,len(ale)))
	respuesta=input(">>" + ale[crear-1] + '\n')
	if respuesta == esp[crear-1]:
		print("correcto \n")
		puntaje=puntaje + 1
	else: 
		print("mal la respuesta correcta es " + esp[crear-1]) 
		#print('Palabra numero ' + str(crear) + "\n")

print("Ha obtenido: " + puntaje + "puntos")
