package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Way;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ControllerImport {

    /**
     * Open file OpenStreetMap
     **/
    public File openFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Scegli file streetMap");

        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String pathFile = jFileChooser.getSelectedFile().getPath();

            File file = new File(pathFile);

            System.out.println("Aperto file: " + pathFile);

            return file;
        } else {
            System.out.println("Nessun file selezionato");
            return null;
        }
    }

    /**
     * Create nodes hashmap
     **/
    public HashMap createNodes(File file) {
        HashMap<Long, Node> nodes = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        float minlat = 0;
        float minlon = 0;
        float maxlat = 0;
        float maxlon = 0;

        try {
            Document doc = saxBuilder.build(file);
            Element root = doc.getRootElement();
            List children_node = root.getChildren("node");

            Element bounds = root.getChild("bounds");

            minlat = Float.parseFloat(bounds.getAttribute("minlat").getValue());
            minlon = Float.parseFloat(bounds.getAttribute("minlon").getValue());
            maxlat = Float.parseFloat(bounds.getAttribute("maxlat").getValue());
            maxlon = Float.parseFloat(bounds.getAttribute("maxlon").getValue());

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
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        applyDimension(nodes, minlat, maxlat, minlon, maxlon);

        return nodes;
    }

    private void applyDimension(HashMap<Long, Node> nodes, float minlat, float maxlat, float minlon, float maxlon) {
        int i = 0;
        double dmax = distance(minlat, maxlat, minlon, minlon, 0, 0);
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node n = it.next();
            n.setIndex(i++);
            n.setX((int) (1.00f * (distance(minlat, minlat, minlon, n.getLon(), 0, 0))));
            n.setY((int) (1.00f * (dmax - distance(minlat, n.getLat(), minlon, minlon, 0, 0))));
        }
    }

    private static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

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
     * Create ways hashmap
     **/
    public HashMap createWays(File file) {
        HashMap<Long, Way> ways = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document doc = saxBuilder.build(file);
            Element root = doc.getRootElement();
            List children_way = root.getChildren("way");

            //esamino le strade
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

                            node.setId(Long.parseLong(nd2.getAttribute("ref").getValue()));
                            //settare anche latitudine e longitudine
                            nodes_way.add(node);

                            way.setNd(nodes_way);
                        }
                    }
                    ways.put(way.getId(), way);
                }
            }
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return ways;
    }

    /**
     * Create arcs hashmap
     **/
    public HashSet createArcs(HashMap<Long, Way> ways) {
        HashSet<Arc> arcs = new HashSet<>();

        ways.values().forEach((way) -> {
            Node newNode = null;

            for (Node node : way.getNd()) {
                if (newNode != null) {
                    Arc arc = new Arc(node, newNode);

                    arcs.add(arc);
                }
                newNode = node;
            }
        });
        return arcs;
    }

    /**
     * Export
     **/
    public void exportALL(HashMap<Long, Node> nodes, HashSet<Arc> arcs) {
        String pathFile = "C:\\Users\\Giuseppe\\Desktop\\prova.osm.grf";
        File file = new File(pathFile);
        FileWriter outFile = null;

        try {

            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);
            System.out.println(nodes.size() + " " + arcs.size() + " " + 0);
            out.println(nodes.size() + " " + arcs.size() + " " + 0);

            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
                Node n = it.next();
                out.println(n.getIndex() + " " + n.getX() + " " + n.getY() + " " + n.getZ() + " " + n.getLat() + " " + n.getLon());
            }

            for (Iterator<Arc> it = arcs.iterator(); it.hasNext(); ) {
                Arc a = it.next();
                out.println(a.getFrom().getIndex() + " " + a.getTo().getIndex() + " " + 0);
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
     * Identify keys of tags
     **/
    public ArrayList identifyTags(File file, String nameChildren) {
        ArrayList<String> key_tags = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document doc = saxBuilder.build(file);
            Element root = doc.getRootElement();
            List children = root.getChildren(nameChildren);
            List children2;

            key_tags = new ArrayList<>();

            for (Iterator it = children.iterator(); it.hasNext(); ) {
                Element nd = (Element) it.next();

                children2 = nd.getChildren("tag");

                if (children2.size() > 0) {

                    for (Iterator it3 = children2.iterator(); it3.hasNext(); ) {
                        Element nd3 = (Element) it3.next();

                        String key = nd3.getAttributeValue("k");

                        if (!key_tags.contains(key))
                            key_tags.add(key);
                    }
                }
            }
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return key_tags;
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

    public void printBuildings(HashSet<Node> buildings) {
        System.out.println("Buildings: " + buildings.size());

        buildings.forEach((value) -> {
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
        });
    }

    public void printArcs(HashSet<Arc> arcs) {
        System.out.println("Arcs: " + arcs.size());

        arcs.forEach((value) -> {
            System.out.println("from: " + value.getFrom().getId()
                    + " to: " + value.getTo().getId());
        });
    }

    public void printTags(ArrayList<String> key_tags) {
        System.out.println("Tags: " + key_tags.size());

        key_tags.forEach((key_tag) -> {
            System.out.println(
                    " key: " + key_tag);
        });
    }
}
