package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Way {

    private Long id;
    private ArrayList<Node> nd;

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
}
