package com.OpenStreetMap.Model;

public class Arc {

    private Node from;
    private Node to;
    private double length;
    private int index;

    private boolean oneway=false;
    private boolean tunnel=false;


    public Arc(Node from, Node to) {
        this.from = from;
        this.to = to;
        length = from.distanzaLatLog(to);
    }

    public Arc(Node from, Node to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
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

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
