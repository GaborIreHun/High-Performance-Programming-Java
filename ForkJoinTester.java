package com.gabor.highPerformanceProgramming.week4;

/**
 * @author seank & GaborIreHun This Java application allows the experimentation with the Java
 * Fork Join Framework
 * Modified in accordance with Session Labs 4 by Gabor Sebestyen
 * The original code was modified from creating random numbers to create the first 100000 prime numbers,
 * then add these together by modifying both sequential and parallel methods.
 * I have created an additional two paralle methods to compare the time efficiency of the different approaches.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;


public class ForkJoinTester {

    private static int[] testArray;
    private static String systemProperty = "java.util.concurrent.ForkJoinPool.common.parallelism";

    public static void main(String[] args) throws InterruptedException {

        // declaring variable for the size of array, and to aid the determination of prime numbers
        int arraySizePlusOne = 100001, num = 3;

        // declaring variable that assigning true value to prime numbers
        boolean isPrime = true;

        // declaring arraylist to store prime numbers initially
        ArrayList<Integer> primeNumbers = new ArrayList<>();

        // adding 2 to the arraylist as the first prime number
        primeNumbers.add(2);

        // Creating prime numbers - (beginnersbook.com, 2014)
        for (int i = 2 ; i < arraySizePlusOne ;){

            for (int j = 2 ; j <= Math.sqrt(num) ; j++ ){

                if (num%j == 0){
                    isPrime = false;
                    break;
                }
            }
            if (isPrime != false){
                primeNumbers.add(num);
                i++;
            }
            isPrime = true;
            num++;
        }


        // adding the values of primeNumbers ArrayList to testArray
        testArray = primeNumbers.stream().mapToInt(i -> i).toArray();


        System.out.println("The size of testArray is: " + testArray.length);
        //System.out.println("The size of testArray2 is: " + testArray2.length);

        /*
            Session Labs 4(e) - explain the importance of line 30:
            Originally the below calling of setProperty method was at line 30.
            The below method is, to modify the already set system properties.
            It is replacing the whole set of system properties with the new represented set.
            There are two parameters, the first is the key, and the second is the value.
            Our key here is java.util.concurrent.ForkJoinPool.common.parallelism,
            that allow us the setting of ForkJoinPool API (Oracle.com, 2021).
            It is a more efficient way, then to directly program it. ForkJoinPool is utilized
            most efficiently with programs, that would not match with the parallel streams model.
            A common fork-join pool is shared between the parallel streams in the process.
            The utilization of the resource is aided to be optimized, by knowing what cores within
            the process are used globally (Schmidt, 2020).
            By-default the level of parallelism is equal with the number of available processors,
            however it may be constructed with a given target parallelism level(docs.oracle.com, n.d.).
         */
        System.setProperty(systemProperty, "1000");

        for (int executeEvent = 0; executeEvent < 10; executeEvent++) {
            System.out.printf("\nComparison Event %d\n", executeEvent + 1);
            arraySumSequential(testArray);
            arraySumParallel(testArray);
            arraySumParallelStream(testArray);
            arraySumQuadruple(testArray);
        }
    }

    private static int arraySumSequential(int[] arr) {

        long startPoint = System.nanoTime();
        int arraySum = 0;

        for (int i = 0; i < arr.length; i++) {
            arraySum += arr[i];
        }

        long nanoRunTime = System.nanoTime() - startPoint;
        printOutcome("SEQUENTIAL           ", nanoRunTime, arraySum);
        return arraySum;
    }

    private static int arraySumParallel(int[] arr) {

        long startPoint = System.nanoTime();
        ArraySum s = new ArraySum(arr, 0, arr.length);
        ForkJoinPool.commonPool().invoke(s);
        int sumFromArraySum = s.answer;
        long nanoRunTime = System.nanoTime() - startPoint;
        printOutcome("PARALLEL_FORK_JOIN   ", nanoRunTime, sumFromArraySum);
        return sumFromArraySum;
    }

    /**
     * Method to calculate the sum of the arr array's elements using Java Stream's parallel approach
     * @param arr: the input array, what elements are intended to be summed
     * @return: returning the sum of the elements in the input array
     */
    private static int arraySumParallelStream(int[] arr) {
        // START our 'stopwatch' to record the duration of the PARALLEL calculation
        long startPoint = System.nanoTime();

        // Utilizing the parallel approach of the Java Stream to assign the sum of the input array's
        // elements to an int variable, to store our result.
        int arraySummary = Arrays.stream(arr).parallel().sum();

        // STOP our 'stopwatch' to record the duration of the calculation
        long nanoRunTime = System.nanoTime() - startPoint;

        // Use the method to print the results for the PARALLEL_STREAM calculation
        printOutcome("PARALLEL_STREAM      ", nanoRunTime, arraySummary);
        return arraySummary;
    }

    private static int arraySumQuadruple(int[] arr) throws InterruptedException {

        // START our 'stopwatch' to record the duration of the PARALLEL calculation
        long startPoint = System.nanoTime();

        // Created four independent array sum AtomicInteger (first, second, third and fourth quarter)
        // AtomicInteger is a thread-safe method when we attempt to update integer value in the threads.
        AtomicInteger sum1 = new AtomicInteger();
        AtomicInteger sum2 = new AtomicInteger();
        AtomicInteger sum3 = new AtomicInteger();
        AtomicInteger sum4 = new AtomicInteger();

        /*  Created a thread to calculate the sum of the first quarter of the arr array
            Java Stream has been utilized and the appropriate range was given
         */
        Thread firstQuarterOfArray = new Thread(
                () -> { sum1.set(Arrays.stream(arr, 0, arr.length / 4).sum());
                });
        firstQuarterOfArray.start();

        /*  Created a thread to calculate the sum of the second quarter of the arr array
            Java Stream has been utilized and the appropriate range was given
         */
        Thread secondQuarterOfArray = new Thread(
                () -> { sum2.set(Arrays.stream(arr, arr.length / 4, arr.length / 2).sum());
                });
        secondQuarterOfArray.start();


        /*  Created a thread to calculate the sum of the third quarter of the arr array
            Java Stream has been utilized and the appropriate range was given
         */
        Thread thirdQuarterOfArray = new Thread(
                () -> { sum3.set(Arrays.stream(arr, arr.length / 2, arr.length - arr.length / 4).sum());
                });
        thirdQuarterOfArray.start();

        /*  Created a thread to calculate the sum of the fourth quarter of the arr array
            Java Stream has been utilized and the appropriate range was given
         */
        Thread fourthQuarterOfArray = new Thread(
                () -> { sum4.set(Arrays.stream(arr, arr.length - arr.length / 4, arr.length).sum());
                });
        fourthQuarterOfArray.start();

        // Ensure that the threads complete
        firstQuarterOfArray.join();
        secondQuarterOfArray.join();
        thirdQuarterOfArray.join();
        fourthQuarterOfArray.join();

        // Calculate the total sum from the result of each thread
        int finalSum = sum1.intValue() + sum2.intValue() + sum3.intValue() + sum4.intValue();

        // STOP our 'stopwatch' to record the duration of the calculation
        long nanoRunTime = System.nanoTime() - startPoint;

        // Use the method to print the results for the QUADRUPLE_WITH_STREAM calculation
        printOutcome("QUADRUPLE_WITH_STREAM", nanoRunTime, finalSum);
        return finalSum;
    }


    private static class ArraySum extends RecursiveAction {

        /*
            Session Labs 4(e) - explain the importance of line 66:
            Originally the below calling of setProperty method was at line 66.
            Setting the value of below sequential threshold as variable will be utilized in the compute() method
            at line 127.
            The THRESHOLD_SEQUENTIAL value refers to the number of elements, when the method should start to
            process sequentially. If the number of elements are higher than the referred value, it will process parallel
            (www.demo2s.com, n.d.).
         */
        static int THRESHOLD_SEQUENTIAL = 100000;
        int high;
        int low;
        int[] arr;
        int answer = 0;

        ArraySum(int[] a, int l, int h) {
            arr = a;
            high = h;
            low = l;
        }

        // We must override the abstract method compute()- - the main
        // computation performed by the task
        protected void compute() {
            if (high - low <= THRESHOLD_SEQUENTIAL) {
                for (int i = low; i < high; i++) {
                    answer += arr[i];
                }
            } else {
                ArraySum left = new ArraySum(arr, low, (high + low) / 2);
                ArraySum right = new ArraySum(arr, (high + low) / 2, high);
                left.fork();
                right.compute();
                left.join();

                answer = left.answer + right.answer;
            }
        }
    }

    static void printOutcome(String label, long runTime, double sum) {
        System.out.printf(" %s process runtime was:  %8.3f milliseconds with final sum as: %8.5f \n", label, runTime / 1e6, sum);
    }

    /*
       References

       - beginnersbook.com. (2014). Java Program to display first n or first 100 prime numbers.[online]
       Available at: https://beginnersbook.com/2014/01/java-program-to-display-first-n-or-first-100-prime-numbers/
       - docs.oracle.com. (n.d.). ForkJoinPool (Java Platform SE 8 ). [online]
       Available at: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html#commonPool--
       - Oracle.com. (2021). System Properties (The JavaTM Tutorials > Essential Java Classes >
       The Platform Environment). [online]
       Available at: https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
       - Schmidt, D. (2020). Understand Java Parallel Streams Internals:
       Parallel Processing w/the Common Fork-Join Pool. [online]
       Available at: http://www.dre.vanderbilt.edu/~schmidt/cs891f/2020-PDFs/
       7.2.3-parallelstream-internals-parallel-processing-via-CFJP.pdf
       - www.demo2s.com. (n.d.). Java Fork/Join Framework set Level of Parallelism. [online]
       Available at: https://www.demo2s.com/java/java-fork-join-framework-set-level-of-parallelism.html
     */

}
