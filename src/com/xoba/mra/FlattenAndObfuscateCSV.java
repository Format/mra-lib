package com.xoba.mra;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * assumes one header row, and first column is a per-row label
 * 
 */
public class FlattenAndObfuscateCSV {

    private static final ILogger logger = LogFactory.getDefault().create();

    public static void main(String[] args) throws Exception {

        File f = args.length > 0 ? new File(args[0]) : new File("/tmp/s3cache/tm-features/site_features.csv");

        File dir = new File(f.getParentFile(), "flat_" + f.getName());
        dir.mkdirs();

        File dict = new File(dir, "dict_" + f.getName() + ".gz");
        File flat = new File(dir, "data_" + f.getName() + ".gz");
        File labels = new File(dir, "labels_" + f.getName() + ".gz");

        PrintWriter flatPW = new PrintWriter(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(flat))));
        flatPW.println("label\tfeature\tvalue");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

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

            {
                PrintWriter dictPW = new PrintWriter(new BufferedOutputStream(new GZIPOutputStream(
                        new FileOutputStream(dict))));
                dictPW.println("index\tfeature");
                for (Integer i : map.keySet()) {
                    dictPW.printf("%d\t%s", i, header[map.get(i)]);
                    dictPW.println();
                }
                dictPW.close();
            }

            PrintWriter labelPW = new PrintWriter(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(
                    labels))));
            labelPW.println("id\tlabel");

            long row = 0;
            boolean done = false;
            while (!done) {
                String line = reader.readLine();
                if (line == null) {
                    done = true;
                } else {
                    String[] parts = line.split("\t");

                    if (parts.length == header.length) {

                        String label = parts[0];

                        labelPW.printf("%d\t%s", row, label);
                        labelPW.println();

                        for (Integer i : map.keySet()) {
                            flatPW.print(row);
                            flatPW.print("\t");
                            flatPW.print(i);
                            flatPW.print("\t");
                            flatPW.print(parts[map.get(i)]);
                            flatPW.println();
                        }

                        row++;
                    }

                }
            }

            labelPW.close();

            reader.close();
        } finally {
            flatPW.close();
        }

    }
}
