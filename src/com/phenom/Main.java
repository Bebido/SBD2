package com.phenom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {

        boolean going = true;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String text;
        Integer argument;
        ///tutaj z pliku bedzie inaczej
        if(Globals.getTreeHeader() == null)
            Globals.initTreeHeader();
        BTree bTree = new BTree();

        while (going){
            try {
                text = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                text = "";
            }

            if (text.startsWith("a")) {
                argument = getArgument(text);
                if(bTree.add(argument))
                    System.out.println("Dodano rekord o kluczu: " + argument + ".");
                else
                    System.out.println("Rekord o podanym kluczu juz istnieje");
            }

            if (text.startsWith("d")) {
                argument = getArgument(text);
                //del
            }

            if (text.startsWith("w")) {
                bTree.display();
                //argument = getArgument(text);
                //del
            }

            if (text.startsWith(("f"))){
                argument = getArgument(text);
                Rekord rekord = bTree.find(argument);
                if (rekord == null){
                    System.out.println("Nie znaleziono rekordu o kluczu " + argument.toString() + ".");
                } else
                    System.out.println(rekord.toString());
            }

            if (text.startsWith("e")) {
                going = false;
            }
        }
    }

    private static Integer getArgument(String text) {
        String[] s = text.split(" ");
        return Integer.parseInt(s[1]);
    }
}
