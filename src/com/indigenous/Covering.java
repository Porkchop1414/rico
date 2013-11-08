package com.indigenous;

import java.util.*;

public class Covering {

  List<Attribute> attributes;
  Map<String, List<Integer>> groupings;

  public Covering(List<Attribute> coveringAttributes) {
    attributes = new ArrayList<Attribute>();
    for (Attribute a : coveringAttributes) {
      attributes.add(a);
    }

    groupings = new HashMap<String, List<Integer>>();
    for (int i = 0; i < coveringAttributes.get(0).getTotalValueCount(); i++) {
      String s = "";
      for (int j = 0; j < coveringAttributes.size(); j++) {
        s += coveringAttributes.get(j).getValue(i);
        if(j < coveringAttributes.size() - 1) {
          s += " ";
        }
      }
      List<Integer> indexes = groupings.get(s);
      if (indexes == null) {
        indexes = new LinkedList<Integer>();
      }
      indexes.add(i);
      groupings.put(s, indexes);
    }
  }

  public String getAttributeNames() {
    String s = "";
    for(int i = 0; i < attributes.size(); i++) {
      s += attributes.get(i).getName();
      if(i < attributes.size() - 1) {
        s += ", ";
      }
    }
    return s;
  }

  public List<String> getPossibleValues() {
    return new ArrayList<String>(groupings.keySet());
  }

  public List<Integer> getPossibleValueCoverages() {
    List<Integer> counts = new ArrayList<Integer>();
    List<List<Integer>> temp = new ArrayList<List<Integer>>(groupings.values());
    for(List<Integer> list : temp) {
      counts.add(list.size());
    }
    return counts;
  }

  public boolean isValidCovering(Covering decisionCovering) {
    for (Map.Entry<String, List<Integer>> entry : groupings.entrySet()) {
      List<Integer> value = entry.getValue();
      boolean contains = true;
      for (Map.Entry<String, List<Integer>> entry2 : decisionCovering.groupings.entrySet()) {
        contains = true;
        for (Integer i : value) {
          if (!entry2.getValue().contains(i)) {
            contains = false;
            break;
          }
        }
        if (contains) {
          break;
        }
      }
      if (!contains) {
        return false;
      }
    }
    return true;
  }

  public String getRules(Covering decisionCovering, int minRuleCoverage) {
    String s = "Rules for covering [";
    for (int i = 0; i < attributes.size(); i++) {
      s += attributes.get(i).getName();
      if (i < attributes.size() - 1) {
        s += ", ";
      }
    }
    s += "]:\n[";
    for (Map.Entry<String, List<Integer>> entry : groupings.entrySet()) {
      if (entry.getValue().size() >= minRuleCoverage) {
        String[] values = entry.getKey().split("\\s+");
        s += "[[";
        for (String value : values) {
          s += value + ", ";
        }
        // decision covering values here
        for (Map.Entry<String, List<Integer>> decisionEntry : decisionCovering.groupings.entrySet()) {
          boolean flag = true;
          for (Integer i : entry.getValue()) {
            if (!decisionEntry.getValue().contains(i)) {
              flag = false;
            }
          }
          if (flag) {
            values = decisionEntry.getKey().split("\\s+");
            for (int i = 0; i < values.length; i++) {
              s += values[i];
              if (i < values.length - 1) {
                s += ", ";
              }
            }
          }
        }
        s += "], " + entry.getValue().size() + "]";
      }
    }
    s += "]";
    return s;
  }
}
