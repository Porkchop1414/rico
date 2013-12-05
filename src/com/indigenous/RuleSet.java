package com.indigenous;

import java.util.ArrayList;
import java.util.BitSet;
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

  public void reduce() {

    List<List<RuleReductionBitSet>> antBitSets = generateAntecedentBitSets();
    List<RuleReductionBitSet> decBitSets = generateDecisionBitSets();

    List<RuleReductionBitSet> sortedBitSets = new ArrayList<RuleReductionBitSet>();
    for(List<RuleReductionBitSet> list : antBitSets) {
      for(RuleReductionBitSet bs : list) {
        sortedBitSets.add(bs);
      }
    }
    // TODO: Sort the sortedBitSets list, this will theoretically improve reductive performance.

    List<List<RuleReductionBitSet>> reductions = new ArrayList<List<RuleReductionBitSet>>();
    for(int i=0; i<decBitSets.size(); i++) {
      reductions.add(new ArrayList<RuleReductionBitSet>());
    }

    for(int decBitIndex=0; decBitIndex<decBitSets.size(); decBitIndex++) {
      RuleReductionBitSet dbs = decBitSets.get(decBitIndex);
      BitSet dbsTemp = (BitSet)dbs.clone();

      for(int i=0; i<sortedBitSets.size(); i++) {
        RuleReductionBitSet bs = sortedBitSets.get(i);
        if(bs.cardinality() <= dbsTemp.cardinality()) {
          BitSet test = (BitSet)dbsTemp.clone();

          test.xor(bs);
          if(test.cardinality() < dbsTemp.cardinality()) {
            reductions.get(decBitIndex).add(bs);
            dbsTemp.xor(bs); // Apply it for real.

            if(dbsTemp.cardinality() == 0) break;
          }
        }
      }

      // Apply the reductions for this decision BitSet (dbs):
      if(reductions.get(decBitIndex).size() > 0) {
        for(int i=0; i<antecedents.length; i++) { // Check each antecedent to see if it was included in the reduction
          boolean antFound = false;
          for(int j=0; j<reductions.get(decBitIndex).size(); j++) {
            if(reductions.get(decBitIndex).get(j).getAttribute() == i) {
              antFound = true;
              break;
            }
          }
          if(!antFound) { // If we couldn't find the antecedent listed in the reductions, remove it.
            dbsTemp.xor(dbs); // XOR to find the rules which we have found reductions for.
            for(int bi=0; bi<dbsTemp.size(); bi++) {
              if(dbsTemp.get(bi)) {
                rules[bi][i] = "_";
              }
            }
          }
        }

        for(int ri=0; ri<reductions.get(decBitIndex).size(); ri++) {
          RuleReductionBitSet reduction = reductions.get(decBitIndex).get(ri);
          boolean initialRuleFound = false;
          int initialRuleIndex = -1;
          for(int rj=0; rj<reduction.size(); rj++) { // Go through the bits, ie rules.
            if(reduction.get(rj)) {
              if(initialRuleFound) {
                int initialCount = Integer.parseInt(rules[initialRuleIndex][rules[initialRuleIndex].length - 1]);
                int rjCount = Integer.parseInt(rules[rj][rules[initialRuleIndex].length - 1]);

                rules[initialRuleIndex][rules[initialRuleIndex].length - 1] = Integer.toString(initialCount + rjCount);
                rules[rj][rules[initialRuleIndex].length - 1] = Integer.toString(0);
              } else {
                initialRuleFound = true;
                initialRuleIndex = rj;
              }
            }
          }
        }
      }
    }
  }

  private List<List<RuleReductionBitSet>> generateAntecedentBitSets() {
    List<List<RuleReductionBitSet>> bitSets = new ArrayList<List<RuleReductionBitSet>>();

    for(int i=0; i<antecedents.length; i++) {
      bitSets.add(new ArrayList<RuleReductionBitSet>());
    }

    for(int ruleIndex=0; ruleIndex<rules.length; ruleIndex++) { // Iterate over each rule (ruleIndex)
      for(int antIndex=0; antIndex<antecedents.length; antIndex++) { // Iterate over each antecedent (antIndex)

        boolean valAlreadyExists = false;

        int valIndex;
        for(valIndex=0; valIndex<bitSets.get(antIndex).size(); valIndex++) {
          if(bitSets.get(antIndex).get(valIndex).value.compareTo(rules[ruleIndex][antIndex]) == 0) {
            valAlreadyExists = true;
            break;
          }
        }

        if(!valAlreadyExists) {
          bitSets.get(antIndex).add(new RuleReductionBitSet(rules.length,antIndex,rules[ruleIndex][antIndex]));
        }

        bitSets.get(antIndex).get(valIndex).set(ruleIndex);
      }
    }

    return bitSets;
  }

  private List<RuleReductionBitSet> generateDecisionBitSets() {
    List<RuleReductionBitSet> bitSets = new ArrayList<RuleReductionBitSet>();

    for(int ruleIndex=0; ruleIndex<rules.length; ruleIndex++) { // Iterate over each rule (ruleIndex)
      String decValue = "";

      for(int decIndex=antecedents.length; decIndex<antecedents.length+consequents.length; decIndex++) { // Iterate over each consequent (decIndex)
        decValue += rules[ruleIndex][decIndex];
        decValue += " ";
      }

      boolean valAlreadyExists = false;

      int valIndex;
      for(valIndex=0; valIndex<bitSets.size(); valIndex++) {
        if(bitSets.get(valIndex).value.compareTo(decValue) == 0) {
          valAlreadyExists = true;
          break;
        }
      }

      if(!valAlreadyExists) {
        bitSets.add(new RuleReductionBitSet(rules.length,ruleIndex,decValue));
      }

      bitSets.get(valIndex).set(ruleIndex);
    }

    return bitSets;
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
