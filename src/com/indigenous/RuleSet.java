package com.indigenous;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class RuleSet {
  String[][] rules;
  int numAntecedents;
  int numConsequents;

  public RuleSet(int numRules, int numAntecedents, int numConsequents) {
    rules = new String[numRules][numAntecedents + numConsequents + 1];
    this.numAntecedents = numAntecedents;
    this.numConsequents = numConsequents;
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
    // TODO: Sort the sortedBitSets list.

    // TODO: Consider moving this into the for loop.
    List<List<RuleReductionBitSet>> reductions = new ArrayList<List<RuleReductionBitSet>>();
    for(int i=0; i<decBitSets.size(); i++) {
      reductions.add(new ArrayList<RuleReductionBitSet>());
    }

    for(int decIndex=0; decIndex<decBitSets.size(); decIndex++) {
      RuleReductionBitSet dbs = decBitSets.get(decIndex);
      BitSet dbsTemp = (BitSet)dbs.clone();

      for(int i=0; i<sortedBitSets.size(); i++) {
        RuleReductionBitSet bs = sortedBitSets.get(i);
        if(bs.cardinality() <= dbsTemp.cardinality()) {
          BitSet test = (BitSet)dbsTemp.clone();

          test.xor(bs);
          if(test.cardinality() < dbsTemp.cardinality()) {
            reductions.get(decIndex).add(bs);
            dbsTemp.xor(bs); // Apply it for real.

            if(dbsTemp.cardinality() == 0) break;
          }
        }
      }

      if(dbsTemp.cardinality() != 0) {
        // No reduction can be made for this rule.
        reductions.get(decIndex).clear();
      }

      // Apply the reductions:
      if(reductions.get(decIndex).size() > 0) {
        for(int i=0; i<numAntecedents; i++) {
          boolean antFound = false;
          for(int j=0; j<reductions.get(decIndex).size(); j++) {
            if(reductions.get(decIndex).get(j).getAttribute() == i) {
              antFound = true;
              break;
            }
          }
          if(!antFound) {
            rules[decIndex][i] = "_";
          }
        }
      }
    }
  }

  private List<List<RuleReductionBitSet>> generateAntecedentBitSets() {
    List<List<RuleReductionBitSet>> bitSets = new ArrayList<List<RuleReductionBitSet>>();

    for(int i=0; i<numAntecedents; i++) {
      bitSets.add(new ArrayList<RuleReductionBitSet>());
    }

    for(int ruleIndex=0; ruleIndex<rules.length; ruleIndex++) { // Iterate over each rule (ruleIndex)
      for(int antIndex=0; antIndex<numAntecedents; antIndex++) { // Iterate over each antecedent (antIndex)

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

      for(int decIndex=numAntecedents; decIndex<numAntecedents+numConsequents; decIndex++) { // Iterate over each consequent (decIndex)
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

  public int getNumAntecedents() { return numAntecedents; }
  public int getNumConsequents() { return numConsequents; }
  public String toString() {
    return "";
  }
}
