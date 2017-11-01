package com.Bus.Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Route {
    private String name;
    private ArrayList<Node> nodes;
    private double distanza;
    private Color color;

    public static Route getRouteByName(HashSet<Route> routes, String name) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
            Route route = it.next();

            if (route.getName().equals(name)) {
                return route;
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

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
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
}
