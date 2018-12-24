package com.phenom;

import java.util.LinkedList;
import java.util.List;

public class BTree {

    int h = 0;
    Integer s = null;
    Node currentNode = null;

    public boolean add(int key) {

        RekordNode rekordNode = find(key);
        if (rekordNode != null)
            return false;   //rekordNode istnieje w pliku

        Rekord rekord = new Rekord(key);
        int rekordAddress = rekord.save();
        rekordNode = new RekordNode(key, rekordAddress);
        currentNode.add(rekordNode);
        if (currentNode.getM() <= 2 * currentNode.getD()) {
            currentNode.save();
            return true;
        } else {
            if (currentNode.kompensacja())
                return true;
            else {
                currentNode = currentNode.split();
                if (currentNode.root) {
                    s = currentNode.myAddress;
                }
                return true;
            }
        }
    }

    public RekordNode find(Integer key) {
        RekordNode rekordNode = new RekordNode(key);

        //jesli brak root lub rekordNode znajduje sie w ostatnio wczytanym wezle
        if (s == null) {
            currentNode = new Node();
            currentNode.root = true;
            h = 1;
            currentNode.myAddress = Globals.getTreeHeader().getAddressToSaveTree();
            s = currentNode.myAddress;
            currentNode.save();
            return null;
        }

        // wczytywanie od root
        int currentS = s.intValue();
        int parentHelper = -5;

        while(currentS > 0){
            currentNode = new Node(new NodesAddress(currentS));
            if (currentNode.parentAdress > 0 && currentNode.parentAdress != parentHelper){
                currentNode.parentAdress = parentHelper;
                currentNode.save();
            }
            parentHelper = currentS;
            rekordNode = currentNode.findRekord(key);
            if (rekordNode != null)
                break;
            if (currentNode.rekordNodeList.size() == 0)
                break;
            //wyszukanie mniejszych lub wiekszych
            else if (currentNode.rekordNodeList.get(0).getKey() >= 0 && key.intValue() < currentNode.rekordNodeList.get(0).getKey())
                currentS = currentNode.pointerList.get(0).getValue();
            else
                currentS = currentNode.getRightSidePointer(key.intValue()).getValue();
        }

        return rekordNode;
    }

    public void display() {
        if (s == null){
            System.out.println("Baza jest pusta");
            return;
        }
        int currentS = s.intValue();
        List<NodesAddress> addressesToDisplay = new LinkedList<>();
        addressesToDisplay.add(new NodesAddress(-20));

        while(currentS > 0) {
            currentNode = new Node(new NodesAddress(currentS));
            System.out.print(currentNode.toString());
            for (NodesAddress pointer : currentNode.pointerList){
                if (pointer.getValue() > 0)
                    addressesToDisplay.add(pointer);
            }
            if (addressesToDisplay.size() > 0) {
                currentS = addressesToDisplay.get(0).getValue();
                if (currentS == -20){
                    System.out.println();
                    addressesToDisplay.remove(0);
                    if(addressesToDisplay.size() == 0)
                        break;
                    currentS = addressesToDisplay.get(0).getValue();
                    if (addressesToDisplay.size() > 0){
                        addressesToDisplay.add(new NodesAddress(-20));
                    }
                }
                addressesToDisplay.remove(0);
            } else {
                currentS = -1;
            }
        }
    }

    public void setFromHeader(){
        this.s = Globals.getTreeHeader().getRootAdress();
    }

    public void setHeader(){
        Globals.getTreeHeader().setRootAdress(this.s.intValue());
    }

    public Rekord getRekord(Integer key) {

        Rekord rekord = new Rekord();
        RekordNode rekordNode = find(key);
        if(rekordNode == null){
            return null;
        } else {
          rekord.load(rekordNode.getRecordAddress());
        }

        return rekord;
    }

    public boolean delete(Integer key) {
        Rekord rekord = new Rekord();
        RekordNode rekordNode = find(key);
        if(rekordNode == null)
            return false;

        rekord.delete(rekordNode.getRecordAddress());

        if (currentNode.isLeaf()){
            currentNode = currentNode.deleteFromLeaf(rekordNode);
        } else {
            currentNode = currentNode.delete(rekordNode);
        }

        //currentNode is now leaf with deleted record

        if (currentNode.getM() >= Globals.D)
            return true;
        else {
            while(true) {
                if (currentNode.kompensacjaDelete())
                    return true;
                else {
                    currentNode = currentNode.scalenie();
                    if (currentNode == null)
                        return true;
                    if (currentNode.root){
                        h--;
                        s = currentNode.myAddress;
                        return true;
                    }
                    if (!(currentNode.getM() < Globals.D)) //niedomiar
                        return true;
                }
            }
        }
    }
}
