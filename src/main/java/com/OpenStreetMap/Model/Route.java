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
    private int numFermate;
    private ArrayList<Node> fermate = null;

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

    public ArrayList<Node> getFermate() {
        return fermate;
    }

    public void setFermate(ArrayList<Node> fermate) {
        this.fermate = fermate;
    }

}
