import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class DecibinaryUnit {
    long decimal;
    long startOrder;
    long startNumber;
    int minLen;
    int maxLen;
    
    Hashtable<Integer, Long> countByLenSum;
}

public class DecibinaryNumbers {
    static ArrayList<DecibinaryUnit> dbUnitEvenList = new ArrayList<DecibinaryUnit>();

    // Complete the decibinaryNumbers function below.
     static long decibinaryNumbers(long x) {
        if (dbUnitEvenList.size() == 0) {
            DecibinaryUnit dbUnit0 = new DecibinaryUnit();
            dbUnit0.decimal = 0L;
            dbUnit0.startOrder = 1L;
            dbUnit0.startNumber = 0L;
            dbUnit0.minLen = 1;
            dbUnit0.maxLen = 1;
            dbUnit0.countByLenSum = new Hashtable<Integer, Long>();
            dbUnit0.countByLenSum.put(1, 1L);
              
            dbUnitEvenList.add(dbUnit0);
        }

        int unitIndex = dbUnitEvenList.size() - 1;
        DecibinaryUnit dbUnit = dbUnitEvenList.get(unitIndex);
        long sameCountInUnit = dbUnit.countByLenSum.get(dbUnit.maxLen);
        long currentOrder = dbUnit.startOrder + sameCountInUnit * 2L - 1L;
        
        if (currentOrder < x) {            
            int newIndex = unitIndex + 1;
            long newDecimal = dbUnit.decimal + 2L;
            long newStartNumber = getNextDecibinary(dbUnit.startNumber, 2);

            while (currentOrder < x) {
                DecibinaryUnit dbUnitNew = new DecibinaryUnit();
                dbUnitNew.decimal = newDecimal;
                dbUnitNew.startOrder = currentOrder + 1;
                dbUnitNew.startNumber = newStartNumber;
                dbUnitNew.countByLenSum = new Hashtable<Integer, Long>();                
                dbUnitNew.minLen = (int)Math.log10(dbUnitNew.startNumber) + 1;
                dbUnitNew.maxLen = log2(dbUnitNew.decimal) + 1;

                for (int i = dbUnitNew.minLen; i <= dbUnitNew.maxLen; i++) {
                    if (i == 1) {
                        dbUnitNew.countByLenSum.put(i, 1L);
                        continue;
                    } 
                    
                    if (i > dbUnitNew.minLen) {
                        dbUnitNew.countByLenSum.put(i, dbUnitNew.countByLenSum.get(i - 1));
                    }
                    
                    long pow10 = (long)Math.pow(10, i - 1);
                    int pow2 = (int)Math.pow(2, i - 1);
                    int startDigit = i == dbUnitNew.minLen ? (int)((dbUnitNew.startNumber / pow10) % 10L) : 1;
                    int endDigit = (int)dbUnitNew.decimal / pow2;
                                     
                    if (startDigit != 1 && endDigit > 9) {                       
                        endDigit = 9;
                        
                        long count = 0L;
                        for (int j = startDigit; j <= endDigit; j++) {
                            long adjustedDecimal = dbUnitNew.decimal - j * pow2;                        
                            
                            if (adjustedDecimal == 0L) {
                                count += 1L;
                            }
                            else {
                                DecibinaryUnit dbUnitAdjusted = dbUnitEvenList.get((int)(adjustedDecimal / 2L));
                                int possibleMaxLen = Math.min(i - 1, dbUnitAdjusted.maxLen);
                                   
                                count += dbUnitAdjusted.countByLenSum.get(possibleMaxLen);                              
                            }
                        }
                                              
                        if (dbUnitNew.countByLenSum.containsKey(i)) {
                            dbUnitNew.countByLenSum.put(i, dbUnitNew.countByLenSum.get(i) + count);
                        }
                        else {
                            dbUnitNew.countByLenSum.put(i, count);
                        }          
                    }
                    else if (i == dbUnitNew.maxLen && isPowerOf2(dbUnitNew.decimal)) {
                        dbUnitNew.countByLenSum.put(i, dbUnitNew.countByLenSum.get(i) + 1L);
                    }
                    else {
                        long adjustedDecimal = dbUnitNew.decimal - pow2;                        
                        long count = 0L;   
            
                        if (adjustedDecimal == 0L) {
                            count = 1L;
                        }
                        else {
                            DecibinaryUnit dbUnitAdjusted = dbUnitEvenList.get((int)(adjustedDecimal / 2L));
                            int possibleMaxLen = Math.min(i, dbUnitAdjusted.maxLen);
                            
                            count = dbUnitAdjusted.countByLenSum.get(possibleMaxLen);                                
                        }

                        if (dbUnitNew.countByLenSum.containsKey(i)) {
                            dbUnitNew.countByLenSum.put(i, dbUnitNew.countByLenSum.get(i) + count);
                        }
                        else {
                            dbUnitNew.countByLenSum.put(i, count);
                        }
                    }
                }           
                dbUnitEvenList.add(dbUnitNew);

                sameCountInUnit = dbUnitNew.countByLenSum.get(dbUnitNew.maxLen); 
                currentOrder += (sameCountInUnit * 2L);
                newIndex++;
                newDecimal += 2L;
                newStartNumber = getNextDecibinary(newStartNumber, 2);
                dbUnit = dbUnitNew;
            }
            unitIndex = newIndex - 1;
        }
        else {
            unitIndex = binarySearch(dbUnitEvenList, 0, dbUnitEvenList.size() - 1, x);
            dbUnit = dbUnitEvenList.get(unitIndex);
            sameCountInUnit =  dbUnit.countByLenSum.get(dbUnit.maxLen);
            currentOrder = dbUnit.startOrder + sameCountInUnit * 2L - 1L;
        }
        
        long orderInTarget = x - (dbUnit.startOrder - 1L);
        boolean isOdd = false;

        if (orderInTarget > sameCountInUnit) {
            orderInTarget -= sameCountInUnit;
            isOdd = true;
        }  
        
        if (isOdd) { 
            return getNthDecibinaryInUnit(dbUnit, orderInTarget) + 1L;
        }
        else {
            return getNthDecibinaryInUnit(dbUnit, orderInTarget);
        }
    }   
  
    static long getNthDecibinaryInUnit (DecibinaryUnit dbUnit, long n) {
        if (n == 1) {
            return dbUnit.startNumber;
        }
        else {
            long order = 0L;
            long adjustedDecimal = 0L;
            long addition = 0L;
            long prevCountInLen = 0L;
            long countInLen = 0L;
            
            outerloop:            
            for (int i = dbUnit.minLen; i <= dbUnit.maxLen; i++) {
                if (dbUnit.countByLenSum.containsKey(i)) {  
                    if (i > dbUnit.minLen) {
                        prevCountInLen = countInLen;
                    }                  
                    countInLen = dbUnit.countByLenSum.get(i);                
                             
                    if (countInLen >= n) {
                        long pow10 = (long)Math.pow(10, i - 1);
                        int pow2 = (int)Math.pow(2, i - 1);
                        int startDigit = i == dbUnit.minLen ? (int)((dbUnit.startNumber / pow10) % 10L) : 1;                                                     
                        int endDigit = Math.min(9, (int)dbUnit.decimal / pow2);
                        long count = 0L;
                        long countSumInLen = 0L;

                        n -= prevCountInLen; 
                        for (int j = startDigit; j <= endDigit; j++) {
                            adjustedDecimal = dbUnit.decimal - j * pow2;                               
                            count = 0L;
                                          
                            if (adjustedDecimal == 0L) {
                                count = 1L;
                            }
                            else {
                                DecibinaryUnit dbUnitAdjusted = dbUnitEvenList.get((int)(adjustedDecimal / 2L));    
                                int possibleMaxLen = Math.min(i - 1, dbUnitAdjusted.maxLen);
                                        
                                count = dbUnitAdjusted.countByLenSum.get(possibleMaxLen);
                            }
                                         
                            if ((countSumInLen + count) >= n) {
                                order = n - countSumInLen;
                                   addition = j * pow10;
                                break outerloop;
                            }
                            else {
                                countSumInLen += count;
                            }
                        }
                    }
                }
            }         
                
            return addition + getNthDecibinaryInUnit(dbUnitEvenList.get((int)(adjustedDecimal / 2L)), order);            
        }
    }
    
    static long getNextDecibinary (long number, long addition) {
        long result = number;
        int digit = (int)(result % 10L);     
        
        if (digit == 8) {
            long pow10 = 10L;
            int digitHigher = (int)((number / pow10) % 10L);  
            
            while (digitHigher == 9) {
                result -= pow10;             
                pow10 *= 10L;
                digitHigher = (int)((number / pow10) % 10L);
            }
            
            result = result + pow10;
        }
        else {
            result = result + 2L;
        }
        
        return result;
    }
    
    static boolean isPowerOf2 (long number) {
        return number != 0L && (number & (number - 1L)) == 0L;
    }
    
    static int log2(long n) {
        return (int)(Math.log(n) / Math.log(2));
    }
    
    static int binarySearch(ArrayList<DecibinaryUnit> dbUnitEvenList, int l, int r, long target) {
        int index = -1; 

        if (l <= r) {
            int m = (l + r) / 2;
            DecibinaryUnit dbUnit = dbUnitEvenList.get(m);
            long endOrder = dbUnit.startOrder + dbUnit.countByLenSum.get(dbUnit.maxLen) * 2L - 1L;

            if (target >= dbUnit.startOrder && target <= endOrder) {
                index = m;
            }
            else if (dbUnit.startOrder > target) {
                index = binarySearch(dbUnitEvenList, l, m - 1, target);
            }
            else {
                index = binarySearch(dbUnitEvenList, m + 1, r, target);
            }
        }

        return index;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int qItr = 0; qItr < q; qItr++) {
            long x = scanner.nextLong();
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            long result = decibinaryNumbers(x);

            bufferedWriter.write(String.valueOf(result));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();

        scanner.close();
    }
}
