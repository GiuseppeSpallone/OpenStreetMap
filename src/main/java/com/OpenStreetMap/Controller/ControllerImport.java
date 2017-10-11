package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Way;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ControllerImport {

    public HashMap<Long, Node> nodes = null;
    public HashMap<Long, Way> ways = null;
    public HashSet<Arc> arcs = null;

    float minlat = 0;
    float minlon = 0;
    float maxlat = 0;
    float maxlon = 0;

    public void create(File file) {

        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setValidation(false);

        try {
            Document doc = saxBuilder.build(file);
            Element root = doc.getRootElement();

            System.out.println("CREAZIONE");

            //ottengo min e max di lat e lon
            Element bounds = root.getChild("bounds");
            if (bounds != null) {
                minlat = Float.parseFloat(bounds.getAttribute("minlat").getValue());
                minlon = Float.parseFloat(bounds.getAttribute("minlon").getValue());
                maxlat = Float.parseFloat(bounds.getAttribute("maxlat").getValue());
                maxlon = Float.parseFloat(bounds.getAttribute("maxlon").getValue());
            }

            float minlatT = minlat - 10.0f * (maxlat - minlat) / 100.0f;
            float minlonT = minlon - 10.0f * (maxlon - minlon) / 100.0f;
            float maxlatT = maxlat + 10.0f * (maxlat - minlat) / 100.0f;
            float maxlonT = maxlon + 10.0f * (maxlon - minlon) / 100.0f;

            //ottengo i nodi
            List children_node = root.getChildren("node");
            if (children_node.size() > 0) {
                nodes = new HashMap<>();

                for (Iterator it = children_node.iterator(); it.hasNext(); ) {
                    Element nd = (Element) it.next();
                    Node node = new Node();

                    node.setId(Long.parseLong(nd.getAttribute("id").getValue()));
                    node.setLat(Float.parseFloat(nd.getAttribute("lat").getValue()));
                    node.setLon(Float.parseFloat(nd.getAttribute("lon").getValue()));

                    nodes.put(node.getId(), node);
                }
            }
            System.out.println("   Creati nodi num: " + nodes.size());

            //set indice nodi
            int i = 0;
            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
                Node n = it.next();
                n.setIndex(i++);
            }

            //ottengo le strade
            List children_way = root.getChildren("way");
            if (children_way.size() > 0) {
                ways = new HashMap<>();
                List children_way_nd;

                for (Iterator it = children_way.iterator(); it.hasNext(); ) {
                    Element nd = (Element) it.next();
                    Way way = new Way();

                    way.setId(Long.parseLong(nd.getAttribute("id").getValue()));

                    children_way_nd = nd.getChildren("nd");

                    ArrayList<Node> nodes_way = new ArrayList<>();

                    //esamino i nodi delle strade
                    if (children_way_nd.size() > 0) {
                        for (Iterator it2 = children_way_nd.iterator(); it2.hasNext(); ) {
                            Element nd2 = (Element) it2.next();

                            Node node = new Node();

                            Long ref = Long.parseLong(nd2.getAttribute("ref").getValue());

                            //aggiungo informazioni ai nodi delle strade
                            nodes.forEach((key, value) -> {
                                if (Objects.equals(value.getId(), ref)) {

                                    node.setIndex(value.getIndex());
                                    node.setId(value.getId());
                                    node.setLat(value.getLat());
                                    node.setLon(value.getLon());

                                    nodes_way.add(node);
                                }
                            });
                            way.setNd(nodes_way);
                        }
                    }
                    ways.put(way.getId(), way);
                }
            }
            System.out.println("   Create strade num: " + ways.size());

            //creo archi
            arcs = new HashSet<>();
            for (Iterator<Way> it = ways.values().iterator(); it.hasNext(); ) {
                Way w = it.next();
                Node old = null;

                for (Iterator<Node> it1 = w.getNd().iterator(); it1.hasNext(); ) {
                    Node n = it1.next();

                    if (old != null) {
                        Arc a = new Arc(old, n);

                        arcs.add(a);
                    }
                    old = n;
                }
            }
            System.out.println("   Creati archi num: " + arcs.size());

            applyDimension(nodes, minlatT, maxlatT, minlonT, maxlonT);

            //exportALL(nodes, arcs);


        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private static void applyDimension(HashMap<Long, Node> nodes, float minlatT, float maxlatT, float minlonT, float maxlonT) {
        int i = 0;
        double dmax = distance(minlatT, maxlatT, minlonT, maxlonT, 0, 0);

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            n.setIndex(i++);
            n.setX((int) (1.00f * (distance(minlatT, minlatT, minlonT, n.getLon(), 0, 0))));
            n.setY((int) (1.00f * (dmax - distance(minlatT, n.getLat(), minlonT, minlonT, 0, 0))));
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

    /**
     * Export
     **/
    public void exportALL(File file, HashMap<Long, Node> nodes, HashSet<Arc> arcs) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            System.out.println("EXPORT: " + file.getName());
            System.out.println("   Creato file: " + file.getName());
            System.out.println("     nodi: " + nodes.size() + " archi: " + arcs.size() + " buildings: " + 0);
            out.println(nodes.size() + " " + arcs.size() + " " + 0);


            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
                Node n = it.next();
                out.println(n.getIndex() + " " + n.getX() + " " + n.getY() + " " + n.getZ() + " " + n.getLat() + " " + n.getLon());
            }


            for (Iterator<Arc> it = arcs.iterator(); it.hasNext(); ) {
                Arc a = it.next();
                double ll = Math.round(a.getLength() * 100.0) / 100.0;
                out.println(a.getFrom().getIndex() + " " + a.getTo().getIndex() + " " + ll);
            }

            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                outFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Print
     **/
    public void printNodes(HashMap<Long, Node> nodes) {
        System.out.println("Nodes: " + nodes.size());

        nodes.forEach((key, value) -> {
            System.out.println(
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
                nodes.forEach((nd) -> {
                    System.out.println("   nd: " + nd.getId() + "   lat: " + nd.getLat() + "   lon: " + nd.getLon());
                });
            }

            ArrayList<Node> nodes_approximate = value.getNd_approximate();
            if (nodes_approximate != null && nodes_approximate.size() > 0) {
                nodes_approximate.forEach((nd_approximate) -> {
                    System.out.println("   nd_approximate: " + nd_approximate.getId() + "   lat: " + nd_approximate.getLat() + "   lon: " + nd_approximate.getLon());
                });
            }
        });
    }

    public void printArcs(HashSet<Arc> arcs) {
        System.out.println("Arcs: " + arcs.size());

        arcs.forEach((value) -> {
            System.out.println("from: " + value.getFrom().getIndex()
                    + " to: " + value.getTo().getIndex());
        });
    }
}
