package com.phenom;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node implements Serializable{

    boolean root;
    public int m;
    public int d;
    public int parentAdress;
    List<Rekord> rekordList = new LinkedList<>();
    List<RekordAddress> pointerList = new LinkedList<>();
    public int myAddress;

    Node(){
        this.root = false;
        this.m = 0;
        this.d = Globals.D;
        myAddress = -1;
        parentAdress = -1;
    }

    Node(boolean root){
        if (root){
            this.root = true;
            this.m = 0;
            this.d = Globals.D;
            myAddress = -1;
            parentAdress = -1;
        }
    }

    Node(RekordAddress adress){

        byte[] nodeInBytes = new byte[Globals.getTreeHeader().getRekordSize()];
        try {
            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            dataFile.seek(adress.getValue());
            dataFile.read(nodeInBytes, 0, nodeInBytes.length);
            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(nodeInBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = (Node)in.readObject();
            this.myAddress = ((Node) o).myAddress;
            this.m = ((Node) o).m;
            this.pointerList = ((Node) o).pointerList;
            this.rekordList = ((Node) o).rekordList;
            this.root = ((Node) o).root;
            this.parentAdress = ((Node) o).parentAdress;
            this.d = ((Node) o).d;
            this.cleanDummyValues();
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

    public void add(Rekord rekord){
        boolean added = false;

        if (this.rekordList.size() == 0) {
            pointerList.add(new RekordAddress(-1));
            pointerList.add(new RekordAddress(-1));
            rekordList.add(rekord);
            m++;
            added = true;
        } else {
            int position = -1;
            for (Rekord recordIt : rekordList) {
                if (rekord.getKey() < recordIt.getKey() || recordIt.getKey() < 0) {
                    position = rekordList.indexOf(recordIt);
                    break;
                }
            }
            if (position >= 0){
                rekordList.add(position, rekord);
                pointerList.add(position + 1, new RekordAddress(-1));
                m++;
                added = true;
            }
        }

        if (!added){
            rekordList.add(rekord);
            pointerList.add(new RekordAddress(-1));
            m++;
        }
    }

    public void add(RekordAddress pointer){
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
            //maksymalny rozmiar

            //int i = -20;
            while (this.pointerList.size() < 2*d + 1){
                pointerList.add(new RekordAddress(-2));
            }
            while (this.rekordList.size() < 2*d ){
                rekordList.add(new Rekord(-2));
            }

            if (myAddress < 0){
                myAddress = Globals.getTreeHeader().getAddressToSaveTree();
            }
//            Node nodeToSave = new Node();
//            nodeToSave.clone(this);

            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            out.close();



            //Globals.getTreeHeader().getRekordSize();//todo: sprawdzic z headertree.lenght;
            dataFile.seek(myAddress);
            dataFile.write(byteNode, 0, Globals.getTreeHeader().getRekordSize());
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        this.cleanDummyValues();
    }

    private void clone(Node node) {
        this.parentAdress = node.parentAdress;
        this.root = node.root;
        this.myAddress = node.myAddress;
        this.m = node.m;
        this.d = node.d;
        for (Rekord rekord : node.rekordList){
            this.rekordList.add(rekord);
        }
        for (RekordAddress pointer : node.pointerList){
            this.pointerList.add(pointer);
        }
    }

    public RekordAddress getRightSidePointer(int key) {
        int i = 0;
        for (Rekord rekord : rekordList){
            if (key < rekord.getKey() || rekord.getKey() < 0)
                break;
            i++;
        }
        return pointerList.get(i);
    }

    public long getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public long getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public boolean kompensacja() {
        if (parentAdress < 0)
            return false;   //root
        Node parentNode = new Node(new RekordAddress(parentAdress));
        Node sibling = null;
        //left sibling
        int myPosition = 0;
        for (RekordAddress pointer : parentNode.pointerList){
            if (pointer.getValue() == this.myAddress){
                break;
            }
            myPosition++;
        }
        if (myPosition > 0 && parentNode.pointerList.get(myPosition - 1).getValue() > 0){
            sibling = new Node(parentNode.pointerList.get(myPosition - 1 ));
            if (sibling.m < 2*sibling.d) {
                kompensujZ(parentNode, sibling, true);
                return true;
            }
        }
        if (myPosition < 2*d && parentNode.pointerList.size() > myPosition + 1 && parentNode.pointerList.get(myPosition + 1).getValue() > 0){
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
        List<RekordAddress> joinPointers = new LinkedList<>();
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

            for (RekordAddress pointer : siblingNode.pointerList){
                joinPointers.add(pointer);
            }
            for (RekordAddress pointer : this.pointerList){
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

            for (RekordAddress pointer : this.pointerList){
                joinPointers.add(pointer);
            }
            for (RekordAddress pointer : siblingNode.pointerList){
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
                siblingNode.rekordList.add(joinRekordy.get(i));
                siblingNode.pointerList.add(joinPointers.get(i+1));
                siblingNode.m++;
            }
            siblingNode.save();

            parentNode.getRekordList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                this.rekordList.add(joinRekordy.get(i));
                this.m++;
            }
            for(int i = podzial + 1; i < joinPointers.size(); i++){
                this.pointerList.add(joinPointers.get(i));
            }
            this.save();
        } else {
            this.pointerList.add(joinPointers.get(0));
            for (int i = 0; i < podzial; i++){
                this.rekordList.add(joinRekordy.get(i));
                this.pointerList.add(joinPointers.get(i+1));
                this.m++;
            }
            this.save();

            parentNode.getRekordList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                siblingNode.rekordList.add(joinRekordy.get(i));
                siblingNode.m++;
            }
            for(int i = podzial + 1; i < joinPointers.size(); i++){
                siblingNode.pointerList.add(joinPointers.get(i));
            }
            siblingNode.save();
        }
    }

    public Node split() {

        Node createdNode = new Node();
        Rekord middleRekord = new Rekord();
        Integer middleIndeks = this.m / 2;
        middleRekord.clone(this.rekordList.get(middleIndeks));

        createdNode.pointerList.add(this.pointerList.get(0));
        for(int i = 0; i < middleIndeks; i++){
            createdNode.rekordList.add(this.rekordList.get(i));
            createdNode.pointerList.add(this.pointerList.get(i+1));
            createdNode.m++;
        }

        for(int i = 0; i <= middleIndeks; i++){
            this.rekordList.remove(0);
            this.pointerList.remove(0);
            m--;
        }

        Node parentNode = null;
        boolean isRoot = this.root;
        if(isRoot){
            this.root = false;
            parentNode = new Node();
            parentNode.root = true;
            parentNode.myAddress = Globals.getTreeHeader().getAddressToSaveTree();
            this.parentAdress = parentNode.myAddress;
        } else{
            parentNode = new Node(new RekordAddress(this.parentAdress));
        }

        createdNode.parentAdress = parentNode.myAddress;
        createdNode.save();
        this.save();

        if(isRoot) {
            parentNode.rekordList.add(middleRekord);
            parentNode.pointerList.add(new RekordAddress(createdNode.myAddress));
            parentNode.pointerList.add(new RekordAddress(this.myAddress));
            parentNode.m++;
        } else {
            parentNode.add(middleRekord);
            int rekordPosition = parentNode.rekordList.indexOf(middleRekord);
            parentNode.pointerList.remove(rekordPosition + 1);
            parentNode.pointerList.add(rekordPosition, new RekordAddress(createdNode.myAddress));
        }

        boolean save = true;
        if (parentNode.getM() > 2*parentNode.getD()){
            parentNode = parentNode.split();
            save = false;
        }

        if (save)
            parentNode.save();
        else
            parentNode.cleanDummyValues();

        return parentNode;
    }

    public void cleanDummyValues(){
        List<Rekord> rekordsToRemove = new ArrayList<>();
        List<RekordAddress> rekordsAddressesToRemove = new ArrayList<>();

        for (Rekord rekord : rekordList){
            if (rekord.getKey() < -1)
                rekordsToRemove.add(rekord);
        }

        for (RekordAddress pointer : pointerList){
            if (pointer.getValue() < -1)
                rekordsAddressesToRemove.add(pointer);
        }

        for (Rekord rekord : rekordsToRemove){
                rekordList.remove(rekord);
        }

        for (RekordAddress pointer : rekordsAddressesToRemove){
                pointerList.remove(pointer);
        }
    }

    @Override
    public String toString() {
        String textNode = " ";
        for(int i = 0; i < rekordList.size(); i++){
            textNode = textNode + "|" +pointerList.get(i).getValue() + "|" + rekordList.get(i).getKey();
        }
        textNode = textNode + "|" + pointerList.get(pointerList.size() - 1).getValue() + "| ";
        return textNode;
    }
}
