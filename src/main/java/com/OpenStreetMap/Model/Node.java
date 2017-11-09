package com.OpenStreetMap.Model;

import com.OpenStreetMap.Controller.ImportMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Node {

    private Long id;
    private float lat;
    private float lon;
    private int index;
    private int x;
    private int y;
    private int mark = -1;
    private boolean flag1;
    private int comp;
    private boolean tunnel = false;
    public ArrayList<Way> nd_ways = new ArrayList<>();
    public ArrayList<Arc> nd_arcs = new ArrayList<>();

    //Dijkstra
    private double distanza;
    private Node predecessore;
    //private int mark;

    //Tratta
    private int num_studenti;
    private Route route; //rotta prefissata
    private ArrayList<Percorso> percorsi_dijkstra; //percorsi dijkstra verso tutti i nodi della tratta
    private ArrayList<Percorso> percorsi_euclide; //percorsi euclidei verso tutti i nodi della tratta
    private Percorso idealPercorso; //percorso verso la fermata ideale
    private Node idealStop; //fermata ideale
    private Percorso realPercorso; //percorso verso la fermata effettiva
    private Node realStop; //fermata effettiva

    public double distanzaLatLog(Node n) {
        return ImportMap.distance(lat, n.getLat(), lon, n.getLon(), 0, 0);
    }

    public float distanza(Node n) {
        float d = (x - n.x) * (x - n.x) + (y - n.y) * (y - n.y);
        return (float) Math.sqrt(d);
    }

    public float distToEdge(Arc e) {
        return (float) Math.sqrt(distToSegmentSquared(x, y, e.getFrom().getX(), e.getFrom().getY(), e.getTo().getX(), e.getTo().getY()));
    }

    private float distToSegmentSquared(float px, float py, float vx, float vy, float wx, float wy) {
        float l2 = dist2(vx, vy, wx, wy);
        if (l2 == 0) {
            return dist2(px, py, vx, vy);
        }
        float t = ((px - vx) * (wx - vx) + (py - vy) * (wy - vy)) / l2;
        if (t < 0) {
            return dist2(px, py, vx, vy);
        }
        if (t > 1) {
            return dist2(px, py, wx, wy);
        }
        return dist2(px, py, vx + t * (wx - vx), vy + t * (wy - vy));
    }

    private float dist2(float vx, float vy, float wx, float wy) {
        return sqr(vx - wx) + sqr(vy - wy);
    }

    private float sqr(float x) {
        return x * x;
    }

    public static Node randomNode(HashMap<Long, Node> nodes) {
        int random = 0 + (int) (Math.random() * nodes.size());

        Node node = null;

        int i = 0;
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
            Node nd = it.next();

            if (i == random) {
                node = nd;
            }
            i++;
        }
        return node;
    }

    public static Node nodeByLatLon(HashMap<Long, Node> nodes, float lat, float lon) {
        if (lat != 0 && lon != 0) {
            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
                Node node = it.next();
                if (node.getLat() == lat && node.getLon() == lon) {
                    return node;
                }
            }
        }
        System.out.println("Latitudine o longitudine non inseriti");
        return null;
    }

    public static Node nodeVicinoByLatLon(HashMap<Long, Node> nodes, float lat, float lon) {
        Node node = null;
        float dist = Float.MAX_VALUE;

        if (lat != 0 && lon != 0) {
            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
                Node nd = it.next();

                float latitudine = nd.getLat();
                float longitudine = nd.getLon();

                float d = (lat - latitudine) * (lat - latitudine) + (lon - longitudine) * (lon - longitudine);
                if (d < dist) {
                    dist = d;
                    node = nd;
                }
            }
        }
        System.out.println("Latitudine o longitudine non inseriti");
        return node;
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

    public boolean isFlag1() {
        return flag1;
    }

    public void setFlag1(boolean flag1) {
        this.flag1 = flag1;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public double getDistanza() {
        return distanza;
    }

    public void setDistanza(double distanza) {
        this.distanza = distanza;
    }

    public Node getPredecessore() {
        return predecessore;
    }

    public void setPredecessore(Node predecessore) {
        this.predecessore = predecessore;
    }

    public int getComp() {
        return comp;
    }

    public void setComp(int comp) {
        this.comp = comp;
    }

    public int getNum_studenti() {
        return num_studenti;
    }

    public void setNum_studenti(int num_studenti) {
        this.num_studenti = num_studenti;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Node getIdealStop() {
        return idealStop;
    }

    public void setIdealStop(Node idealStop) {
        this.idealStop = idealStop;
    }

    public Percorso getIdealPercorso() {
        return idealPercorso;
    }

    public void setIdealPercorso(Percorso idealPercorso) {
        this.idealPercorso = idealPercorso;
    }

    public Node getRealStop() {
        return realStop;
    }

    public void setRealStop(Node realStop) {
        this.realStop = realStop;
    }

    public ArrayList<Percorso> getPercorsi_dijkstra() {
        return percorsi_dijkstra;
    }

    public void setPercorsi_dijkstra(ArrayList<Percorso> percorsi_dijkstra) {
        this.percorsi_dijkstra = percorsi_dijkstra;
    }

    public ArrayList<Percorso> getPercorsi_euclide() {
        return percorsi_euclide;
    }

    public void setPercorsi_euclide(ArrayList<Percorso> percorsi_euclide) {
        this.percorsi_euclide = percorsi_euclide;
    }

    public Percorso getRealPercorso() {
        return realPercorso;
    }

    public void setRealPercorso(Percorso realPercorso) {
        this.realPercorso = realPercorso;
    }

}
