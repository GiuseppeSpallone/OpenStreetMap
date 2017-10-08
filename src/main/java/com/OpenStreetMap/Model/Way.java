package com.OpenStreetMap.Model;

import java.util.ArrayList;

public class Way {

    private Long id;
    private ArrayList<Node> nd;
    private ArrayList<Node> nd_approximate;

    private boolean bicycle;
    private boolean foot;
    private boolean electrified;
    private String highway;
    private int lanes;
    private int maxspeed;
    private String name;
    private boolean oneway;
    private boolean bridge;
    private int layer;
    private boolean tunnel;
    private String railway;

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

    public boolean isBicycle() {
        return bicycle;
    }

    public void setBicycle(boolean bicycle) {
        this.bicycle = bicycle;
    }

    public boolean isFoot() {
        return foot;
    }

    public void setFoot(boolean foot) {
        this.foot = foot;
    }

    public boolean isElectrified() {
        return electrified;
    }

    public void setElectrified(boolean electrified) {
        this.electrified = electrified;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public int getLanes() {
        return lanes;
    }

    public void setLanes(int lanes) {
        this.lanes = lanes;
    }

    public int getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(int maxspeed) {
        this.maxspeed = maxspeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean isBridge() {
        return bridge;
    }

    public void setBridge(boolean bridge) {
        this.bridge = bridge;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }

    public String getRailway() {
        return railway;
    }

    public void setRailway(String railway) {
        this.railway = railway;
    }
}
