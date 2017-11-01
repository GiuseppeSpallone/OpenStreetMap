package com.OpenStreetMap.Model;

import java.util.Iterator;

public class Arc {

    private Node from;
    private Node to;
    private double length;
    private int index;

    private boolean oneway = false;
    private boolean tunnel = false;

    //Dijkstra
    private int mark;

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

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

    public static Arc arcByFromTo(Node from, Node to) {
        for (Iterator<Arc> it1 = from.nd_arcs.iterator(); it1.hasNext(); ) {
            Arc a1 = it1.next();

            for (Iterator<Arc> it2 = to.nd_arcs.iterator(); it2.hasNext(); ) {
                Arc a2 = it2.next();

                if (a1 == a2) {
                    return a1;
                }
            }
        }
        return null;
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
