package com.phenom;

import java.io.Serializable;

public class RekordAddress implements Serializable {

    int value;

    RekordAddress(int address){
        this.value = address;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
