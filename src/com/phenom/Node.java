package com.phenom;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Node {

    boolean root;
    public int m;
    public int d;
    public Integer parentAdress;
    List<Rekord> rekordList = new LinkedList<>();
    List<Integer> pointerList = new LinkedList<>();
    public Integer myAddress;

    Node(){
        this.root = false;
        this.m = 0;
        this.d = Globals.D;
        myAddress = -1;
    }

    Node(boolean root){
        if (root){
            this.root = true;
            this.m = 0;
            this.d = Globals.D;
            myAddress = -1;
        }
    }

    Node(Integer adress){

        byte[] nodeInBytes = new byte[Globals.getTreeHeader().rekordSize];
        ByteArrayInputStream bis = new ByteArrayInputStream(nodeInBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
            finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                // ignore close exception
            }
        }

    }

    public void add(Rekord rekord){
        if (this.rekordList.size() == 0) {
            pointerList.add(-1);
            pointerList.add(-1);
            rekordList.add(rekord);
            m++;
        } else {
            for (Rekord recordIt : rekordList) {
                if (rekord.getKey() < recordIt.getKey()) {
                    rekordList.add(rekordList.indexOf(recordIt), rekord);
                    pointerList.add(rekordList.indexOf(recordIt) + 1, -1 );
                    m++;
                    break;
                }
            }
        }
    }

    public void add(Integer pointer){
        this.pointerList.add(pointer);
    }


    public List<Rekord> getRekordList() {
        return rekordList;
    }

    public void setRekordList(List<Rekord> rekordList) {
        this.rekordList = rekordList;
    }

    public Rekord findRekord(Integer key) {
        for (Rekord rekord : rekordList){
            if (rekord.getKey().equals(key))
                return rekord;
            else if (rekord.getKey().intValue() > key.intValue())
                return null;
        }
        return null;
    }

    public void save(){
        try  {
            if(Globals.getTreeHeader() == null)
                Globals.initTreeHeader();

            //maksymalny rozmiar

            while (this.pointerList.size() < 2*d + 1){
                pointerList.add(-2);
            }
            while (this.rekordList.size() < 2*d ){
                rekordList.add(new Rekord(-2));
            }

            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;

            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();

            if (myAddress < 0){
                myAddress = Globals.getTreeHeader().writableAdressTree;
            } else {
                myAddress = Globals.getTreeHeader().calculateAdress();
            }

            //todo: sprawdzic z headertree.lenght;
            dataFile.write(byteNode, myAddress, byteNode.length);
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getSuitPointer(Integer key) {
        int i = -1;
        for (Rekord rekord : rekordList){
            i++;
            if (key > rekord.getKey())
                break;
        }
        return pointerList.get(i);
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public boolean kompensacja() {
        if (parentAdress == null)
            return false;   //root
        Node parentNode = new Node(parentAdress);
        Node sibling = null;
        //left sibling
        int myPosition = parentNode.pointerList.indexOf(this.myAddress);
        if (myPosition > 0 && parentNode.pointerList.get(myPosition - 1) != null){
            sibling = new Node(parentNode.pointerList.get(myPosition - 1 ));
            if (sibling.m < 2*sibling.d) {
                kompensujZ(parentNode, sibling, true);
                return true;
            }
        }
        if (myPosition < 2*d && parentNode.pointerList.get(myPosition + 1) != null){
            sibling = new Node(parentNode.pointerList.get(myPosition + 1));
            if (sibling.m < 2*sibling.d){
                kompensujZ(parentNode, sibling, false);
                return true;
            }
        }
        return false;
    }


    private void kompensujZ(Node parentNode, Node siblingNode, boolean isLeftSibling){
        List<Rekord> joinRekordy = new LinkedList<>();
        List<Integer> joinPointers = new LinkedList<>();
        Integer parentRekordPosition = 0;
        if (isLeftSibling){
            for(Rekord rekord : siblingNode.getRekordList()){
                joinRekordy.add(rekord);
            }
            for(Rekord rekord : parentNode.getRekordList()){
                if (rekord.getKey() > ((LinkedList<Rekord>) joinRekordy).getLast().getKey()){
                    joinRekordy.add(rekord);
                    parentRekordPosition = parentNode.getRekordList().indexOf(rekord);
                }
            }
            for(Rekord rekord : this.getRekordList()){
                joinRekordy.add(rekord);
            }

            for (Integer pointer : siblingNode.pointerList){
                joinPointers.add(pointer);
            }
            for (Integer pointer : this.pointerList){
                joinPointers.add(pointer);
            }
        } else {
            for(Rekord rekord : this.getRekordList()){
                joinRekordy.add(rekord);
            }
            for(Rekord rekord : parentNode.getRekordList()){
                if (rekord.getKey() > ((LinkedList<Rekord>) joinRekordy).getLast().getKey()){
                    joinRekordy.add(rekord);
                    parentRekordPosition = parentNode.getRekordList().indexOf(rekord);
                }
            }
            for(Rekord rekord : siblingNode.getRekordList()){
                joinRekordy.add(rekord);
            }

            for (Integer pointer : this.pointerList){
                joinPointers.add(pointer);
            }
            for (Integer pointer : siblingNode.pointerList){
                joinPointers.add(pointer);
            }
        }

        int podzial = joinRekordy.size()/2;
        siblingNode.pointerList = new LinkedList<>();
        siblingNode.rekordList = new LinkedList<>();
        siblingNode.m = 0;
        this.pointerList = new LinkedList<>();
        this.rekordList = new LinkedList<>();
        this.m = 0;

        if(isLeftSibling){
            siblingNode.add(joinPointers.get(0));
            for (int i = 0; i < podzial; i++){
                siblingNode.add(joinRekordy.get(i));
                siblingNode.pointerList.remove(pointerList.size());
                siblingNode.add(joinPointers.get(i+1));
            }
            siblingNode.save();

            parentNode.getRekordList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                this.add(joinRekordy.get(i));
                this.pointerList.remove(pointerList.size());
            }
            for(int i = podzial + 1; i < joinPointers.size(); i++){
                this.add(joinPointers.get(i));
            }
            this.save();
        } else {
            this.add(joinPointers.get(0));
            for (int i = 0; i < podzial; i++){
                this.add(joinRekordy.get(i));
                this.pointerList.remove(pointerList.size());
                this.add(joinPointers.get(i+1));
            }
            this.save();

            parentNode.getRekordList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                siblingNode.add(joinRekordy.get(i));
                siblingNode.pointerList.remove(pointerList.size());
            }
            for(int i = podzial + 1; i < joinPointers.size(); i++){
                siblingNode.add(joinPointers.get(i));
            }
            siblingNode.save();
        }
    }

    public Node split() {

        Node createdNode = new Node();
        Rekord middleRekord = new Rekord();
        int middleIndeks = this.m / 2;
        middleRekord.clone(this.rekordList.get(middleIndeks));

        createdNode.pointerList.add(this.pointerList.get(0));
        for(int i = 0; i < middleIndeks; i++){
            createdNode.rekordList.add(this.rekordList.get(i));
            createdNode.pointerList.add(this.pointerList.get(i+1));
        }

        this.rekordList.remove(0);
        for(int i = 0; i < middleIndeks; i++){
            this.rekordList.remove(0);
            this.pointerList.remove(0);
        }

        Node parentNode = null;
        boolean isRoot = this.root;
        if(isRoot){
            this.root = false;
            parentNode = new Node();
        } else{
            parentNode = new Node(this.parentAdress);
        }

        createdNode.save();
        this.save();

        parentNode.rekordList.add(middleRekord);
        parentNode.pointerList.add(rekordList.indexOf(middleRekord), createdNode.myAddress);
        parentNode.m++;
        if (isRoot)
            parentNode.pointerList.add(rekordList.indexOf(middleRekord) + 1, this.myAddress);

        boolean save = true;
        if (parentNode.m > 2*parentNode.d){
            parentNode.split();
            save = false;
        }

        if (save)
            parentNode.save();

        return parentNode;
    }
}
