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
    DataSet dataSet = null;
    String fileName = null;
    Attribute decisionAttribute = null;
    int maxNumberAttributes = -1;
    int minRuleCoverage = -1;

    try {
      BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("###################################################################");
      System.out.println("################## Rule Induction From Coverings ##################");
      System.out.println("###################################################################");
      System.out.println("Arff File Name (include extension): ");
      fileName = standardInput.readLine();
      dataSet = ArffReader.readArffFile(new File(fileName));

      System.out.println("Enter the number of the decision attribute: ");
      while(decisionAttribute == null) {
        dataSet.printAttributeList();
        int attributeIndex = Integer.parseInt(standardInput.readLine());
        try {
          decisionAttribute = dataSet.getAttribute(attributeIndex);
        } catch(IndexOutOfBoundsException e) {
          System.out.println("Please select a valid attribute: ");
        }
      }

      System.out.println("Maximum number of attributes in covering: ");
      while (maxNumberAttributes == -1) {
        try {
          maxNumberAttributes = Integer.parseInt(standardInput.readLine());
          if(maxNumberAttributes < 1 || maxNumberAttributes > dataSet.getAttributesSize() - 1) {
            maxNumberAttributes = -1;
            System.out.println("Please enter a valid maximum number of attributes covering: ");
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter a valid number: ");
        }
      }

      System.out.println("Minimum coverage for a rule: ");
      while (minRuleCoverage == -1) {
        try {
          minRuleCoverage = Integer.parseInt(standardInput.readLine());
          if(minRuleCoverage < 1 || minRuleCoverage > dataSet.getDataSize()) {
            minRuleCoverage = -1;
            System.out.println("Please enter a valid minimum coverage: ");
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter a valid number: ");
        }
      }

      standardInput.close();

      // Find Coverings

      // Reduce Rules

      // Output Rules

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
