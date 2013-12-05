package com.indigenous;

import java.util.BitSet;

public class RuleReductionBitSet extends BitSet {
  int attribute;
  String value;

  public RuleReductionBitSet(int nbits, int attribute, String value) {
    super(nbits);
    this.attribute = attribute;
    this.value = value;
  }

  public int getAttribute() { return attribute; }
}
