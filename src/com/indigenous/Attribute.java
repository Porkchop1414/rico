package com.indigenous;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Attribute {

  private String name;
  private AttributeType type;
  private List<String> values;
  private Map<String, Integer> occurrences;

  public Attribute() {
    values = new LinkedList<String>();
    occurrences = new HashMap<String, Integer>();
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

  public void addOccurrenceCount(String key) {
    Integer occurrenceCount = occurrences.get(key);
    if(occurrenceCount == null) {
      occurrenceCount = 0;
    }
    occurrences.put(key, occurrenceCount + 1);
  }

  public String getName() { return name; }
  public AttributeType getType() { return type; }
  public List<String> getValues() { return values; }
  public int getOccurrenceCount(String key) { return occurrences.get(key); }
}
