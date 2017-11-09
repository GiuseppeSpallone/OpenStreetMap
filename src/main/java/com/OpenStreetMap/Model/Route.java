package com.OpenStreetMap.Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Route {

    private String name;
    private Percorso percorso;
    private double distanza;
    private Color color;

    //fermate
    private ArrayList<Combination> combinazioni;
    private int numFermate;
    private ArrayList<Node> fermate_effettive;
    private Combination minCombination;

    public static Route getRouteByName(HashSet<Route> routes, String name) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            if (route.getName().equals(name)) {
                return route;
            }
        }
        return null;
    }

    public static Route getRouteByNode(HashSet<Route> routes, Node node) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
                Node nd = it2.next();

                if (nd == node) {
                    return route;
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Percorso getPercorso() {
        return percorso;
    }

    public void setPercorso(Percorso percorso) {
        this.percorso = percorso;
    }

    public double getDistanza() {
        return distanza;
    }

    public void setDistanza(double distanza) {
        this.distanza = distanza;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumFermate() {
        return numFermate;
    }

    public void setNumFermate(int numFermate) {
        this.numFermate = numFermate;
    }

    public ArrayList<Combination> getCombinazioni() {
        return combinazioni;
    }

    public void setCombinazioni(ArrayList<Combination> combinazioni) {
        this.combinazioni = combinazioni;
    }

    public ArrayList<Node> getFermate_effettive() {
        return fermate_effettive;
    }

    public void setFermate_effettive(ArrayList<Node> fermate_effettive) {
        this.fermate_effettive = fermate_effettive;
    }

    public Combination getMinCombination() {
        return minCombination;
    }

    public void setMinCombination(Combination minCombination) {
        this.minCombination = minCombination;
    }
    
    

}
