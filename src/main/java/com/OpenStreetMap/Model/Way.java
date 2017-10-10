package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Way {

    private Long id;
    private ArrayList<Node> nd;
    private ArrayList<Node> nd_approximate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<Node> getNd() {
        return nd;
    }

    public void setNd(ArrayList<Node> nd) {
        this.nd = nd;
    }

    public ArrayList<Node> getNd_approximate() {
        return nd_approximate;
    }

    public void setNd_approximate(ArrayList<Node> nd_approximate) {
        this.nd_approximate = nd_approximate;
    }
}
