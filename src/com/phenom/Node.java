package com.phenom;

import java.io.*;
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

        byte[] nodeInBytes = new byte[Globals.getTreeHeader().rekordSize];
        try {
            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            dataFile.read(nodeInBytes, adress.getValue(), Globals.getTreeHeader().getRekordSize());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            for (Rekord recordIt : rekordList) {
                if (rekord.getKey() < recordIt.getKey()) {
                    rekordList.add(rekordList.indexOf(recordIt), rekord);
                    pointerList.add(rekordList.indexOf(recordIt) + 1, new RekordAddress(-1));
                    m++;
                    added = true;
                    break;
                }
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
            if(Globals.getTreeHeader() == null)
                Globals.initTreeHeader();

            //maksymalny rozmiar

            int i = -20;
            while (this.pointerList.size() < 2*d + 1){
                pointerList.add(new RekordAddress(-1));
            }
            while (this.rekordList.size() < 2*d ){
                rekordList.add(new Rekord(-1));
            }

            Node nodeToSave = new Node();
            nodeToSave.clone(this);

            RandomAccessFile dataFile = new RandomAccessFile(Globals.DATA_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(nodeToSave);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();

            if (myAddress < 0){
                myAddress = Globals.getTreeHeader().writableAdressTree;
            } else {
                myAddress = Globals.getTreeHeader().calculateAdress();
            }

            //Globals.getTreeHeader().rekordSize;//todo: sprawdzic z headertree.lenght;
            dataFile.write(byteNode, myAddress, byteNode.length);
            dataFile.close();
        } catch (Exception e){
            e.printStackTrace();
        }
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

    public RekordAddress getSuitPointer(Integer key) {
        int i = -1;
        for (Rekord rekord : rekordList){
            i++;
            if (key > rekord.getKey())
                break;
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
        if (parentAdress == -1)
            return false;   //root
        Node parentNode = new Node(new RekordAddress(parentAdress));
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
        Integer middleIndeks = this.m / 2;
        middleRekord.clone(this.rekordList.get(middleIndeks));
        rekordList.remove(middleIndeks);

        createdNode.pointerList.add(this.pointerList.get(0));
        for(int i = 0; i < middleIndeks; i++){
            createdNode.rekordList.add(this.rekordList.get(i));
            createdNode.pointerList.add(this.pointerList.get(i+1));
            createdNode.m++;
        }


        this.pointerList.remove(0);
        for(int i = 0; i < middleIndeks; i++){
            this.rekordList.remove(0);
            this.pointerList.remove(0);
            m--;
        }

        Node parentNode = null;  //todo: skopiowac parent node
        boolean isRoot = this.root;
        if(isRoot){
            this.root = false;
            parentNode = new Node();
        } else{
            parentNode = new Node(new RekordAddress(this.parentAdress));
        }

        createdNode.save();
        this.save();

        parentNode.rekordList.add(middleRekord);
        parentNode.pointerList.add(middleIndeks, new RekordAddress(createdNode.myAddress));
        parentNode.m++;
        if (isRoot)
            parentNode.pointerList.add(rekordList.indexOf(middleRekord) + 1, new RekordAddress(this.myAddress));

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
