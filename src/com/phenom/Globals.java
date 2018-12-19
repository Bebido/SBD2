package com.phenom;

public class Globals {

    static final int D = 5;
    static final String DATA_FILE = "dataFile.dat";
    static final String TREE_FILE = "tree.btree";
    static TreeHeader treeHeader = null;

    public static void loadTreeHeader(){
        if (treeHeader == null){

        }
    }

    public static void initTreeHeader(){
        treeHeader = new TreeHeader();
        treeHeader.rootAdress = null;
        treeHeader.writableAdress = 0;
    }

    public static TreeHeader getTreeHeader() {
        return treeHeader;
    }

    public static void setTreeHeader(TreeHeader treeHeader) {
        Globals.treeHeader = treeHeader;
    }
}
