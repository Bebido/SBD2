package com.phenom;

import java.io.*;

public class TreeHeader implements Serializable {

    private int rekordSize;
    private int rootAdress;
    private int writableAddressTree;
    private int writableAddressRekord;
    private int[] reusableAddressesTree = new int[100];
    private int[] reusableAddressesData = new int[100];

    TreeHeader(){
        rekordSize = -1;
        rootAdress = -1;
        writableAddressTree = -1;
        writableAddressRekord = -1;
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
        writableAddressTree = writableAddressTree + rekordSize;
        return writableAddressTree - rekordSize;
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
            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();

            dataFile.write(byteNode, 0, byteNode.length);
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
