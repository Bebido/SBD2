package com.phenom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Globals {

    static final int D = 5;
    static final String DATA_FILE = "dataFile.dat";
    static final String TREE_FILE = "tree.btree";
    static TreeHeader treeHeader = null;
    static Integer nodeSize;

    public static void loadTreeHeader(){
        if (treeHeader == null){

        }
    }

    public static void initTreeHeader(){
        treeHeader = new TreeHeader();
        treeHeader.rootAdress = null;
        treeHeader.writableAdressTree = 0;

        Node node = new Node();
        node.myAddress = -1;
        while (node.rekordList.size() < 10)
            node.add(new Rekord(1));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(node);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            treeHeader.rekordSize = byteNode.length;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static TreeHeader getTreeHeader() {
        return treeHeader;
    }

    public static void setTreeHeader(TreeHeader treeHeader) {
        Globals.treeHeader = treeHeader;
    }

}
