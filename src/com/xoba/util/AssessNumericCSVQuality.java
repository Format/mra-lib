package com.xoba.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssessNumericCSVQuality {

    private static final ILogger logger = LogFactory.getDefault().create();

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(System.in)));
        boolean done = false;
        while (!done) {
            String line = reader.readLine();
            if (line == null) {
                done = true;
            } else {
                long count = 0;
                String[] parts = line.split("\t");
                for (int i = 0; i < parts.length; i++) {
                    try {
                        Double x = new Double(parts[i]);
                        count++;
                    } catch (Exception e) {
                    }
                }
                System.out.print(count);
                System.out.print("\t");
                System.out.print(line);
                System.out.println();
            }
        }
        reader.close();
    }

}
