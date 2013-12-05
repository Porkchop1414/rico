package com.indigenous;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleSet {
  String[][] rules;
  Attribute[] consequents;
  Attribute[] antecedents;

  public RuleSet(Covering antecedents, Covering consequents) {
    rules = new String[antecedents.getGroupings().size()][antecedents.getAttributes().length + consequents.getAttributes().length + 1];
    this.antecedents = antecedents.getAttributes();
    this.consequents = consequents.getAttributes();

    List<Map.Entry<String, List<Integer>>> entryList = new ArrayList<Map.Entry<String, List<Integer>>>();
    entryList.addAll(antecedents.getGroupings().entrySet());
    for (int i = 0; i < entryList.size(); i++) {
      Map.Entry<String, List<Integer>> entry = entryList.get(i);
      String[] values = entry.getKey().split("\\s+");
      System.arraycopy(values, 0, rules[i], 0, values.length);

      // decision covering values
      for (Map.Entry<String, List<Integer>> decisionEntry : consequents.getGroupings().entrySet()) {
        boolean flag = true;
        for (Integer num : entry.getValue()) {
          if (!decisionEntry.getValue().contains(num)) {
            flag = false;
          }
        }
        if (flag) {
          values = decisionEntry.getKey().split("\\s+");
          System.arraycopy(values, 0, rules[i], antecedents.getAttributes().length, values.length);
        }
      }
      rules[i][rules[i].length - 1] = String.valueOf(entry.getValue().size());
    }
  }

  public int getNumAntecedents() {
    return antecedents.length;
  }

  public int getNumConsequents() {
    return consequents.length;
  }

  public String toString(int minRuleCoverage) {
    String s = "Rules for covering [";
    for (int i = 0; i < antecedents.length; i++) {
      s += antecedents[i].getName();
      if (i < antecedents.length - 1) {
        s += ", ";
      }
    }
    s += "]:\n[";

    boolean firstAttribute = true;
    for (String[] rule : rules) {
      if (Integer.parseInt(rule[rule.length - 1]) >= minRuleCoverage) {
        if (!firstAttribute) {
          s += ", ";
        } else {
          firstAttribute = false;
        }
        s += "[[";
        int i;
        for (i = 0; i < rule.length - 1; i++) {
          s += rule[i];
          if (i < rule.length - 2) {
            s += ", ";
          }
        }
        s += "], " + rule[i] + "]";
      }
    }
    s += "]";

    return s;
  }
}
