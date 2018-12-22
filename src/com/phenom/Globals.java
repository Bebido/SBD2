package com.phenom;


import java.io.*;

public class Globals implements Serializable {

    static final int D = 2;
    static final String DATA_FILE = "dataFile.dat";
    static final String TREE_FILE = "tree.btree";
    static TreeHeader treeHeader = null;
    static Integer nodeSize;
    static Integer treeHeaderSize;

    public static void loadTreeHeader(){
        if (treeHeader == null){

        }
    }

    public static void initTreeHeader(){
        treeHeader = new TreeHeader();
        treeHeader.setRootAdress(-1);
        treeHeader.setWritableAddressTree(0);

        Node node = new Node();
        node.myAddress = -1;
        node.parentAdress = -1;
        while (node.rekordList.size() < 2*D)
            node.add(new Rekord(-1));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(node);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            treeHeader.setRekordSize(byteNode.length);
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

}
