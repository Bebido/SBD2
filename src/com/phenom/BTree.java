package com.phenom;

public class BTree {

    int h = 0;
    Integer s = null;

    Node currentNode = null;

    public boolean add(int key){

        Rekord rekord = find(key);
        if (rekord != null)
            return false;   //rekord istnieje w pliku

        rekord = new Rekord(key);
        while(true) {
            if (currentNode.getM() < 2 * currentNode.getD()) {
                currentNode.add(rekord);
                currentNode.save();
                return true;
            } else {
                if (currentNode.kompensacja())
                    continue;
                else {
                    currentNode.add(rekord);
                    currentNode = currentNode.split();
                    if (currentNode.root){

                    }
                }
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
            currentNode = new Node(new RekordAddress(currentS));
//            if (currentNode.parentAdress != parentHelper){
//                currentNode.parentAdress = parentHelper;
//                currentNode.save();
//            }
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
}
