package com.xoba.mra;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FlattenCSV {

    private static final ILogger logger = LogFactory.getDefault().create();

    public static void main(String[] args) throws Exception {

        PrintWriter pw = new PrintWriter(new BufferedOutputStream(System.out));

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(System.in)));

            String[] header = reader.readLine().split("\t");

            List<Integer> list = new LinkedList<Integer>();
            for (int i = 1; i < header.length; i++) {
                list.add(i);
            }

            Collections.shuffle(list);

            Integer[] array = list.toArray(new Integer[list.size()]);

            Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
            for (int i = 0; i < array.length; i++) {
                map.put(i, array[i]);
            }

            for (Integer i : map.keySet()) {
                pw.printf("# %d: %s", i, header[map.get(i)]);
                pw.println();
            }

            boolean done = false;
            while (!done) {
                String line = reader.readLine();
                if (line == null) {
                    done = true;
                } else {
                    String[] parts = line.split("\t");

                    if (parts.length == header.length) {
                        for (Integer i : map.keySet()) {
                            pw.print(i);
                            pw.print("\t");
                            pw.print(parts[map.get(i)]);
                            pw.print("\t");
                            pw.print(parts[0]);
                            pw.println();
                        }
                    }

                }
            }
            reader.close();
        } finally {
            pw.close();
        }
    }
}
