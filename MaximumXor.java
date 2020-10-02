import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;


/*
Problem description
https://www.hackerrank.com/challenges/maximum-xor/problem?h_l=interview&playlist_slugs%5B%5D=interview-preparation-kit&playlist_slugs%5B%5D=miscellaneous
*/


public class MaximumXor {

    // Complete the maxXor function below.
    static int[] maxXor(int[] arr, int[] queries) {
        // solve here
        int[] maxXORArray = new int[queries.length];
        List<Hashtable<String, Integer>> hexHashList = new ArrayList<Hashtable<String, Integer>>();
        int maxAvail = 0;
        int minAvail = Integer.MAX_VALUE;
        
        for (int i = 0; i < 8; i++) {
            Hashtable<String, Integer> hexHash = new Hashtable<String, Integer>();    
            hexHashList.add(hexHash);
        }
        
        for (int i = 0; i < arr.length; i++) {
            String hexString = Integer.toHexString(arr[i]);

            for (int j = 0; j < hexString.length(); j++) {
                String hexFromMSD = hexString.substring(0, j + 1);
                Hashtable<String, Integer> hexHash = hexHashList.get(hexString.length() - j - 1);
                
                if (!hexHash.containsKey(hexFromMSD)) {
                    hexHash.put(hexFromMSD, 1);
                }
                else {
                    hexHash.put(hexFromMSD, hexHash.get(hexFromMSD) + 1);
                }
            }
            minAvail = Math.min(minAvail, arr[i]);
            maxAvail = Math.max(maxAvail, arr[i]);
        }
        
        for (int i = 0; i < queries.length; i++) {
            int queryXOR = flippingBits(queries[i]);
            int queryXORMaxPowerOf2 = log2(queryXOR);
            int queryMaxPowerOf2 = log2(queries[i]);
            int maxPossible = 0;
            StringBuilder actualMaxSB = new StringBuilder();
                           
            if (queryMaxPowerOf2 < 30 && maxAvail > (queries[i] + queryXOR)) {           
                maxPossible = clearBits(maxAvail, queryMaxPowerOf2 + 1) + queryXOR;
            }
            else if (queryXOR > maxAvail) {
                maxPossible = queryXOR & maxAvail;
            }
            else if (log2(minAvail) > queryXORMaxPowerOf2) {
                maxPossible = clearBits(minAvail, queryXORMaxPowerOf2) + queryXOR; 
            }
            else {
                maxPossible = queryXOR;
            }

            String maxPossibleHex = Integer.toHexString(maxPossible);
            
            for (int j = 0; j < maxPossibleHex.length(); j++) {
                String hex = maxPossibleHex.substring(j, j + 1);
                Hashtable<String, Integer> hexHash = hexHashList.get(maxPossibleHex.length() - j - 1);              
                int order = 0;
                
                while (order <= 15) {                   
                    String maxHex = Integer.toHexString((Integer.parseInt(hex, 16) ^ order)); 
                    String hexFromMSD = j == 0 ? maxHex : actualMaxSB.toString() + maxHex;
                    
                    if (hexHash.containsKey(hexFromMSD)) {
                        actualMaxSB.append(maxHex);
                        break;
                    }
                    else {
                        order++;
                    }
                }
            }

            maxXORArray[i] = Integer.parseInt(actualMaxSB.toString(), 16) ^ queries[i];                
        }        
       
        return maxXORArray;
    }

    static int flippingBits(int n) {
        int nextPowerOf2 = log2(n) + 1;
        
        for (int i = 0; i < nextPowerOf2; i++) {
            n = n ^ (1 << i);
        }

        return n;
    }

    static int log2(int n) {
        return (int)(Math.log(n) / Math.log(2));
    }

    static int clearBits(int n, int msb) {
        int mask = 0;
        
        for (int i = msb; i < 31; i++) {
            mask |= (1 << i);    
        }   
        
        return (n & mask);
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] arr = new int[n];

        String[] arrItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        int m = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] queries = new int[m];

        for (int i = 0; i < m; i++) {
            int queriesItem = scanner.nextInt();
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
            queries[i] = queriesItem;
        }

        int[] result = maxXor(arr, queries);

        for (int i = 0; i < result.length; i++) {
            bufferedWriter.write(String.valueOf(result[i]));

            if (i != result.length - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
