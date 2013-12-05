package com.indigenous;

import java.util.BitSet;

public class RuleReductionBitSet extends BitSet {
  Attribute attribute;
  String value;

  public RuleReductionBitSet(int nbits, Attribute attribute, String value) {
    super(nbits);
    this.attribute = attribute;
    this.value = value;
  }
}
