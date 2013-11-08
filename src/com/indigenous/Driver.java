package com.indigenous;

import java.io.*;
import java.util.*;

public class Driver {
  public static void main(String args[]) {
    try {
      BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("###################################################################");
      System.out.println("################## Rule Induction From Coverings ##################");
      System.out.println("###################################################################");
      System.out.println("Arff File Name (include extension): ");
      String fileName = standardInput.readLine();
      DataSet dataSet = ArffReader.readArffFile(new File(fileName));

      List<Attribute> decisionAttributes = readDecisionAttribute(standardInput, dataSet);
      int maxNumberAttributes = readNumberAttributes(standardInput, dataSet);
      int minRuleCoverage = readMinRuleCoverage(standardInput, dataSet);
      boolean dropConditions = readDropConditions(standardInput);

      standardInput.close();

      // Find Coverings
      Covering decisionCovering = new Covering(decisionAttributes);
      List<Attribute> nonDecisionAttributes = getNonDecisionAttributes(dataSet, decisionAttributes);
      List<Attribute> coveringAttributes = new LinkedList<Attribute>();

      // TODO: Expand this to consider more than one attribute for a covering (all combinations)
      List<Covering> coverings = new LinkedList<Covering>();
      Iterator<Attribute> iterator = nonDecisionAttributes.iterator();
      while (iterator.hasNext() && !nonDecisionAttributes.isEmpty()) {
        Attribute a = iterator.next();
        coveringAttributes.clear();
        coveringAttributes.add(a);
        Covering temp = new Covering(coveringAttributes);
        if(temp.isValidCovering(decisionCovering)) {
          coverings.add(temp);
          iterator.remove();
        }
      }

      // Drop unnecessary conditions from rules
      if(dropConditions) {

      }

      // Output Rules
      System.out.println("\nRelation Name: " + dataSet.getName());
      System.out.println("\nDecision attributes: [" + decisionCovering.getAttributeNames() + "]");
      for(Attribute a : decisionAttributes) {
        System.out.println("\nDistribution of values for attribute " + a.getName() + ":");
        for(String s : a.getPossibleValues()) {
          System.out.println("\tValue: " + s + "\tOccurrences: " + a.getValueCount(s));
        }
      }

      if(decisionAttributes.size() > 1) {
        System.out.println("\nDistribution of values for attributes "  + decisionCovering.getAttributeNames() + ":");
        List<Integer> coverages = decisionCovering.getPossibleValueCoverages();
        List<String> coverageValues = decisionCovering.getPossibleValues();
        for(int i = 0; i < coverages.size() && i < coverageValues.size(); i++) {
          String[] values = coverageValues.get(i).split("\\s+");
          System.out.print("\tValue: ");
          for(String value : values) {
            System.out.print(value + " ");
          }
          System.out.println("\tOccurrences: " + coverages.get(i));
        }
      }

      for(Covering c : coverings) {
        System.out.println("\n" + c.getRules(decisionCovering, minRuleCoverage));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static public List<Attribute> readDecisionAttribute(BufferedReader standardInput, DataSet dataSet) throws IOException {
    List<Attribute> decisionAttributes = new ArrayList<Attribute>();
    System.out.println("Enter the numbers of the desired decision attributes (space delimited): ");
    while(decisionAttributes.size() == 0) {
      dataSet.printAttributeList();
      try {
        String[] values = standardInput.readLine().split("\\s+");
        for(String s : values) {
          if(Integer.parseInt(s) > dataSet.getNumberAttributes() - 1 || Integer.parseInt(s) < 0) {
            decisionAttributes.clear();
            System.out.println("Please select valid attributes: ");
            break;
          }
          decisionAttributes.add(dataSet.getAttribute((Integer.parseInt(s))));
        }
      } catch(NumberFormatException e) {
        decisionAttributes.clear();
        System.out.println("Please select valid attributes: ");
      }
    }
    return decisionAttributes;
  }

  static public int readNumberAttributes(BufferedReader standardInput, DataSet dataSet) throws IOException {
    int maxAttributes = -1;
    System.out.println("Maximum number of attributes in covering: ");
    while (maxAttributes == -1) {
      try {
        maxAttributes = Integer.parseInt(standardInput.readLine());
        if(maxAttributes < 1 || maxAttributes > dataSet.getNumberAttributes() - 1) {
          maxAttributes = -1;
          System.out.println("Please enter a valid maximum number of attributes covering: ");
        }
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number: ");
      }
    }
    return maxAttributes;
  }

  static public int readMinRuleCoverage(BufferedReader standardInput, DataSet dataSet) throws IOException {
    int minCoverage = -1;
    System.out.println("Minimum coverage for a rule: ");
    while (minCoverage == -1) {
      try {
        minCoverage = Integer.parseInt(standardInput.readLine());
        if(minCoverage < 1 || minCoverage > dataSet.getNumberDataRows()) {
          minCoverage = -1;
          System.out.println("Please enter a valid minimum coverage: ");
        }
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number: ");
      }
    }
    return minCoverage;
  }

  static public boolean readDropConditions(BufferedReader standardInput) throws IOException {
    System.out.println("Drop unnecessary conditions (y/n): ");
    String input = standardInput.readLine();
    return (input.charAt(0) == 'y' || input.charAt(0) == 'Y');
  }

  static public List<Attribute> getNonDecisionAttributes(DataSet dataSet, List<Attribute> decisionAttributes) {
    List<Attribute> nonDecisionAttributes = new ArrayList<Attribute>();
    for(int i = 0; i < dataSet.getNumberAttributes(); i++) {
      if(!decisionAttributes.contains(dataSet.getAttribute(i))) {
        nonDecisionAttributes.add(dataSet.getAttribute(i));
      }
    }
    return nonDecisionAttributes;
  }
}
