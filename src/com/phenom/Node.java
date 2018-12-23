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
    List<RekordNode> rekordNodeList = new LinkedList<>();
    List<NodesAddress> pointerList = new LinkedList<>();
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

    Node(NodesAddress adress){

        byte[] nodeInBytes = new byte[Globals.getTreeHeader().getNodeSize()];
        try {
            RandomAccessFile treeFile = new RandomAccessFile(Globals.TREE_FILE, "rw");
            treeFile.seek(adress.getValue());
            treeFile.read(nodeInBytes, 0, nodeInBytes.length);
            treeFile.close();
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
            this.rekordNodeList = ((Node) o).rekordNodeList;
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

    public void add(RekordNode rekordNode){
        boolean added = false;

        if (this.rekordNodeList.size() == 0) {
            pointerList.add(new NodesAddress(-1));
            pointerList.add(new NodesAddress(-1));
            rekordNodeList.add(rekordNode);
            m++;
            added = true;
        } else {
            int position = -1;
            for (RekordNode recordIt : rekordNodeList) {
                if (rekordNode.getKey() < recordIt.getKey() || recordIt.getKey() < 0) {
                    position = rekordNodeList.indexOf(recordIt);
                    break;
                }
            }
            if (position >= 0){
                rekordNodeList.add(position, rekordNode);
                pointerList.add(position + 1, new NodesAddress(-1));
                m++;
                added = true;
            }
        }

        if (!added){
            rekordNodeList.add(rekordNode);
            pointerList.add(new NodesAddress(-1));
            m++;
        }
    }

    public void add(NodesAddress pointer){
        this.pointerList.add(pointer);
    }


    public List<RekordNode> getRekordNodeList() {
        return rekordNodeList;
    }

    public void setRekordNodeList(List<RekordNode> rekordNodeList) {
        this.rekordNodeList = rekordNodeList;
    }

    public RekordNode findRekord(Integer key) {
        for (RekordNode rekordNode : rekordNodeList){
            if (rekordNode.getKey().equals(key))
                return rekordNode;
            else if (rekordNode.getKey().intValue() > key.intValue())
                return null;
        }
        return null;
    }

    public void save(){
        try  {

            while (this.pointerList.size() < 2*d + 1){
                pointerList.add(new NodesAddress(-2));
            }
            while (this.rekordNodeList.size() < 2*d ){
                rekordNodeList.add(new RekordNode(-2));
            }

            if (myAddress < 0){
                myAddress = Globals.getTreeHeader().getAddressToSaveTree();
            }

            RandomAccessFile treeFile = new RandomAccessFile(Globals.TREE_FILE, "rw");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            byte[] byteNode = bos.toByteArray();
            bos.close();
            out.close();

            treeFile.seek(myAddress);
            treeFile.write(byteNode, 0, Globals.getTreeHeader().getNodeSize());
            treeFile.close();
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
        for (RekordNode rekordNode : node.rekordNodeList){
            this.rekordNodeList.add(rekordNode);
        }
        for (NodesAddress pointer : node.pointerList){
            this.pointerList.add(pointer);
        }
    }

    public NodesAddress getRightSidePointer(int key) {
        int i = 0;
        for (RekordNode rekordNode : rekordNodeList){
            if (key < rekordNode.getKey() || rekordNode.getKey() < 0)
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
        Node parentNode = new Node(new NodesAddress(parentAdress));
        Node sibling = null;
        //left sibling
        int myPosition = 0;
        for (NodesAddress pointer : parentNode.pointerList){
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
        List<RekordNode> joinRekordy = new LinkedList<>();
        List<NodesAddress> joinPointers = new LinkedList<>();
        Integer parentRekordPosition = 0;
        if (isLeftSibling){
            for(RekordNode rekordNode : siblingNode.getRekordNodeList()){
                joinRekordy.add(rekordNode);
            }
            for(RekordNode rekordNode : parentNode.getRekordNodeList()){
                if (rekordNode.getKey() > ((LinkedList<RekordNode>) joinRekordy).getLast().getKey()){
                    joinRekordy.add(rekordNode);
                    parentRekordPosition = parentNode.getRekordNodeList().indexOf(rekordNode);
                }
            }
            for(RekordNode rekordNode : this.getRekordNodeList()){
                joinRekordy.add(rekordNode);
            }

            for (NodesAddress pointer : siblingNode.pointerList){
                joinPointers.add(pointer);
            }
            for (NodesAddress pointer : this.pointerList){
                joinPointers.add(pointer);
            }
        } else {
            for(RekordNode rekordNode : this.getRekordNodeList()){
                joinRekordy.add(rekordNode);
            }
            for(RekordNode rekordNode : parentNode.getRekordNodeList()){
                if (rekordNode.getKey() > ((LinkedList<RekordNode>) joinRekordy).getLast().getKey()){
                    joinRekordy.add(rekordNode);
                    parentRekordPosition = parentNode.getRekordNodeList().indexOf(rekordNode);
                }
            }
            for(RekordNode rekordNode : siblingNode.getRekordNodeList()){
                joinRekordy.add(rekordNode);
            }

            for (NodesAddress pointer : this.pointerList){
                joinPointers.add(pointer);
            }
            for (NodesAddress pointer : siblingNode.pointerList){
                joinPointers.add(pointer);
            }
        }

        int podzial = joinRekordy.size()/2;
        siblingNode.pointerList = new LinkedList<>();
        siblingNode.rekordNodeList = new LinkedList<>();
        siblingNode.m = 0;
        this.pointerList = new LinkedList<>();
        this.rekordNodeList = new LinkedList<>();
        this.m = 0;

        if(isLeftSibling){
            siblingNode.add(joinPointers.get(0));
            for (int i = 0; i < podzial; i++){
                siblingNode.rekordNodeList.add(joinRekordy.get(i));
                siblingNode.pointerList.add(joinPointers.get(i+1));
                siblingNode.m++;
            }
            siblingNode.save();

            parentNode.getRekordNodeList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                this.rekordNodeList.add(joinRekordy.get(i));
                this.m++;
            }
            for(int i = podzial + 1; i < joinPointers.size(); i++){
                this.pointerList.add(joinPointers.get(i));
            }
            this.save();
        } else {
            this.pointerList.add(joinPointers.get(0));
            for (int i = 0; i < podzial; i++){
                this.rekordNodeList.add(joinRekordy.get(i));
                this.pointerList.add(joinPointers.get(i+1));
                this.m++;
            }
            this.save();

            parentNode.getRekordNodeList().set(parentRekordPosition, joinRekordy.get(podzial));
            parentNode.save();

            for(int i = podzial + 1; i < joinRekordy.size(); i++){
                siblingNode.rekordNodeList.add(joinRekordy.get(i));
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
        RekordNode middleRekordNode = new RekordNode();
        Integer middleIndeks = this.m / 2;
        middleRekordNode.clone(this.rekordNodeList.get(middleIndeks));

        createdNode.pointerList.add(this.pointerList.get(0));
        for(int i = 0; i < middleIndeks; i++){
            createdNode.rekordNodeList.add(this.rekordNodeList.get(i));
            createdNode.pointerList.add(this.pointerList.get(i+1));
            createdNode.m++;
        }

        for(int i = 0; i <= middleIndeks; i++){
            this.rekordNodeList.remove(0);
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
            parentNode = new Node(new NodesAddress(this.parentAdress));
        }

        createdNode.parentAdress = parentNode.myAddress;
        createdNode.save();
        this.save();

        if(isRoot) {
            parentNode.rekordNodeList.add(middleRekordNode);
            parentNode.pointerList.add(new NodesAddress(createdNode.myAddress));
            parentNode.pointerList.add(new NodesAddress(this.myAddress));
            parentNode.m++;
        } else {
            parentNode.add(middleRekordNode);
            int rekordPosition = parentNode.rekordNodeList.indexOf(middleRekordNode);
            parentNode.pointerList.remove(rekordPosition + 1);
            parentNode.pointerList.add(rekordPosition, new NodesAddress(createdNode.myAddress));
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
        List<RekordNode> rekordsToRemove = new ArrayList<>();
        List<NodesAddress> rekordsAddressesToRemove = new ArrayList<>();

        for (RekordNode rekordNode : rekordNodeList){
            if (rekordNode.getKey() < -1)
                rekordsToRemove.add(rekordNode);
        }

        for (NodesAddress pointer : pointerList){
            if (pointer.getValue() < -1)
                rekordsAddressesToRemove.add(pointer);
        }

        for (RekordNode rekordNode : rekordsToRemove){
                rekordNodeList.remove(rekordNode);
        }

        for (NodesAddress pointer : rekordsAddressesToRemove){
                pointerList.remove(pointer);
        }
    }

    @Override
    public String toString() {
        String textNode = " ";
        for(int i = 0; i < rekordNodeList.size(); i++){
            textNode = textNode + "|" +pointerList.get(i).getValue() + "|" + rekordNodeList.get(i).getKey();
        }
        textNode = textNode + "|" + pointerList.get(pointerList.size() - 1).getValue() + "| ";
        return textNode;
    }
}
