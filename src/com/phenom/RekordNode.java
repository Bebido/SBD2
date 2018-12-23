package com.phenom;

import java.io.Serializable;

public class RekordNode implements Serializable {

    int key;
    int recordAddress;

    RekordNode(){
    }

    RekordNode(int key){
        this.key = key;
        this.recordAddress = -1;
    }

    RekordNode(int key, int recordAddress){
        this.key = key;
        this.recordAddress = recordAddress;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getRecordAddress() {
        return recordAddress;
    }

    public void setRecordAddress(int recordAddress) {
        this.recordAddress = recordAddress;
    }

    public void clone(RekordNode rekordNode){
        this.key = rekordNode.key;
        this.recordAddress = rekordNode.recordAddress;
    }
}