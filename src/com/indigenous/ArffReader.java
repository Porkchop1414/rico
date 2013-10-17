package com.indigenous;

import java.io.*;
import java.util.Scanner;

/* Example ARFF File
@relation table3_10_fg

@attribute a numeric
@attribute b {L,R,S}
@attribute c numeric
@attribute d {L,H}
@attribute f numeric
@attribute g {L,H}

@data
0,L,0,L,0,L
0,R,1,L,1,L
0,L,0,L,0,L
0,R,1,L,1,L
1,R,0,L,2,H
1,R,0,L,2,H
2,S,2,H,3,H
2,S,2,H,3,H
*/

public class ArffReader {
  public static ArffData readArffFile(File file) {
    ArffData arffData = new ArffData();

    try {
      /*FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);*/
      Scanner scanner = new Scanner(file);

      // Do the reading into the arffData object here

      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return arffData;
  }
}
