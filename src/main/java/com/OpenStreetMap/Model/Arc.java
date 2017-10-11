package com.OpenStreetMap.Model;

public class Arc {

    private Node from;
    private Node to;
    private double length;

    public Arc(Node from, Node to) {
        this.from = from;
        this.to = to;
        length = from.distanzaLatLog(to);
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom() {
        return from;
    }

    public double getLength() {
        return length;
    }

}
