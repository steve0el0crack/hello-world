import sys
import random

#Se definen las coordenadas del plano en el que se mueve nuestro caminante
x = int(sys.argv[1])
y = int(sys.argv[2])
coor = [x,y]


for x in range(0,int(sys.argv[3])):	#La cantidad de pasos que va a dar
	#print("---")
	for i in range(0,len(coor)):	#Cada paso es aleatorio entre 3 opciones
		value = random.randint(-1,1)
		#print("Para " + str(coor[i]) + "la variacion es " + str(value))
		coor[i] = coor[i] + value

print(coor) #coordenadas finales
