package com.indigenous;

import java.util.*;

public class Covering {

  List<String> attributeNames;
  Map<String, List<Integer>> groupings;

  public Covering(List<Attribute> partitionAttributes) {
    attributeNames = new ArrayList<String>();
    for(Attribute a : partitionAttributes) {
      attributeNames.add(a.getName());
    }

    groupings = new HashMap<String, List<Integer>>();
    for(int i = 0; i < partitionAttributes.get(0).getTotalValueCount(); i++) {
      String s = "";
      for(Attribute a : partitionAttributes) {
        s += a.getValue(i) + " ";
      }
      List<Integer> indexes = groupings.get(s);
      if(indexes == null) {
        indexes = new LinkedList<Integer>();
      }
      indexes.add(i);
      groupings.put(s, indexes);
    }
  }

  public boolean isValidCovering(Covering decisionCovering) {
    for(Map.Entry<String, List<Integer>> entry : groupings.entrySet()) {
      List<Integer> value = entry.getValue();
      boolean contains = true;
      for(Map.Entry<String, List<Integer>> entry2 : decisionCovering.groupings.entrySet()) {
        contains = true;
        for(Integer i : value) {
          if(!entry2.getValue().contains(i)) {
            contains = false;
            break;
          }
        }
        if(contains) {
          break;
        }
      }
      if(!contains) {
        return false;
      }
    }
    return true;
  }

  public List<String> getAttributeNames() { return attributeNames; }

  // TODO: Finish this... rename it, and possibly move it somewhere else
  public String toString(Covering decisionCovering) {
    String s = "[";
    for(Map.Entry<String, List<Integer>> entry : groupings.entrySet()) {
      String[] values = entry.getKey().split("\\s+");
      s += "[[";
      for(String value : values) {
        s += value + ", ";
      }
      // decision covering values here
      s += "], ";
      // Coverage goes here
      s += "]";
    }
    s += "]";
    return s;
  }
}
