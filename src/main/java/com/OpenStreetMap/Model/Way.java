package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Way {

    private Long id;
    private boolean oneway = false;
    private boolean tunnel=false;

    public ArrayList<Node> nd = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
