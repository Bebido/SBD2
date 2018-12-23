package com.phenom;

import java.util.LinkedList;
import java.util.List;

public class BTree {

    int h = 0;
    Integer s = null;

    Node currentNode = null;

    public boolean add(int key) {

        Rekord rekord = find(key);
        if (rekord != null)
            return false;   //rekord istnieje w pliku

        rekord = new Rekord(key);
        currentNode.add(rekord);
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


    public Rekord find(Integer key) {
        Rekord rekord = new Rekord(key);

        //jesli brak root lub rekord znajduje sie w ostatnio wczytanym wezle
        if (s == null) {
            currentNode = new Node();
            currentNode.root = true;
            h = 1;
            currentNode.myAddress = Globals.getTreeHeader().getAddressToSaveTree();
            s = currentNode.myAddress;
            currentNode.save();
            return null;
        }
//        else if (currentNode != null){
//            rekord = currentNode.findRekord(key);
//            if (rekord != null){
//                return rekord;
//            }
//        }

        // wczytywanie od root
        int currentS = s.intValue();
        int parentHelper = -5;
        //boolean doSave;

        while(currentS != -1){
            //doSave = false;
            currentNode = new Node(new NodesAddress(currentS));
            parentHelper = currentS;
            rekord = currentNode.findRekord(key);
            if (rekord != null)
                break;
            //wyszukanie mniejszych lub wiekszych
            else if (currentNode.rekordList.get(0).getKey() >= 0 && key.intValue() < currentNode.rekordList.get(0).getKey())
                currentS = currentNode.pointerList.get(0).getValue();
            else
                currentS = currentNode.getRightSidePointer(key.intValue()).getValue();
        }

        return rekord;
    }

    public void display() {
        int currentS = s.intValue();
        List<NodesAddress> addressesToDisplay = new LinkedList<>();
        addressesToDisplay.add(new NodesAddress(-20));

        while(currentS > 0) {
            //doSave = false;
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
}
