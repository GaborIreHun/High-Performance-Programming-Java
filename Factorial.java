package com.gabor.highPerformanceProgramming.week4;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 *
 * @author seank - This program demonstrates the calculation of a Factorial
 * value by using recursion
 * Modified in accordance with Session Labs 4 by @author Gabor Sebestyen
 * I created iterative methods, one to that capable for valid output of n! up to 18!
 * and another iterative method that that capable for valid output of n! from 19.
 * BigInteger has been utilized here.
 * I also created three Stream methods, one that capable for valid output of n! up to 18!
 * and another stream method that that capable for valid output of n! from 19.
 * BigInteger has been utilized here. The third method is similar to the previously mentioned one,
 * however, it is utilizing parallel.
 *
 */
public class Factorial {

   public static void main(String[] args) {
   
      int n;
   
      Scanner s = new Scanner(System.in);
      System.out.print("Enter an integer to calculate the Factorial value : ");
   
      n = s.nextInt();

      // If statement has been utilized to created valid output for any input.
      if (n < 20){
         System.out.println("Factorial of "+ n + " using recursion is: " + factorialUsingRecursion(n));
         System.out.println("Factorial of "+ n + " using iteration is: " + factorialUsingIteration(n));
         System.out.println("Factorial of "+ n + " using stream is:    " + factorialUsingParallelStreams(n));
      }
      else{
         System.out.println("Factorial of "+ n + " using iteration is:          " + factorialUsingIterationBigInteger(n));
         System.out.println("Factorial of "+ n + " using stream is:             " + factorialUsingStreamBigInteger(n));
         System.out.println("Factorial of "+ n + " using stream in parallel is: " + factorialUsingParallelStreamBigInteger(n));
      }

      s.close();
   }


   public static long factorialUsingRecursion(int n) {

      /* This is the 'exit condition' for our recursive method

         Session 4 Exercise (a):
         The importance of the exit condition is to ensure the method
         has a condition that allows it to exit from the recursion.
         In the absence of this it would act like an infinite loop.
       */
      if (n <= 2) { return n;}
      return n * factorialUsingRecursion(n - 1);
   }


   /**
    * Iterative method created for calculating n! as per Session 4 Labs required
    * @param n: The integer value of the factorials product
    * @return returning the value of n!
    */
   public static long factorialUsingIteration(int n) {

      // creating long variable that shall be returned to allow bigger results
      long factorial = n;

      // ensuring the value of 1 will be returned if the n is 0
      if (n == 0 || n == 1){
         factorial = 1;
      }
      else{
         for (int i = 1; i < n; i++){
            factorial *= (n - i);
         }
      }

      return factorial;
   }


   /**
    * Stream method created for calculating n! extra task for Session 4 Labs
    * @param n: The integer value of the factorials product
    * @return returning the value of n!
    */
   public static long factorialUsingParallelStreams(int n) {

      // returning 1 if the n is smaller than 2
      if(n < 2){ return n;}

      // returning n! when n is larger than 2
      return LongStream.rangeClosed(2, n).parallel().reduce(1, (a, b) -> a * b);
   }


   /**
    * Iterative method created to calculate the value of n! when the n is greater than 19
    * BigInteger has been utilized for this method to handled extra large values
    * @param n: The integer value of the factorials product
    * @return returning the value of n!
    */
   public static BigInteger factorialUsingIterationBigInteger(int n) {

      // initializing counter variable to help assign values to Biginteger[]
      // and upToNineteen to help create 19!
      int counter = 1, upToNineteen = 20;

      // initializing long variable to store 19!
      long factorialL = 1;

      // Declaring BigInteger variable for returning the result over 19!
      BigInteger factorial;

      BigInteger test;

      // Declaring BigInteger array to store BigInteger factorial values
      BigInteger[] bigIntegers;

      // Initializing the size of the array according to the size of n
      if (n > 20) { bigIntegers = new BigInteger[n-20];}
      else { bigIntegers = new BigInteger[1];}

      // calculating 19!
      factorialL = LongStream.rangeClosed(2, 19).reduce(1, (a, b) -> a * b);

      // parsing 19! long value to BigInteger
      BigInteger factorialForBig = new BigInteger(Long.toString(factorialL));

      // calculating 20!
      BigInteger firstBig = new BigInteger(String.valueOf(factorialForBig
              .multiply(new BigInteger(Integer.toString(20)))));

      // storing 20! as the first element of bigIntegers array
      bigIntegers[0] =  firstBig;


      // calculating from 21! to n!-1 and storing the values to the biginteger array in ascending order
      for (int i = 21; i < n; i++){
         bigIntegers[counter] = new BigInteger(String.valueOf(bigIntegers[counter-1]
                 .multiply(new BigInteger(Integer.toString(i)))));
         counter++;
      }

      // declaring the value of n! according to the size of n
      if (n == 20) { factorial = bigIntegers[0];}
      else factorial = bigIntegers[bigIntegers.length - 1].multiply(new BigInteger(Integer.toString(n)));

      // returning 1 if the n is smaller than 2
      if (n < 2) { return new BigInteger(String.valueOf(1));}

      // returning n! when n is larger than 2
      return factorial;
   }

   /**
    * Stream method created to calculate the value of n! when the n is greater than 19 (blog.emptyq.net, 2022)
    * BigInteger has been utilized for this method to handled extra large values
    * @param n: The integer value of the factorials product
    * @return returning the value of n!
    */
   public static BigInteger factorialUsingStreamBigInteger(int n) {

      // returning 1 if the n is smaller than 2
      if (n < 2) { return new BigInteger(String.valueOf(1));}

      // returning the result of calculation of n!
      return IntStream.rangeClosed(2, n)
              .mapToObj(BigInteger :: valueOf)
              .reduce(BigInteger :: multiply)
              .get();
   }

   /**
    * Stream method created to calculate the value of n! parallel when the n is greater than 19 (blog.emptyq.net, 2022)
    * BigInteger has been utilized for this method to handled extra large values
    * @param n: The integer value of the factorials product
    * @return returning the value of n!
    */
   public static BigInteger factorialUsingParallelStreamBigInteger(int n) {

      // returning 1 if the n is smaller than 2
      if (n < 2) { return new BigInteger(String.valueOf(1));}

      // returning the result of the parallel calculation of n!
      return IntStream.rangeClosed(2, n).parallel()
              .mapToObj(BigInteger :: valueOf)
              .reduce (BigInteger :: multiply)
              .get();
   }

   /*
      References:
      - blog.emptyq.net. (2022). Calculating factorial or the power of the Stream API - EmptyQ. [online]
      Available at: https://blog.emptyq.net/a?ID=00004-2fc2b302-bbdc-4771-9a3d-6cbd6a469a84
    */
}
