package com.indigenous;

public class RuleSet {
  String[][] rules;
  int numAntecedents;
  int numConsequents;

  public RuleSet(int numRules, int numAntecedents, int numConsequents) {
    rules = new String[numRules][numAntecedents + numConsequents + 1];
    this.numAntecedents = numAntecedents;
    this.numConsequents = numConsequents;
  }

  public int getNumAntecedents() { return numAntecedents; }
  public int getNumConsequents() { return numConsequents; }
  public String toString() {
    return "";
  }
}
