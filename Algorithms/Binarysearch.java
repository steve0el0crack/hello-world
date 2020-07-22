// Algorithms, Robert Sedgewick

// The first algorithm we are introduced to is a BinarySearch that is capable of finding the position in which a certain
// number is, within a sorted array. The implementation given in the book is too classic and I am gonna try to implement
// a "newer" one using HashMaps<Integer, Integer> imitating a sorting algorithm.

import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.Collection;
import java.lang.*;
import java.io.Console;

public class BinarySearch {

    public static int[] list (int length) {  // int[] is the type of the returning element of the function
	int[] objs;
	objs = new int[length];

	for(int i = 0; i < length; i++) {
	    Random rand = new Random();  //Here I create the instance of the class Random.
	    objs[i] = rand.nextInt(100);
	}
	//Arrays.sort(objs);  // If I return the list sorted, then a curious fact is to be noticed: If the length of the list is large enough, the ocurrences of each distinct number seemed to be equally distributed.
	return objs;  // It seems like I cannot do operations while returning. I wanted to do the sorting process in the same line as the ruturn process...?
    }


    public static HashMap<Integer, Integer> search(int[] collection, Integer searched) { // This tricky word STATIC denotes something that encapsulates a set of transformations... a function! And void means nil by the way, what means that the function is just returning nothing.

	HashMap<Integer, Integer> answer = new HashMap<Integer, Integer>();  
	answer.put(100, 0);

	for(int i = 0; i < collection.length; i++) {

    	    Integer fitness = Math.abs(collection[i] - searched);  // Everything depends on this new variable: How much near or far is the number I am currently looking at, from that which I am searching for.

	    if (fitness < (Integer) answer.keySet().toArray()[0]) {
		answer.clear();  // I want to remove the pair previously introduced, because there is now a new pair containing a number even nearer to the asked by the user.
		answer.put(fitness, i);  // And then add the new pair!
	    }
	    
	    if (collection[i] == searched) {
		answer.clear();
		answer.put(fitness, i);
		break;  // Because I found it already!
	    }
	}

	return answer;  // At the end I am always returning the HashMap containing the two Integers: The maximum fitness reached and the position in which it was reached.
    }


    public static void main(String[] args) {

	// An interval (int[]) with random elements (max = 100) will be generated.
	System.out.println("How long should be the list I am going to search into?");	
	int[] interval = list(Integer.parseInt(System.console().readLine()));
	//System.out.println("I've just created and correctly sorted the following list -> " + Arrays.toString(interval));  ... for inspection

	// The user types a number and we must determine if the number he gives to us is contained into the array we have just created! Lucky if it is in, 
	// otherwise the nearest number should be answered and of course its position.
	System.out.println("Which number are you giving to me?");	
	HashMap<Integer, Integer> output = search(interval, Integer.parseInt(System.console().readLine()));  // The output I want is the fitness of the number (or the number itself) who remained as the nearest, and its position. So for that I am using a HashMap.

	System.out.println("The nearest number in the list is " + interval[(Integer) output.values().toArray()[0]] + " and it is found at the position " + output.values().toArray()[0]);

    }
}

