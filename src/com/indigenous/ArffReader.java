package com.indigenous;

import javax.activation.UnsupportedDataTypeException;
import java.io.*;
import java.util.Scanner;

public class ArffReader {
  public static DataSet readArffFile(File file) throws IOException {
    DataSet dataSet = new DataSet();
    Scanner scanner = new Scanner(file);

    while (scanner.hasNext()) {
      String declaration = scanner.next();
      if (declaration.equals("@relation")) {
        dataSet.setName(scanner.next());
      } else if (declaration.equals("@attribute")) {
        Attribute attribute = new Attribute();
        if (scanner.hasNext()) {
          attribute.setName(scanner.next());
        } else throw new EOFException();

        if (scanner.hasNext()) {
          String type = scanner.next();
          if (type.equals("numeric")) {
            attribute.setType(AttributeType.numeric);
            attribute.addPossibleValue("?");
          } else if (type.charAt(0) == '{' && type.charAt(type.length() - 1) == '}') {
            type = type.substring(1, type.length() - 1);   // remove the braces
            attribute.setType(AttributeType.nominal);

            String[] values = type.split(",");
            for (String s : values) {
              attribute.addPossibleValue(s);
            }
          } else throw new UnsupportedDataTypeException("The supplied file is not a valid ARFF file.");
        } else throw new EOFException();

        dataSet.addAttribute(attribute);
      } else if (declaration.equals("@data")) {
        // Process all data
        while (scanner.hasNext()) {
          dataSet.incrementDataSize();
          String[] dataSplit = scanner.next().split(",");

          // Add value to set of possible values of attribute if type numeric
          int counter = 0;
          for (String s : dataSplit) {
            Attribute attribute = dataSet.getAttribute(counter++);
            attribute.addActualValue(s);
            if (attribute.getType() == AttributeType.numeric) {
              attribute.addPossibleValue(s);
            }
          }
        }
      } else throw new UnsupportedDataTypeException("The supplied file is not a valid ARFF file.");
    }

    scanner.close();

    return dataSet;
  }
}
