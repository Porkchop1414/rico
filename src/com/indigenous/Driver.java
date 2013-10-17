package com.indigenous;

/* PROJECT OVERVIEW:
Program Flow
1) Read in ARFF
2) Find coverings (efficiently)
3) Reduce (if needed)
4) Output rules

Initial Focus
1) ARFF Reading
2) Storage of data
3) Storage of coverings/rules

Testing
1) Demonstrate Soundness
2) small data set verification
*/

import java.io.*;

public class Driver {
  public static void main(String args[]) {
    String fileName = null;

    try {
      BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("Rule Induction From Coverings");
      System.out.println("Arff File Name (include extension): ");
      fileName = standardInput.readLine();
      // Read in the other stuff here too before processing the file, coverings, etc
      standardInput.close();
    } catch (IOException e) {
      e.printStackTrace();
    }



    // Read Arff File
    ArffData arffData = ArffReader.readArffFile(new File(fileName));

    // Find Coverings

    // Reduce Rules

    // Output Rules
  }
}
