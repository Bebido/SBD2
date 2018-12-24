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
        BTree bTree = null;

        System.out.println("Czy wczytac baze pliku? (t/n)");
        try {
            text = in.readLine();
            if (text.startsWith("t")) {
                Globals.initFromFile();
                bTree = new BTree();
                bTree.setFromHeader();
                System.out.println("Wczytano baze z pliku");
            } else {
                if(Globals.getTreeHeader() == null)
                    Globals.initTreeHeader();
                bTree = new BTree();
                System.out.println("Utworzona nowa baze");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                if (bTree.delete(argument))
                    System.out.println("Usunieto rekord o kluczu: " + argument);
                else{
                    System.out.println("Nie znaleziono rekordu o podanym kluczu");
                }
            }

            if (text.startsWith("u")) {
                argument = getArgument(text);
                if (bTree.update(argument))
                    System.out.println("Zaktualizowano rekord: " + argument);
                else{
                    System.out.println("Nie znaleziono rekordu o podanym kluczu");
                }
            }

            if (text.startsWith("w")) {
                bTree.display();
            }

            if (text.startsWith(("f"))){
                argument = getArgument(text);
                Rekord rekord = bTree.getRekord(argument);
                if (rekord == null){
                    System.out.println("Nie znaleziono rekordu o kluczu " + argument.toString() + ".");
                } else
                    System.out.println(rekord.toString());
            }

            if (text.startsWith("e")) {
                going = false;
                bTree.setHeader();
                Globals.getTreeHeader().save();
            }

            System.out.println("Zapisy: " + Globals.zapisyTree + " Odczyty: " + Globals.odczytyTree);
        }
    }

    private static Integer getArgument(String text) {
        String[] s = text.split(" ");
        return Integer.parseInt(s[1]);
    }
}
