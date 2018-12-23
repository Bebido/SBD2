package com.phenom;

import java.io.*;

public class TreeHeader implements Serializable {

    private int rekordSize;
    private int nodeSize;
    private int rootAdress;
    private int writableAddressTree;
    private int writableAddressRekord;
    private int[] reusableAddressesTree = new int[100];
    private int[] reusableAddressesData = new int[100];

    TreeHeader(){
        rekordSize = 0;//calculateRekordSize();
        rootAdress = -1;
        nodeSize = -1;
        writableAddressTree = -1;
        writableAddressRekord = 0;
    }

    public int getRootAdress() {
        return rootAdress;
    }

    public void setRootAdress(int rootAdress) {
        this.rootAdress = rootAdress;
    }

    public int getWritableAddressTree() {
        return writableAddressTree;
    }

    public void setWritableAddressTree(int writableAddressTree) {
        this.writableAddressTree = writableAddressTree;
    }

    public int[] getReusableAddressesTree() {
        return reusableAddressesTree;
    }

    public void setReusableAddressesTree(int[] reusableAddressesTree) {
        this.reusableAddressesTree = reusableAddressesTree;
    }

    public int getRekordSize() {
        return rekordSize;
    }

    public void setRekordSize(int rekordSize) {
        this.rekordSize = rekordSize;
    }

    public int getWritableAddressRekord() {
        return writableAddressRekord;
    }

    public void setWritableAddressRekord(int writableAddressRekord) {
        this.writableAddressRekord = writableAddressRekord;
    }

    public int[] getReusableAddressesData() {
        return reusableAddressesData;
    }

    public void setReusableAddressesData(int[] reusableAddressesData) {
        this.reusableAddressesData = reusableAddressesData;
    }

    public int getAddressToSaveTree() {
        writableAddressTree = writableAddressTree + nodeSize;
        return writableAddressTree - nodeSize;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public int calculateSize(){
        int size = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            size = byteNode.length;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

    public void save() {
        try  {
            RandomAccessFile treeFile = new RandomAccessFile(Globals.TREE_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();

            treeFile.write(byteNode, 0, byteNode.length);
            treeFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void load(){

        byte[] headerInBytes = new byte[this.calculateSize()];
        try {
            RandomAccessFile treeFile = new RandomAccessFile(Globals.TREE_FILE, "rw");
            treeFile.seek(0);
            treeFile.read(headerInBytes, 0, headerInBytes.length);
            treeFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(headerInBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = (TreeHeader)in.readObject();
            this.nodeSize = ((TreeHeader) o).nodeSize;
            this.rootAdress = ((TreeHeader) o).rootAdress;
            this.writableAddressRekord = ((TreeHeader) o).writableAddressRekord;
            this.writableAddressTree = ((TreeHeader) o).writableAddressTree;
            this.rekordSize = ((TreeHeader) o).rekordSize;
            this.reusableAddressesData = ((TreeHeader) o).reusableAddressesData;
            this.reusableAddressesTree = ((TreeHeader) o).reusableAddressesTree;
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

//    public int calculateRekordSize(){
//
//    }
}
