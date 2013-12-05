package com.indigenous;

import java.util.*;

public class Covering {

  Attribute[] attributes;                   // List of attributes in this covering
  Map<String, List<Integer>> groupings;     // Key is the joint value of the attributes; Value is a list of the indexes in both attributes' value-sets where these joint attributes occur

  public Covering(List<Attribute> coveringAttributes) {
    attributes = new Attribute[coveringAttributes.size()];
    for (int i = 0; i < coveringAttributes.size(); i++) {
      attributes[i] = coveringAttributes.get(i);
    }

    createGroupings();
  }

  public Covering(Attribute[] coveringAttributes) {
    attributes = new Attribute[coveringAttributes.length];
    System.arraycopy(coveringAttributes, 0, attributes, 0, coveringAttributes.length);

    createGroupings();
  }

  private void createGroupings() {
    groupings = new HashMap<String, List<Integer>>();
    if (attributes.length > 0) {
      int totalPossibleValues = attributes[0].getTotalValueCount();
      for (int i = 0; i < totalPossibleValues; i++) {
        String s = "";
        for (Attribute a : attributes) {
          s += a.getValue(i) + " ";
        }
        List<Integer> indexes = groupings.get(s);
        if (indexes == null) {
          indexes = new ArrayList<Integer>();
        }
        indexes.add(i);
        groupings.put(s, indexes);
      }
    }
  }

  public void dropUnnecessaryConditions(Covering decisionCovering) {
    // Generate partition bitsets for each single attribute in the covering
    List<List<RuleReductionBitSet>> bitSets = new ArrayList<List<RuleReductionBitSet>>();
    for(Attribute a : attributes) {
      bitSets.add(new ArrayList<RuleReductionBitSet>());
    }
    List<Map.Entry<String, List<Integer>>> entryList = new ArrayList<Map.Entry<String, List<Integer>>>();
    entryList.addAll(groupings.entrySet());
    for (int i = 0; i < entryList.size(); i++) {
      Map.Entry<String, List<Integer>> entry = entryList.get(i);
      String[] groupingValues = entry.getKey().split("\\s+");
      for(int j = 0; j < groupingValues.length; j++) {
        boolean exists = false;
        int k;
        for(k = 0; k < bitSets.get(j).size(); k++) {
          if(bitSets.get(j).get(k).value.equals(groupingValues[j])) {
            exists = true;
            break;
          }
        }
        if(!exists) {
          bitSets.get(j).add(new RuleReductionBitSet(groupings.size(), attributes[j], groupingValues[j]));
        }
        bitSets.get(j).get(k).set(i);
      }
    }

    /*for(int i = 0; i < bitSets.size(); i++) {
      for(int j = 0; j < bitSets.get(i).size(); j++) {
        System.out.println(bitSets.get(i).get(j).toString());
      }
    }*/

    List<RuleReductionBitSet> decisionBitSets = new ArrayList<RuleReductionBitSet>();


    // Sort non-decision bitsets

    // loop through each decision bitset and find reductions

    // use reductions to clean up rules

  }

  private void dropCommonConditions() {
    List<String[]> groupingKeyValues = new ArrayList<String[]>();
    Set<Map.Entry<String, List<Integer>>> entrySet = groupings.entrySet();
    for (Map.Entry<String, List<Integer>> entry : entrySet) {
      groupingKeyValues.add(entry.getKey().split("\\s+"));
    }

    for (int i = 0; i < groupingKeyValues.size(); i++) {
      String[] keyValues = groupingKeyValues.get(i);
      for (int j = 0; j < keyValues.length; j++) {
        boolean drop = true;
        for (int k = 0; k < groupingKeyValues.size() && drop; k++) {
          if (k != i && groupingKeyValues.get(k)[j].equals(keyValues[j])) {
            // More than one grouping uses same value (cannot be dropped)
            drop = false;
          }
        }

        if (drop) {
          String oldKey = "";
          for (String s : keyValues) {
            oldKey += s + " ";
          }
          String newKey = "";
          for (int k = 0; k < keyValues.length; k++) {
            if (k != j) {
              newKey += "_" + " ";
            } else {
              newKey += keyValues[k] + " ";
            }
          }

          groupings.put(newKey, groupings.get(oldKey));
          groupings.remove(oldKey);
          break;
        }
      }
    }
  }

  public String getAttributeNames() {
    String s = "";
    for (int i = 0; i < attributes.length; i++) {
      s += attributes[i].getName();
      if (i < attributes.length - 1) {
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
    for (List<Integer> list : temp) {
      counts.add(list.size());
    }
    return counts;
  }

  /**
   * Checks that the attribute dependency inequality holds for this covering
   *
   * @param decisionCovering the decision attribute
   * @return true if the inequality holds, false otherwise
   */
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
    for (int i = 0; i < attributes.length; i++) {
      s += attributes[i].getName();
      if (i < attributes.length - 1) {
        s += ", ";
      }
    }
    s += "]:\n[";
    boolean firstAttribute = true;
    Set<Map.Entry<String, List<Integer>>> entrySet = groupings.entrySet();
    for (Map.Entry<String, List<Integer>> entry : entrySet) {
      if (entry.getValue().size() >= minRuleCoverage) {
        if (!firstAttribute) {
          s += ", ";
        } else {
          firstAttribute = false;
        }
        String[] values = entry.getKey().split("\\s+");
        s += "[[";
        for (String value : values) {
          s += value + ", ";
        }
        // decision covering values
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
