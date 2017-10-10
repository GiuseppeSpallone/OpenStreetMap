package com.OpenStreetMap.Model;

public class Arc {

    private Node from;
    private Node to;
    private int length;

    public Arc(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom() {
        return from;
    }

    public int getLength() {
        return length;
    }
}
