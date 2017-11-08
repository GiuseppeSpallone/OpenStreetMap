package com.OpenStreetMap.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Combination {

    private Route route;
    private ArrayList<Node> stops;
    private HashMap<Node, Percorso> students;
    private double value;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public ArrayList<Node> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Node> stops) {
        this.stops = stops;
    }

    public HashMap<Node, Percorso> getStudents() {
        return students;
    }

    public void setStudents(HashMap<Node, Percorso> students) {
        this.students = students;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static boolean isStops(HashSet<Combination> combinations, ArrayList<Node> stops) {
        boolean is = false;

        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();
            ArrayList<Node> stops_combination = combination.getStops();
            
            if (stops_combination.equals(stops)) {
                is = true;
            }
        }
        return is;
    }

}
