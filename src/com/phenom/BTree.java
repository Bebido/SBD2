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
                return true;
            } else {
                if (currentNode.kompensacja())
                    continue;
                else {
                    currentNode.add(rekord);
                    currentNode = currentNode.split();
                }
            }
        }
    }

    private void addFirstRekord(Rekord rekord) {
        Node root = new Node();
        //TODO: wszystko
    }

    public Rekord find(Integer key) {
        Rekord rekord = new Rekord(key);

        //jesli brak root lub rekord znajduje sie w ostatnio wczytanym wezle
        if (s == null) {
            currentNode = new Node();
            currentNode.root = true;
            h = 1;
            s = currentNode.myAddress;
            return null;
        }
        else if (currentNode != null){
            rekord = currentNode.findRekord(key);
            if (rekord != null){
                return rekord;
            }
        }

        Integer currentS = new Integer(s.intValue());
        Integer parentHelper = null;
        Boolean doSave;

        while(currentS != null){
            doSave = false;
            currentNode = new Node(currentS);
            if (currentNode.parentAdress != parentHelper || currentNode.myAddress != currentS){
                currentNode.parentAdress = parentHelper;
                currentNode.myAddress = currentS;
                doSave = true;
            }
            parentHelper = currentS;
            rekord = currentNode.findRekord(key);
            if (rekord != null)
                break;
            //wyszukanie mniejszych lub wiekszych
            else if (key.intValue() < currentNode.rekordList.get(0).getKey())
                currentS = currentNode.pointerList.get(0);
            else
                currentS = currentNode.getSuitPointer(key);
            if(doSave)
                currentNode.save();
        }

        return rekord;
    }
}
