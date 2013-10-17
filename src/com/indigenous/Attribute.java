package com.indigenous;

import java.util.LinkedList;
import java.util.List;

public class Attribute {

  private String name;
  private AttributeType type;
  private List<String> values;

  public Attribute() {
    values = new LinkedList<String>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(AttributeType type) {
    this.type = type;
  }

  /**
   * Adds the value to the list of possible values if it does not already exists in that list
   * @param value value to be added to the list
   */
  public void addValue(String value) {
    if(!values.contains(value)) {
      values.add(value);
    }
  }

  public String getName() { return name; }
  public AttributeType getType() { return type; }
  public List<String> getValues() { return values; }
}
