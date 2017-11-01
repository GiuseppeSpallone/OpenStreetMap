package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Percorso {
    private double distanza;
    private ArrayList<Node> nodes;

    public double getDistanza() {
        return distanza;
    }

    public void setDistanza(double distanza) {
        this.distanza = distanza;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
    
    
    
}
