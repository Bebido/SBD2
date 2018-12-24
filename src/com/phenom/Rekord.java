package com.phenom;

import java.io.*;
import java.util.Random;

public class Rekord implements Serializable {

    int key;
    float liczba1, liczba2, liczba3, liczba4, liczba5;
    boolean deleted;

    Rekord(){
        this.key = -1;
    }

    Rekord(int key){
        this.key = key;
        generateValues();
        deleted = false;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public float getLiczba1() {
        return liczba1;
    }

    public void setLiczba1(float liczba1) {
        this.liczba1 = liczba1;
    }

    public float getLiczba2() {
        return liczba2;
    }

    public void setLiczba2(float liczba2) {
        this.liczba2 = liczba2;
    }

    public float getLiczba3() {
        return liczba3;
    }

    public void setLiczba3(float liczba3) {
        this.liczba3 = liczba3;
    }

    public float getLiczba4() {
        return liczba4;
    }

    public void setLiczba4(float liczba4) {
        this.liczba4 = liczba4;
    }

    public float getLiczba5() {
        return liczba5;
    }

    public void setLiczba5(float liczba5) {
        this.liczba5 = liczba5;
    }

    float getValue(){
        return (liczba1 + liczba2 + liczba3 + liczba4 + liczba5) / 5;
    }

    @Override
    public String toString(){
        String s = "K: " + this.getKey() + " V: " + this.getValue() + " L: " + this.getLiczba1() + " " +
                this.getLiczba2() + " " + this.getLiczba3() + " " + this.getLiczba4() + " " + this.getLiczba5();
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

    public void clone(Rekord rekord) {
        this.key = rekord.key;
        this.liczba1 = rekord.getLiczba1();
        this.liczba2 = rekord.getLiczba2();
        this.liczba3 = rekord.getLiczba3();
        this.liczba4 = rekord.getLiczba4();
        this.liczba5 = rekord.getLiczba5();
    }

    public int save() {

        int myAddress = 0;
        try  {
            myAddress = Globals.getTreeHeader().getAddressToSaveData();

            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteRekord = bos.toByteArray();
            bos.close();
            out.close();

            dataFile.seek(myAddress);
            dataFile.write(byteRekord, 0, Globals.getTreeHeader().getRekordSize());
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        Globals.addZapis();
        return myAddress;
    }

    public void load(int recordAddress) {

        byte[] recordInBytes = new byte[Globals.getTreeHeader().getRekordSize()];

        try {
            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            dataFile.seek(recordAddress);
            dataFile.read(recordInBytes, 0, recordInBytes.length);
            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(recordInBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Rekord o = (Rekord) in.readObject();
            this.clone(o);
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
        Globals.addOdczyt();
    }

    public void delete(int recordAddress) {
        this.load(recordAddress);
        this.deleted = true;
        this.save(recordAddress);
        Globals.getTreeHeader().addReusableAddressData(recordAddress);
    }

    public void save(int address) {

        try  {
            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteRekord = bos.toByteArray();
            bos.close();
            out.close();

            dataFile.seek(address);
            dataFile.write(byteRekord, 0, Globals.getTreeHeader().getRekordSize());
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
