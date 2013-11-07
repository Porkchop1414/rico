package com.indigenous;

import java.io.*;
import java.util.*;

public class Driver {
  public static void main(String args[]) {
    List<Covering> coverings = new LinkedList<Covering>();
    DataSet dataSet = null;
    Attribute decisionAttribute = null;
    int decisionAttributeIndex = -1;
    int maxNumberAttributes = -1;
    int minRuleCoverage = -1;

    try {
      BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("###################################################################");
      System.out.println("################## Rule Induction From Coverings ##################");
      System.out.println("###################################################################");
      System.out.println("Arff File Name (include extension): ");
      String fileName = standardInput.readLine();
      dataSet = ArffReader.readArffFile(new File(fileName));

      System.out.println("Enter the number of the decision attribute: ");
      while(decisionAttribute == null) {
        dataSet.printAttributeList();
        decisionAttributeIndex = Integer.parseInt(standardInput.readLine());
        try {
          decisionAttribute = dataSet.getAttribute(decisionAttributeIndex);
        } catch(IndexOutOfBoundsException e) {
          System.out.println("Please select a valid attribute: ");
        }
      }

      System.out.println("Maximum number of attributes in covering: ");
      while (maxNumberAttributes == -1) {
        try {
          maxNumberAttributes = Integer.parseInt(standardInput.readLine());
          if(maxNumberAttributes < 1 || maxNumberAttributes > dataSet.getNumberAttributes() - 1) {
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
          if(minRuleCoverage < 1 || minRuleCoverage > dataSet.getNumberDataRows()) {
            minRuleCoverage = -1;
            System.out.println("Please enter a valid minimum coverage: ");
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter a valid number: ");
        }
      }

      standardInput.close();

      // Find Coverings
      List<Attribute> coveringAttributes = new LinkedList<Attribute>();
      coveringAttributes.add(decisionAttribute);
      Covering decisionCovering = new Covering(coveringAttributes);

      List<Attribute> nonDecisionAttributes = new ArrayList<Attribute>();
      for(int i = 0; i < dataSet.getNumberAttributes(); i++) {
        if(i != decisionAttributeIndex) {
          nonDecisionAttributes.add(dataSet.getAttribute(i));
        }
      }

      // TODO: Expand this to consider more than one attribute for a covering (all combinations)
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

      // Reduce Rules

      // Output Rules
      System.out.println("Relation Name: " + dataSet.getName());
      System.out.println("Decision attributes: [" + decisionAttribute.getName() + "]");
      System.out.println("Distribution of values for attribute " + decisionAttribute.getName() + ":");
      for(String s : decisionAttribute.getPossibleValues()) {
        System.out.println("\tValue: " + s + "\tOccurrences: " + decisionAttribute.getValueCount(s));
      }
      for(Covering c : coverings) {
        System.out.print("Rules for covering [");
        List<String> names = c.getAttributeNames();
        for(int i = 0; i < names.size(); i++) {
          System.out.print(names.get(i));
          if(i < names.size() - 1) {
            System.out.print(", ");
          }
        }
        System.out.println("]:");
        System.out.println(c.toString(decisionCovering));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
