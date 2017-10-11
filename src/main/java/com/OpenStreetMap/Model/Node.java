package com.OpenStreetMap.Model;

import com.OpenStreetMap.Controller.ControllerImport;

public class Node {
    
    private Long id;
    private float lat;
    private float lon;

    private int index;
    private int x;
    private int y;
    private int z;

    double distanzaLatLog(Node n) {
        return ControllerImport.distance(lat, n.getLat(), lon, n.getLon(), 0, 0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
