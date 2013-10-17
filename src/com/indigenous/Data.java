package com.indigenous;

public class Data {

  private String[] values;

  public Data(String[] values) {
    this.values = values;
  }

  public String[] getData() { return values; }
  public String getDataValue(int index) { return values[index]; }
}
