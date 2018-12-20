package com.phenom;

public class TreeHeader {

    public Integer rekordSize;
    Integer rootAdress;
    Integer writableAdressTree;
    Integer writableAdressRekord;
    Integer[] reusableAdresses = new Integer[100];

    public Integer getRootAdress() {
        return rootAdress;
    }

    public void setRootAdress(Integer rootAdress) {
        this.rootAdress = rootAdress;
    }

    public Integer getWritableAdressTree() {
        return writableAdressTree;
    }

    public void setWritableAdressTree(Integer writableAdressTree) {
        this.writableAdressTree = writableAdressTree;
    }

    public Integer[] getReusableAdresses() {
        return reusableAdresses;
    }

    public void setReusableAdresses(Integer[] reusableAdresses) {
        this.reusableAdresses = reusableAdresses;
    }

    public Integer calculateAdress() {
        writableAdressTree = writableAdressTree + rekordSize;
        return writableAdressTree - rekordSize;
    }

    public Integer getRekordSize() {
        return rekordSize;
    }

    public void setRekordSize(Integer rekordSize) {
        this.rekordSize = rekordSize;
    }

    public Integer getWritableAdressRekord() {
        return writableAdressRekord;
    }

    public void setWritableAdressRekord(Integer writableAdressRekord) {
        this.writableAdressRekord = writableAdressRekord;
    }
}
