package com.phenom;

import java.io.Serializable;

public class NodesAddress implements Serializable {

    int value;

    NodesAddress(int address){
        this.value = address;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
