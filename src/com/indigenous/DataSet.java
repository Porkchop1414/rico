package com.indigenous;

import java.util.LinkedList;
import java.util.List;

public class DataSet {
  private String name;
  private List<Attribute> attributes;
  private List<Data> data;

  public DataSet() {
    attributes = new LinkedList<Attribute>();
    data = new LinkedList<Data>();
  }

  public void setName(String name) { this.name = name; }
  public void addAttribute(Attribute attribute) { this.attributes.add(attribute); }
  public void addData(Data data) { this.data.add(data); }

  public String getName() { return name; }
  public Attribute getAttribute(int index) { return attributes.get(index); }
  public List<Data> getData() { return data; }
}
