package com.indigenous;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Driver {
  static List<Covering> validCoverings;
  static Covering decisionCovering;
  static Set<Attribute> usedAttributes;

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
      int maxNumberAttributes = readNumberAttributes(standardInput, dataSet, decisionAttributes.size());
      int minRuleCoverage = readMinRuleCoverage(standardInput, dataSet);
      boolean dropConditions = readDropConditions(standardInput);

      standardInput.close();

      // Find Coverings
      decisionCovering = new Covering(decisionAttributes);
      List<Attribute> nonDecisionAttributes = getNonDecisionAttributes(dataSet, decisionAttributes);
      validCoverings = new ArrayList<Covering>();
      usedAttributes = new HashSet<Attribute>();

      for (int i = 1; i <= maxNumberAttributes && i <= nonDecisionAttributes.size(); i++) {
        processSubsets(nonDecisionAttributes, i);
        for (Attribute attribute : usedAttributes) {
          nonDecisionAttributes.remove(attribute);
        }
      }

      List<RuleSet> ruleSets = new ArrayList<RuleSet>();
      for(Covering c : validCoverings) {
        ruleSets.add(c.getRuleSet(decisionCovering));
      }

      // Drop unnecessary conditions from rules
      if (dropConditions) {
        for (RuleSet r : ruleSets) {
          r.reduce();
        }
      }

      // Output RuleSet
      System.out.println("\nRelation Name: " + dataSet.getName());
      System.out.println("\nDecision attributes: [" + decisionCovering.getAttributeNames() + "]");
      for (Attribute a : decisionAttributes) {
        System.out.println("\nDistribution of values for attribute " + a.getName() + ":");
        for (String s : a.getPossibleValues()) {
          System.out.println("\tValue: " + s + "\tOccurrences: " + a.getValueCount(s));
        }
      }

      if (decisionAttributes.size() > 1) {
        System.out.println("\nDistribution of values for attributes " + decisionCovering.getAttributeNames() + ":");
        List<Integer> coverages = decisionCovering.getPossibleValueCoverages();
        List<String> coverageValues = decisionCovering.getPossibleValues();
        for (int i = 0; i < coverages.size() && i < coverageValues.size(); i++) {
          String[] values = coverageValues.get(i).split("\\s+");
          System.out.print("\tValue: ");
          for (String value : values) {
            System.out.print(value + " ");
          }
          System.out.println("\tOccurrences: " + coverages.get(i));
        }
      }

      for (RuleSet r : ruleSets) {
        System.out.println("\n" + r.toString(minRuleCoverage));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static public List<Attribute> readDecisionAttribute(BufferedReader standardInput, DataSet dataSet) throws IOException {
    List<Attribute> decisionAttributes = new ArrayList<Attribute>();
    System.out.println("Enter the numbers of the desired decision attributes (space delimited): ");
    while (decisionAttributes.size() == 0) {
      dataSet.printAttributeList();
      try {
        String[] values = standardInput.readLine().split("\\s+");
        for (String s : values) {
          if (Integer.parseInt(s) > dataSet.getNumberAttributes() - 1 || Integer.parseInt(s) < 0) {
            decisionAttributes.clear();
            System.out.println("Please select valid attributes: ");
            break;
          }
          decisionAttributes.add(dataSet.getAttribute((Integer.parseInt(s))));
        }
      } catch (NumberFormatException e) {
        decisionAttributes.clear();
        System.out.println("Please select valid attributes: ");
      }
    }
    return decisionAttributes;
  }

  static public int readNumberAttributes(BufferedReader standardInput, DataSet dataSet, int numberDecisionAttributes) throws IOException {
    int maxAttributes = -1;
    System.out.println("Maximum number of attributes in covering: ");
    while (maxAttributes == -1) {
      try {
        maxAttributes = Integer.parseInt(standardInput.readLine());
        if (maxAttributes < 1 || maxAttributes > dataSet.getNumberAttributes() - numberDecisionAttributes) {
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
        if (minCoverage < 1 || minCoverage > dataSet.getNumberDataRows()) {
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
    for (int i = 0; i < dataSet.getNumberAttributes(); i++) {
      if (!decisionAttributes.contains(dataSet.getAttribute(i))) {
        nonDecisionAttributes.add(dataSet.getAttribute(i));
      }
    }
    return nonDecisionAttributes;
  }

  static void processSubsets(List<Attribute> set, int k) {
    Attribute[] subset = new Attribute[k];
    processLargerSubsets(set, subset, 0, 0);
  }

  static void processLargerSubsets(List<Attribute> set, Attribute[] subset, int subsetSize, int nextIndex) {
    if (subsetSize == subset.length) {
      Covering temp = new Covering(subset);
      if (temp.isValidCovering(decisionCovering)) {
        validCoverings.add(temp);
        Collections.addAll(usedAttributes, subset);
      }
    } else {
      for (int j = nextIndex; j < set.size(); j++) {
        subset[subsetSize] = set.get(j);
        processLargerSubsets(set, subset, subsetSize + 1, j + 1);
      }
    }
  }
}
