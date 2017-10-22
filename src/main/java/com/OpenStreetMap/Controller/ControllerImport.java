package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ControllerImport {

    private HashMap<Long, Node> nodes = null;
    private HashMap<Long, Way> ways = null;
    private HashSet<Arc> arcs = null;
    private HashSet<Node> buildings = null;

    private float SGL = 5.0f;
    private float RIS = 100.0f;
    private float prop = 1.00f;
    private static final float maxD = 40.00f;

    private float minlat = 0;
    private float minlon = 0;
    private float maxlat = 0;
    private float maxlon = 0;

    public void create(File file, float sogliaCurva, float unitiSuzeMt, float risoluzioneMt, boolean import_building, boolean import_cycleway) {

        prop = 1 / unitiSuzeMt;
        SGL = sogliaCurva;
        RIS = risoluzioneMt;

        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setValidation(false);

        try {
            Document doc = saxBuilder.build(file);
            Element root = doc.getRootElement();

            System.out.println("CREAZIONE");

            // min e max latitudine e longitudine
            Element bounds = root.getChild("bounds");
            if (bounds != null) {
                minlat = Float.parseFloat(bounds.getAttribute("minlat").getValue());
                minlon = Float.parseFloat(bounds.getAttribute("minlon").getValue());
                maxlat = Float.parseFloat(bounds.getAttribute("maxlat").getValue());
                maxlon = Float.parseFloat(bounds.getAttribute("maxlon").getValue());
            } else {
                Element box = root.getChild("bound");
                if (box != null) {
                    String s = box.getAttribute("box").getValue();
                    String ss[] = s.split(",");
                    minlat = Float.parseFloat(ss[0]);
                    minlon = Float.parseFloat(ss[1]);
                    maxlat = Float.parseFloat(ss[2]);
                    maxlon = Float.parseFloat(ss[3]);
                }
            }

            float minlatT = minlat - 10.0f * (maxlat - minlat) / 100.0f;
            float minlonT = minlon - 10.0f * (maxlon - minlon) / 100.0f;
            float maxlatT = maxlat + 10.0f * (maxlat - minlat) / 100.0f;
            float maxlonT = maxlon + 10.0f * (maxlon - minlon) / 100.0f;

            System.out.println("     MIN LAT: " + minlat + "; MAX LAT: " + maxlat + "; MIN LON: " + minlon + "; MAX LON: " + maxlon);
            System.out.println("     MIN LAT_T: " + minlatT + "; MAX LAT_T: " + maxlatT + "; MIN LON_T: " + minlonT + "; MAX LON_T: " + maxlonT);

            // nodi
            List children_node = root.getChildren("node");
            if (children_node.size() > 0) {
                nodes = new HashMap<>();
                buildings = new HashSet<>();

                for (Iterator it = children_node.iterator(); it.hasNext(); ) {
                    Element nd = (Element) it.next();
                    Node node = new Node();

                    node.setId(Long.parseLong(nd.getAttribute("id").getValue()));
                    node.setLat(Float.parseFloat(nd.getAttribute("lat").getValue()));
                    node.setLon(Float.parseFloat(nd.getAttribute("lon").getValue()));

                    nodes.put(node.getId(), node);

                    // edifici
                    if (nd.getChildren().size() > 0) {
                        buildings.add(node);
                    }
                }
            }
            System.out.println("     CREATE --> NODES: " + nodes.size());
            System.out.println("     CREATE --> BUILDINGS: " + buildings.size());

            //ottengo le strade
            if (import_cycleway) {
                ways = createCycleway(root);
                System.out.println("     CREATE --> CYCLEWAY: " + ways.size());
            } else {
                ways = createWay(root);
                System.out.println("     CREATE --> WAYS: " + ways.size());
            }


            //rimozione nodi ed edifici
            removeNodes_Buildings(minlatT, maxlatT, minlonT, maxlonT);
            System.out.println("     REMOVE -->: NODES: " + nodes.size() + " BUILDINGS: " + buildings.size());

            //creo archi
            arcs = createArcs(ways);
            System.out.println("     CREATE --> ARCS: " + arcs.size());


            if (!import_building) {
                buildings.clear();
                System.out.println("     CLEAR --> BUILDINGS ");
            }

            applyDimension(nodes, buildings, minlatT, maxlatT, minlonT, maxlonT);
            System.out.println("APPLY DIMENSION");
            normalize(nodes, arcs);
            System.out.println("NORMALIZE");
            applyOneway(arcs);
            System.out.println("APPLY ONEWAY");
            removeRepetition(arcs);
            System.out.println("REMOVE REPETITION");

            /*Node rif = Visit.removeUnconnected(nodes, arcs);
            System.out.println("REMOVE UNCONNECTED");
            Visit.removeNotStrongConnected(nodes, arcs, rif);
            System.out.println("REMOVE NOT STRONG CONNECTED");
            removeMiters(buildings, arcs);
            System.out.println("REMOVE MITERS");*/

            /*Node startingNode2 = null;
            Long idNode = 2314744735L;
            Long idNode = 2314745275L;
            Long idNode = 1567596942L;
            if (nodes.containsKey(idNode)) {
                startingNode2 = nodes.get(idNode);
            } else {
                System.out.println("Nodo non presente");
            }*/

            /*Node startingNode = randomNode(nodes);
            Visit visite = new Visite();
            visite.visita(nodes, startingNode);*/

            /*Algorithms algorithms = new Algorithms();
//            Long s = 1567597028L;
//            Long d = 2314745275L;
//            Node sorgente = nodes.get(s);
//            Node destinazione = nodes.get(d);
            Node sorgente = randomNode(nodes);
            Node destinazione = randomNode(nodes);
            algorithms.dijkstra(sorgente, destinazione, nodes);

            setIndexNodes(nodes);*/

        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private HashMap<Long, Way> createCycleway(Element root) {

        HashSet<String> tipiStrade = new HashSet<>();
        tipiStrade.add("primary");
        tipiStrade.add("primary_link");
        tipiStrade.add("secondary");
        tipiStrade.add("tertiary");
        tipiStrade.add("unclassified");
        tipiStrade.add("residential");
        tipiStrade.add("service");
        tipiStrade.add("secondary_link");
        tipiStrade.add("tertiary_link");
        tipiStrade.add("living_street");
        tipiStrade.add("mini_roundabout");
        tipiStrade.add("pedestrian");
        tipiStrade.add("cycleway");

        List children_way = root.getChildren("way");

        if (children_way.size() > 0) {
            ways = new HashMap<>();

            HashSet<String> tipiNuovi = new HashSet<>();
            HashMap<String, Integer> tag = new HashMap<>();

            List children_way_nd, children_way_tag;

            for (Iterator it = children_way.iterator(); it.hasNext(); ) {
                Element we = (Element) it.next();

                boolean imp = false;
                boolean oneway = false;
                boolean building = false;
                boolean tunnel = false;
                boolean stopImp = false;
                boolean cycleway_lane = false;
                boolean cycleway_opposite_lane = false;

                children_way_tag = we.getChildren("tag");

                //esamino i tag delle strade
                if (children_way_tag.size() > 0) {
                    for (Iterator it1 = children_way_tag.iterator(); it1.hasNext(); ) {
                        Element nd = (Element) it1.next();

                        String k = nd.getAttribute("k").getValue();
                        String v = nd.getAttribute("v").getValue();

                        if (k != null) {
                            if (test(k, "tunnel")) {
                                tunnel = true;
                            }
                            if (test(k, "highway")) {
                                if (v != null) {
                                    if (tipiStrade.contains(v)) {
                                        imp = true;
                                    } else {
                                        tipiNuovi.add(v);
                                    }
                                }
                            }
                            if (test(k, "junction")) {
                                imp = true;
                            }
                            if (test(k, "oneway")) {
                                oneway = true;
                            }

                            if (test(k, "area", v, "yes")) {
                                stopImp = true;
                            }
                            if (test(k, "access", v, "private")) {
                                stopImp = true;
                            }
                            if (test(k, "building")) {
                                building = true;
                            }

                            if (test(k, "cycleway")) {
                                if (test(v, "no")) {
                                    stopImp = true;
                                } else {
                                    imp = true;
                                }
                            }
                            if (testStart(k, "cycleway:left") || testStart(k, "cycleway:right")) {
                                imp = true;
                                if (test(v, "opposite_lane")) {
                                    cycleway_opposite_lane = true;
                                } else if (test(v, "lane")) {
                                    cycleway_lane = true;
                                }
                            }

                            if (testStart(k, "bicycle")) {
                                if (test(v, "no")) {
                                    stopImp = true;
                                } else {
                                    imp = true;
                                }
                            }

                            if (testStart(k, "maxspeed:bicycle")) {
                                imp = true;
                            }

                            if (testStart(k, "oneway:bicycle")) {
                                imp = true;
                                if (test(v, "no")) {
                                    cycleway_opposite_lane = true;
                                    cycleway_lane = true;
                                } else {
                                    oneway = true;
                                }
                            }

                            if (testStart(k, "tracktype")) {
                                if (test(v, "grade1")) {
                                    imp = true;
                                }
                                if (test(v, "grade2")) {
                                    imp = true;
                                }
                                if (test(v, "grade3")) {
                                    imp = true;
                                }
                            }


                            Integer i = tag.get(k);
                            if (i != null) {
                                i = i.intValue() + 1;
                            } else {
                                i = 1;
                            }
                            tag.put(k, i);
                        }
                    }

                    if (cycleway_lane && cycleway_opposite_lane) {
                        oneway = false;
                    }
                }

                if (imp && !stopImp) {
                    Way way = new Way();

                    way.setId(Long.parseLong(we.getAttribute("id").getValue()));
                    way.setOneway(oneway);
                    way.setTunnel(tunnel);

                    children_way_nd = we.getChildren("nd");

                    ArrayList<Node> nodes_way = new ArrayList<>();

                    //esamino i nodi delle strade
                    if (children_way_nd.size() > 0) {
                        for (Iterator it2 = children_way_nd.iterator(); it2.hasNext(); ) {
                            Element nd2 = (Element) it2.next();

                            ArrayList<Way> ways_node = new ArrayList<>();

                            Long ref = Long.parseLong(nd2.getAttribute("ref").getValue());

                            if (nodes.containsKey(ref)) {
                                Node node = nodes.get(ref); //creo nodo
                                nodes_way.add(node); //aggiungo nodo in array
                                ways_node.add(way); //aggiungo strada in array
                                node.setNd_ways(ways_node); //set array di strade in nodo
                                ways.put(way.getId(), way); //aggiungo strada in hashmap strade
                            }
                        }
                        way.setNd(nodes_way); //set array di nodi in strada
                        if (oneway && cycleway_opposite_lane) {
                            Collections.reverse(way.getNd());
                        }

                    }
                } else {
                    if (building) {
                        children_way_nd = we.getChildren("nd");
                        for (Iterator it1 = children_way_nd.iterator(); it1.hasNext(); ) {
                            Element nd = (Element) it1.next();
                            long ref = Long.parseLong(nd.getAttribute("ref").getValue());
                            if (!nodes.containsKey(ref)) {
                                System.out.println("Ref Not Found! " + ref);
                            } else {
                                Node n = nodes.get(ref);
                                buildings.add(n);
                                break;
                            }

                        }
                    }
                }
            }

            /*for (String tipiNuovi1 : tipiNuovi) {
                System.out.println("-->" + tipiNuovi1);
            }
            for (Iterator<Map.Entry<String, Integer>> it = tag.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Integer> entry = it.next();
                System.out.println(entry.getKey() + " " + entry.getValue());
            }*/
        }
        return ways;
    }

    private HashMap<Long, Way> createWay(Element root) {

        HashSet<String> tipiStrade = new HashSet<>();
        tipiStrade.add("motorway");
        tipiStrade.add("trunk");
        tipiStrade.add("primary");
        tipiStrade.add("secondary");
        tipiStrade.add("tertiary");
        tipiStrade.add("unclassified");
        tipiStrade.add("residential");
        tipiStrade.add("service");
        tipiStrade.add("motorway_link");
        tipiStrade.add("trunk_link");
        tipiStrade.add("primary_link");
        tipiStrade.add("secondary_link");
        tipiStrade.add("tertiary_link");
        tipiStrade.add("living_street");
        tipiStrade.add("mini_roundabout");

        List children_way = root.getChildren("way");

        if (children_way.size() > 0) {
            ways = new HashMap<>();

            HashSet<String> tipiNuovi = new HashSet<>();
            HashMap<String, Integer> tag = new HashMap<>();

            List children_way_nd, children_way_tag;

            for (Iterator it = children_way.iterator(); it.hasNext(); ) {
                Element we = (Element) it.next();

                boolean imp = false;
                boolean oneway = false;
                boolean building = false;
                boolean tunnel = false;
                boolean impCycleway = false;
                boolean stopImp = false;
                boolean cycleway_lane = false;
                boolean cycleway_opposite_lane = false;

                children_way_tag = we.getChildren("tag");

                //esamino i tag delle strade
                if (children_way_tag.size() > 0) {
                    for (Iterator it1 = children_way_tag.iterator(); it1.hasNext(); ) {
                        Element nd = (Element) it1.next();

                        String k = nd.getAttribute("k").getValue();
                        String v = nd.getAttribute("v").getValue();

                        if (k != null) {
                            if (test(k, "tunnel")) {
                                tunnel = true;
                            }
                            if (test(k, "highway")) {
                                if (v != null) {
                                    if (tipiStrade.contains(v)) {
                                        imp = true;
                                    } else {
                                        tipiNuovi.add(v);
                                    }
                                }
                            }
                            if (test(k, "junction")) {
                                imp = true;
                            }
                            if (test(k, "oneway")) {
                                oneway = true;
                            }

                            if (test(k, "area", v, "yes")) {
                                stopImp = true;
                            }
                            if (test(k, "access", v, "private")) {
                                stopImp = true;
                            }
                            if (test(k, "building")) {
                                building = true;
                            }

                            Integer i = tag.get(k);
                            if (i != null) {
                                i = i.intValue() + 1;
                            } else {
                                i = 1;
                            }
                            tag.put(k, i);
                        }
                    }
                }

                if (imp && !stopImp) {
                    Way way = new Way();

                    way.setId(Long.parseLong(we.getAttribute("id").getValue()));
                    way.setOneway(oneway);
                    way.setTunnel(tunnel);

                    children_way_nd = we.getChildren("nd");

                    ArrayList<Node> nodes_way = new ArrayList<>();

                    //esamino i nodi delle strade
                    if (children_way_nd.size() > 0) {
                        for (Iterator it2 = children_way_nd.iterator(); it2.hasNext(); ) {
                            Element nd2 = (Element) it2.next();

                            ArrayList<Way> ways_node = new ArrayList<>();

                            Long ref = Long.parseLong(nd2.getAttribute("ref").getValue());

                            if (nodes.containsKey(ref)) {
                                Node node = nodes.get(ref);
                                way.nd.add(node);
                                node.nd_ways.add(way);
//                                Node node = nodes.get(ref); //creo nodo
//                                nodes_way.add(node); //aggiungo nodo in array
//                                way.setNd(nodes_way); //set array di nodi in strada
//                                ways_node.add(way); //aggiungo strada in array
//                                node.setNd_ways(ways_node); //set array di strade in nodo
                            }
                        }
                        ways.put(way.getId(), way); //aggiungo strada in hashmap strade
                    }
                } else {
                    if (building) {
                        children_way_nd = we.getChildren("nd");
                        for (Iterator it1 = children_way_nd.iterator(); it1.hasNext(); ) {
                            Element nd = (Element) it1.next();
                            long ref = Long.parseLong(nd.getAttribute("ref").getValue());
                            if (!nodes.containsKey(ref)) {
                                System.out.println("Ref Not Found! " + ref);
                            } else {
                                Node n = nodes.get(ref);
                                buildings.add(n);
                                break;
                            }
                        }
                    }
                }
            }

            /*for (String tipiNuovi1 : tipiNuovi) {
                System.out.println("-->" + tipiNuovi1);
            }
            for (Iterator<Map.Entry<String, Integer>> it = tag.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Integer> entry = it.next();
                System.out.println(entry.getKey() + " " + entry.getValue());
            }*/
        }
        return ways;
    }

    private boolean test(String a, String rif_a) {
        if (a == null || rif_a == null) {
            return false;
        }
        return a.equals(rif_a);
    }

    private boolean test(String a, String rif_a, String b, String rif_b) {
        return test(a, rif_a) && test(b, rif_b);
    }

    private boolean testStart(String a, String rif_a) {
        if (a == null || rif_a == null) {
            return false;
        }
        return a.startsWith(rif_a);
    }

    private HashSet<Arc> createArcs(HashMap<Long, Way> ways) {
        arcs = new HashSet<>();
        //ArrayList<Arc> arcs_node = null;

        for (Iterator<Way> it = ways.values().iterator(); it.hasNext(); ) {
            Way w = it.next();
            Node old = null;

            for (Iterator<Node> it1 = w.getNd().iterator(); it1.hasNext(); ) {
                Node n = it1.next();

                //arcs_node = new ArrayList<>();

                if (old != null) {
                    Arc a = new Arc(old, n);

                    a.setOneway(w.isOneway()); //set oneway dell'arco
                    a.setTunnel(w.isTunnel()); //set tunnel dell'arco

                    if (a.isTunnel()) {
                        old.setTunnel(true);
                        n.setTunnel(true);
                    }
                    arcs.add(a);
                    n.nd_arcs.add(a);
                    old.nd_arcs.add(a);

                    /*arcs.add(a); //aggiungo arco in hashset archi
                    arcs_node.add(a); //aggiungo arco in array
                    n.setNd_arcs(arcs_node); //set array di archi in nodo n
                    old.setNd_arcs(arcs_node); //set array di archi in nodo old*/

                }
                old = n;
            }
        }
        return arcs;
    }

    private void setIndexNodes(HashMap<Long, Node> nodes) {
        int i = 0;
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            n.setIndex(i++);
        }
    }

    private void applyDimension(HashMap<Long, Node> nodes, HashSet<Node> buildings, float minlatT, float maxlatT, float minlonT, float maxlonT) {

        double dmax = distance(minlatT, maxlatT, minlonT, minlonT, 0, 0);

        int i = 0;
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            n.setIndex(i++);
            n.setX((int) (prop * (distance(minlatT, minlatT, minlonT, n.getLon(), 0, 0))));
            n.setY((int) (prop * (dmax - distance(minlatT, n.getLat(), minlonT, minlonT, 0, 0))));
        }

        for (Iterator<Node> it = buildings.iterator(); it.hasNext(); ) {
            Node n = it.next();
            if (!nodes.containsKey(n.getId())) {
                n.setIndex(i++);
                n.setIndex((int) (prop * (distance(minlatT, minlatT, minlonT, n.getLon(), 0, 0))));
                n.setY((int) (prop * (dmax - distance(minlatT, n.getLat(), minlonT, minlonT, 0, 0))));
            }
        }
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private void removeNodes_Buildings(float minlatT, float maxlatT, float minlonT, float maxlonT) {
        ArrayList<Node> del = new ArrayList<>();

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            if (n.getNd_ways().size() <= 0) {
                del.add(n);
            } else {
                if (n.getLat() < minlatT) {
                    del.add(n);
                } else if (n.getLat() > maxlatT) {
                    del.add(n);
                } else if (n.getLon() < minlonT) {
                    del.add(n);
                } else if (n.getLon() > maxlonT) {
                    del.add(n);
                }
            }
        }

        for (Iterator<Node> it = del.iterator(); it.hasNext(); ) {
            Node n = it.next();
            nodes.remove(n.getId());
            for (Iterator<Way> it1 = n.getNd_ways().iterator(); it1.hasNext(); ) {
                Way way = it1.next();
                way.getNd().remove(n);
            }
        }
        del.clear();

        for (Iterator<Node> it = buildings.iterator(); it.hasNext(); ) {
            Node n = it.next();
            if (n.getLat() < minlatT) {
                del.add(n);
            } else if (n.getLat() > maxlatT) {
                del.add(n);
            } else if (n.getLon() < minlonT) {
                del.add(n);
            } else if (n.getLon() > maxlonT) {
                del.add(n);
            }
        }

        for (Iterator<Node> it = del.iterator(); it.hasNext(); ) {
            Node n = it.next();
            buildings.remove(n);
        }
    }

    private void normalize(HashMap<Long, Node> nodes, HashSet<Arc> arcs) {
        ArrayList<Node> del = new ArrayList<>(nodes.size());
        ArrayList<Node> nd = new ArrayList<>(nodes.values());

        Collections.shuffle(nd);

        for (Iterator<Node> it = nd.iterator(); it.hasNext(); ) {
            Node n = it.next();

            if (n.getNd_arcs().size() == 2) {
                if (n.getNd_arcs().get(0).isOneway() == n.getNd_arcs().get(1).isOneway()) {
                    Node nA, nB;
                    Arc a = n.getNd_arcs().get(0);
                    double l = a.getLength();
                    boolean inv = false;
                    if (a.getFrom() == n) {
                        nA = a.getTo();
                        inv = true;
                    } else {
                        nA = a.getFrom();
                    }
                    a = n.getNd_arcs().get(1);
                    l += a.getLength();
                    if (a.getFrom() == n) {
                        nB = a.getTo();
                    } else {
                        nB = a.getFrom();
                    }
                    if (nA != n && n != nB && nA != nB && testDel(nA, n, nB, l)) {
                        del.add(n);

                        nA.getNd_arcs().remove(n.getNd_arcs().get(0));
                        nB.getNd_arcs().remove(n.getNd_arcs().get(1));
                        arcs.remove(n.getNd_arcs().get(0));
                        arcs.remove(n.getNd_arcs().get(1));
                        Arc ar = null;
                        if (inv) {
                            ar = new Arc(nB, nA, l);
                        } else {
                            ar = new Arc(nA, nB, l);
                        }
                        ar.setOneway(n.getNd_arcs().get(0).isOneway());
                        arcs.add(ar);
                        nA.getNd_arcs().add(ar);
                        nB.getNd_arcs().add(ar);
                    }
                }
            }
        }

        for (Iterator<Node> it = del.iterator(); it.hasNext(); ) {
            Node n = it.next();
            nodes.remove(n.getId());
        }
        int i = 0;
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            n.setIndex(i++);
        }
    }

    private boolean testDel(Node nA, Node n, Node nB, double l) {

        float d1 = nA.distanza(n);
        float d2 = n.distanza(nB);

        if (d1 > RIS || d2 > RIS) {
            return false;
        }

        double dAB = nA.distanzaLatLog(nB);
        if (l > dAB + SGL) {
            return false;
        }
        return true;
    }

    private void applyOneway(HashSet<Arc> arc) {
        ArrayList<Arc> arcAdd = new ArrayList<>();

        for (Iterator<Arc> it = arc.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            if (!a.isOneway()) {
                arcAdd.add(a);
            }
        }
        for (Iterator<Arc> it = arcAdd.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            a.setOneway(true);
            Arc b = new Arc(a.getTo(), a.getFrom(), a.getLength());
            b.setOneway(true);
            a.getTo().getNd_arcs().add(b);
            a.getFrom().getNd_arcs().add(b);
            arc.add(b);
        }
    }

    private void removeRepetition(HashSet<Arc> arc) {
        HashSet<String> arcM = new HashSet<>(arc.size());
        ArrayList<Arc> del = new ArrayList<>(arc.size());

        for (Iterator<Arc> it = arc.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            String s = a.getFrom() + "-" + a.getTo();
            if (arcM.contains(s)) {
                del.add(a);
            } else {
                arcM.add(s);
            }
        }

        for (Iterator<Arc> it = del.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            arc.remove(a);
            a.getFrom().getNd_arcs().remove(a);
            a.getTo().getNd_arcs().remove(a);
        }
    }

    private void removeMiters(HashSet<Node> buildings, HashSet<Arc> arc) {

        for (Iterator<Node> it = buildings.iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setFlag1(false);
        }
        for (Iterator<Arc> it = arc.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            for (Iterator<Node> it2 = buildings.iterator(); it2.hasNext(); ) {
                Node node = it2.next();
                if (!node.isFlag1()) {
                    if (node.distToEdge(a) < maxD) {
                        node.setFlag1(true);
                    }
                }
            }
        }
        ArrayList<Node> del = new ArrayList<>();
        for (Iterator<Node> it = buildings.iterator(); it.hasNext(); ) {
            Node node = it.next();
            if (!node.isFlag1()) {
                del.add(node);
            }
        }
    }

    public HashMap<Long, Node> getNodes() {
        return nodes;
    }

    public HashSet<Arc> getArcs() {
        return arcs;
    }

    public HashMap<Long, Way> getWays() {
        return ways;
    }

    public HashSet<Node> getBuildings() {
        return buildings;
    }

    /**
     * Print
     **/

    public void printALL() {
        System.out.println("   Nodi num: " + nodes.size());
        System.out.println("   Archi num: " + arcs.size());
        System.out.println("   Strade num: " + ways.size());
    }

    public void printNodes(HashMap<Long, Node> nodes) {
        System.out.println("Nodes: " + nodes.size());

        nodes.forEach((key, value) -> {
            System.out.println(
                    " index: " + value.getIndex() +
                            " id: " + value.getId()
                            + " lat: " + value.getLat()
                            + " lon: " + value.getLon());
        });
    }

    public void printWays(HashMap<Long, Way> ways) {
        System.out.println("Ways: " + ways.size());

        ways.forEach((key, value) -> {
            System.out.println("id: " + value.getId());

            ArrayList<Node> nodes = value.getNd();
            if (nodes != null && nodes.size() > 0) {
                System.out.println("nodes: " + nodes.size());
                nodes.forEach((nd) -> {
                    System.out.println("   nd: " + nd.getId() + "   lat: " + nd.getLat() + "   lon: " + nd.getLon());
                });
            }
            System.out.println("   oneway: " + value.isOneway() + "   tunnel: " + value.isTunnel());
        });
    }

    public void printArcs(HashSet<Arc> arcs) {
        System.out.println("Arcs: " + arcs.size());
        int i = 0;
        for (Iterator<Arc> it = arcs.iterator(); it.hasNext(); ) {
            Arc a = it.next();
            System.out.println(i + " from: " + a.getFrom().getId()
                    + " to: " + a.getTo().getId());
            i++;
        }

        /*arcs.forEach((value) -> {
            System.out.println("from: " + value.getFrom().getId()
                    + " to: " + value.getTo().getId());
        });*/
    }
}
