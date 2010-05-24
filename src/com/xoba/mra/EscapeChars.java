package com.xoba.mra;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class EscapeChars {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
        boolean done = false;
        while (!done) {
            String line = reader.readLine();
            if (line == null) {
                done = true;
            } else {
                for (int c : line.toCharArray()) {
                    boolean ok = true;
                    if (c <32) {
                        ok = false;
                    } else if (c > 126) {
                        ok = false;
                    }
                    if (ok) {
                        System.out.print((char)c);
                    }
                }
                System.out.println();
            }
        }
    }

}
