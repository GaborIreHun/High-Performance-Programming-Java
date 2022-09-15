
/**
 *
 * @author seank This Java application allows the experimentation with
 * Java Streams
 */
import java.util.stream.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class FunctionalStreams {

   private static TestPerson[] testPersonArray;
   protected static Random random = new Random();
   
   private static class TestPerson{
   
      private int refNumber;
      private double age;
      private double height;
      private boolean isRegistered;
   
      TestPerson(int refNumber, double age, double height, boolean isRegistered){
         this.refNumber = refNumber;
         this.age = age;
         this.height = height;
         this.isRegistered = isRegistered;   
      }
   }
     
   public static void main(String[] args) {      
   
      int arraySize = 10000000;
      testPersonArray = new TestPerson[arraySize];
   
      for (int i = 0; i < testPersonArray.length; i++) {
         
         TestPerson t = new TestPerson(i,rangedRandom(17.0, 60),rangedRandom(1.2, 2.05),true);
         
         testPersonArray[i] = t;
      }
   
      for (int executeEvent = 0; executeEvent < 10; executeEvent++) {
         System.out.printf("\nComparison Event %d\n", executeEvent + 1);
         iterateSequentially(testPersonArray);
         streamParallel(testPersonArray);
      }
   }

   private static double iterateSequentially(TestPerson[] tpArr) {
   
      long startPoint = System.nanoTime();
      
      List<TestPerson> registeredTestPersons = new ArrayList<>();
      for(TestPerson tp:tpArr){
         if(tp.isRegistered){
            registeredTestPersons.add(tp);
         } 
      } 
      
      double sumOfRegAges = 0;
      for(TestPerson tReg:registeredTestPersons){
         sumOfRegAges += tReg.age;
      }
      
      double averageRegAge = sumOfRegAges/registeredTestPersons.size();
   
      long nanoRunTime = System.nanoTime() - startPoint;
      
      printOutcome("SEQUENTIAL        ", nanoRunTime, averageRegAge);
      
      return averageRegAge;
   }

   private static double streamParallel(TestPerson[] tpArr) {
   
      long startPoint = System.nanoTime();
      
      double averageRegAge = Stream.of(tpArr)
                              .parallel()
                              .filter(tp -> tp.isRegistered)
                              .mapToDouble(ag -> ag.age)
                              .average()
                              .getAsDouble();
      
      long nanoRunTime = System.nanoTime() - startPoint;
      
      printOutcome("FUNCTIONAL_STREAM", nanoRunTime, averageRegAge);
      
      return averageRegAge;
   }



   private static void printOutcome(String label, long runTime, double sum) {
      System.out.printf(" %s process runtime was:  %8.3f milliseconds with final sum as: %8.5f \n", label, runTime / 1e6, sum);
   }
   
   public static double rangedRandom(double mn, double mx) {
      double spread = mx - mn;
      double scaled = random.nextDouble() * spread;
      double baseShift = Math.round((scaled + mn) * 100.0) / 100.0;
      return baseShift; 
   }

}



