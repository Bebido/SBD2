package com.phenom;

public class TreeHeader {

    Integer rootAdress;
    Integer writableAdress;
    Integer[] reusableAdresses = new Integer[100];

    public Integer getRootAdress() {
        return rootAdress;
    }

    public void setRootAdress(Integer rootAdress) {
        this.rootAdress = rootAdress;
    }

    public Integer getWritableAdress() {
        return writableAdress;
    }

    public void setWritableAdress(Integer writableAdress) {
        this.writableAdress = writableAdress;
    }

    public Integer[] getReusableAdresses() {
        return reusableAdresses;
    }

    public void setReusableAdresses(Integer[] reusableAdresses) {
        this.reusableAdresses = reusableAdresses;
    }
}
