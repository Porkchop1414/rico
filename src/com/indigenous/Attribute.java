package com.indigenous;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Attribute {

  private String name;
  private AttributeType type;
  private Set<String> possibleValues;
  private List<String> valueSet;

  public Attribute() {
    possibleValues = new HashSet<String>();
    valueSet = new LinkedList<String>();
  }

  public void setName(String name) { this.name = name; }
  public void setType(AttributeType type) { this.type = type; }

  /**
   * Adds the value to the list of possible values if it does not already exists in that list
   * @param value value to be added to the list
   */
  public void addPossibleValue(String value) {
    if(!possibleValues.contains(value)) {
      possibleValues.add(value);
    }
  }

  public void addActualValue(String value) {
    valueSet.add(value);
  }

  public String getName() { return name; }
  public AttributeType getType() { return type; }
  public Set<String> getPossibleValues() { return possibleValues; }
  public String getValue(int index) { return valueSet.get(index); }
  public int getTotalValueCount() { return valueSet.size(); }
  public int getValueCount(String key) {
    int count = 0;
    for(String s : valueSet) {
      if(s.equals(key)) {
        count++;
      }
    }
    return count;
  }
}
