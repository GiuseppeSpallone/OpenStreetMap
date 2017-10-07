package com.OpenStreetMap.Model;

public class Arc {

    private Node to;
    private Node from;
    private double length;

    public Arc(Node to, Node from) {
        this.to = to;
        this.from = from;
        length = distanceNodes(to, from);
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    private double distanceNodes(Node to, Node from) {
        double earthRadius = 6371000;

        double distanceLat = Math.toRadians(from.getLat() - to.getLat());
        double distanceLon = Math.toRadians(from.getLon() - to.getLon());

        double a = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2)
                + Math.cos(Math.toRadians(to.getLat())) * Math.cos(Math.toRadians(from.getLat()))
                * Math.sin(distanceLon / 2) * Math.sin(distanceLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}
