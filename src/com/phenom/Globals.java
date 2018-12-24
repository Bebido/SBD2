package com.phenom;


import java.io.*;

public class Globals implements Serializable {

    static final int D = 1;
    static final String DATA_FILE = "dataFile.dat";
    static final String TREE_FILE = "tree.btree";
    static TreeHeader treeHeader = null;
    static int treeHeaderSize;
    static int zapisyTree = 0;
    static int odczytyTree = 0;

    public static void initTreeHeader(){
        treeHeader = new TreeHeader();
        treeHeader.setRootAdress(-1);
        treeHeader.setWritableAddressTree(0);

        Node node = new Node();
        node.myAddress = -1;
        node.parentAdress = -1;
        while (node.rekordNodeList.size() < 2*D)
            node.add(new RekordNode(-1));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(node);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            treeHeader.setNodeSize(byteNode.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        treeHeaderSize = treeHeader.calculateSize();
        treeHeader.setWritableAddressTree(treeHeaderSize);
        treeHeader.save();
    }

    public static TreeHeader getTreeHeader() {
        return treeHeader;
    }

    public static void setTreeHeader(TreeHeader treeHeader) {
        Globals.treeHeader = treeHeader;
    }

    public static void initFromFile() {

        treeHeader = new TreeHeader();
        treeHeader.load();
    }

    public static int getTreeHeaderSize() {
        return treeHeaderSize;
    }

    public static void setTreeHeaderSize(int treeHeaderSize) {
        Globals.treeHeaderSize = treeHeaderSize;
    }

    public static void addZapis(){
        zapisyTree++;
    }

    public static void addOdczyt(){
        odczytyTree++;
    }

    public static int getZapisyTree() {
        return zapisyTree;
    }

    public static int getOdczytyTree() {
        return odczytyTree;
    }
}
