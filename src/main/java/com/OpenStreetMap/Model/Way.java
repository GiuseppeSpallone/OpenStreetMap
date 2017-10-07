package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Way {

    private Long id;
    private ArrayList<Node> nd;
    private ArrayList<String[]> tag;

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

    public ArrayList<String[]> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String[]> tag) {
        this.tag = tag;
    }
    
    
}
