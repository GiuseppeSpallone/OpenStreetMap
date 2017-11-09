package com.OpenStreetMap.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Combination {

    private ArrayList<Node> fermate;
    private HashMap<Node, Percorso> minPercorsoFermata; //per ciascun studente il percorso verso la fermata più vicina
    private double value;

    public ArrayList<Node> getFermate() {
        return fermate;
    }

    public void setFermate(ArrayList<Node> fermate) {
        this.fermate = fermate;
    }

    public HashMap<Node, Percorso> getMinPercorsoFermata() {
        return minPercorsoFermata;
    }

    public void setMinPercorsoFermata(HashMap<Node, Percorso> minPercorsoFermata) {
        this.minPercorsoFermata = minPercorsoFermata;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static boolean isStops(ArrayList<Combination> combinations, ArrayList<Node> stops) {
        boolean is = false;

        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();
            ArrayList<Node> combination_stop = combination.getFermate();

            //aggiungere anche per array inverso
            if (combination_stop.equals(stops)) {
                is = true;
            }
        }
        return is;
    }

}
