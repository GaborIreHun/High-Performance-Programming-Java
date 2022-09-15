package com.gabor.highPerformanceProgramming.week4;//Using Recursion to calcu;ate a Fibonacci Sequence

import java.util.Scanner;

public class FibonacciCalculator{

   public static void main(String args[]) {

      int endNumber;

      Scanner s = new Scanner(System.in);
      System.out.print("Enter an integer to calculate the Fibonacci Series: ");

      endNumber = s.nextInt();
      
      System.out.print("Fibonacci Series of "+endNumber+" numbers: ");

      for(int i = 0; i < endNumber; i++){
         System.out.print(recursiveFibonacci(i) + " ");
      }

      s.close();
   }
   
   
   public static int recursiveFibonacci(int num){
      if(num == 0){
         return 0;
      }
      if(num == 1 || num == 2){
         return 1;
      }
      return recursiveFibonacci(num-2) + recursiveFibonacci(num-1);
   }
   
   
}