package com.phenom;

import java.io.Serializable;
import java.util.Random;

public class RekordNode implements Serializable {

    int key;
    float liczba1, liczba2, liczba3, liczba4, liczba5;

    RekordNode(){

    }

    RekordNode(int key){
        this.key = key;
        generateValues();
    }

    Float getValue(){
        return (liczba1 + liczba2 + liczba3 + liczba4 + liczba5) / 5;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Float getLiczba1() {
        return liczba1;
    }

    public void setLiczba1(float liczba1) {
        this.liczba1 = liczba1;
    }

    public Float getLiczba2() {
        return liczba2;
    }

    public void setLiczba2(float liczba2) {
        this.liczba2 = liczba2;
    }

    public Float getLiczba3() {
        return liczba3;
    }

    public void setLiczba3(float liczba3) {
        this.liczba3 = liczba3;
    }

    public Float getLiczba4() {
        return liczba4;
    }

    public void setLiczba4(float liczba4) {
        this.liczba4 = liczba4;
    }

    public Float getLiczba5() {
        return liczba5;
    }

    public void setLiczba5(float liczba5) {
        this.liczba5 = liczba5;
    }

    @Override
    public String toString(){
        String s = this.getKey().toString() + " " + this.getValue().toString();
        return s;
    }

    public void generateValues(){
        Random random = new Random();
        liczba1 = random.nextFloat() * 1000f;
        liczba2 = random.nextFloat() * 1000f;
        liczba3 = random.nextFloat() * 1000f;
        liczba4 = random.nextFloat() * 1000f;
        liczba5 = random.nextFloat() * 1000f;
    }

    public void clone(RekordNode rekordNode) {
        this.key = rekordNode.key;
        this.liczba1 = rekordNode.getLiczba1();
        this.liczba2 = rekordNode.getLiczba2();
        this.liczba3 = rekordNode.getLiczba3();
        this.liczba4 = rekordNode.getLiczba4();
        this.liczba5 = rekordNode.getLiczba5();
    }
}
