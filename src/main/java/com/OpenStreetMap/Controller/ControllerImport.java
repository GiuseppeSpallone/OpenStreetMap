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
import java.io.IOException;
import java.util.*;

public class ControllerImport {
    /** Open file OpenStreetMap **/
    public static String openFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Scegli file streetMap");

        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();

            String pathFile = file.getPath();
            String nameFile = file.getName();

            System.out.println("Aperto file: " + nameFile);

            return pathFile;
        } else {
            System.out.println("Nessun file selezionato");
            return null;
        }
    }

    /** Create ways with nodes hashmap **/
    public static HashMap createOnlyWays(String pathFile) {
        File file = new File(pathFile);
        HashMap<Long, Node> nodes = null;
        HashMap<Long, Way> ways = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document doc = saxBuilder.build(file); //ottenere il documento
            Element root = doc.getRootElement(); //ottenere la radice
            List children_node = root.getChildren("node"); //ottenere i figli
            List children_way = root.getChildren("way");

            //Esamino i nodi
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

            //esamino le strade
            if (children_way.size() > 0) {
                ways = new HashMap<>();

                List children_way_nd;
                List children_way_tag;

                for (Iterator it = children_way.iterator(); it.hasNext(); ) {
                    Element nd = (Element) it.next();
                    Way way = new Way();

                    way.setId(Long.parseLong(nd.getAttribute("id").getValue()));

                    children_way_nd = nd.getChildren("nd");
                    children_way_tag = nd.getChildren("tag");

                    ArrayList<Node> nodes_way = new ArrayList<>();
                    ArrayList<String[]> tags = new ArrayList<>();

                    //esamino i nodi delle strade
                    if (children_way_nd.size() > 0) {
                        for (Iterator it2 = children_way_nd.iterator(); it2.hasNext(); ) {
                            Element nd2 = (Element) it2.next();

                            Node node = new Node();

                            Long ref = Long.parseLong(nd2.getAttribute("ref").getValue());

                            //nodi
                            nodes.forEach((key, value) -> {
                                if (Objects.equals(value.getId(), ref)) {
                                    float lat = value.getLat();
                                    float lon = value.getLon();

                                    node.setId(ref);
                                    node.setLat(lat);
                                    node.setLon(lon);

                                    nodes_way.add(node);

                                    //nodes.remove(ref);
                                }
                            });
                            way.setNd(nodes_way);
                        }
                    }

                    //esamino i tag delle strade
                    if (children_way_tag.size() > 0) {
                        for (Iterator it3 = children_way_tag.iterator(); it3.hasNext(); ) {
                            Element nd3 = (Element) it3.next();

                            String tag[] = new String[2];

                            tag[0] = nd3.getAttributeValue("k");
                            tag[1] = nd3.getAttributeValue("v");

                            tags.add(tag);
                            way.setTag(tags);
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

    /**Create only nodes hashmap **/
    public HashMap createOnlyNodes(String pathFile) {
        File file = new File(pathFile);
        HashMap<Long, Node> nodes = null;
        HashSet<Node> buildings = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document doc = saxBuilder.build(file); //ottenere il documento
            Element root = doc.getRootElement(); //ottenere la radice
            List children = root.getChildren("node"); //ottenere i figli
            List children2;

            nodes = new HashMap<>();
            buildings = new HashSet<>();

            for (Iterator it = children.iterator(); it.hasNext();) {
                Element nd = (Element) it.next();
                Node node = new Node();

                node.setId(Long.parseLong(nd.getAttribute("id").getValue()));
                node.setLat(Float.parseFloat(nd.getAttribute("lat").getValue()));
                node.setLon(Float.parseFloat(nd.getAttribute("lon").getValue()));

                nodes.put(node.getId(), node);

                if (nd.getChildren().size() > 0) {
                    buildings.add(node);
                }
            }
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        //printBuildings(buildings);
        return nodes;
    }

    /** Create only ways hashmap **/
    public HashMap createWays(String pathFile) {
        File file = new File(pathFile);
        HashMap<Long, Way> ways = null;
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document doc = saxBuilder.build(file); //ottenere il documento
            Element root = doc.getRootElement(); //ottenere la radice
            List children = root.getChildren("way"); //ottenere i figli
            List children2, children3;

            ways = new HashMap<>();

            for (Iterator it = children.iterator(); it.hasNext();) {
                Element nd = (Element) it.next();
                Way way = new Way();

                way.setId(Long.parseLong(nd.getAttribute("id").getValue()));

                children2 = nd.getChildren("nd");
                children3 = nd.getChildren("tag");

                ArrayList<Node> nodes = new ArrayList<>();
                ArrayList<String[]> tags = new ArrayList<>();

                if (children2.size() > 0) {
                    for (Iterator it2 = children2.iterator(); it2.hasNext();) {
                        Element nd2 = (Element) it2.next();

                        Node node = new Node();

                        node.setId(Long.parseLong(nd2.getAttribute("ref").getValue()));

                        nodes.add(node);
                        way.setNd(nodes);
                    }
                }

                if (children3.size() > 0) {
                    for (Iterator it3 = children3.iterator(); it3.hasNext();) {
                        Element nd3 = (Element) it3.next();

                        String tag[] = new String[2];

                        tag[0] = nd3.getAttributeValue("k");
                        tag[1] = nd3.getAttributeValue("v");

                        tags.add(tag);
                        way.setTag(tags);
                    }
                }
                ways.put(way.getId(), way);
            }
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return ways;
    }

    /** Create only arcs hashmap **/
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

    /** Others **/
    public HashMap simplificationWays(HashMap<Long, Way> ways) {

        HashMap<Long, Way> newWays = new HashMap<>();

        ways.forEach((key, value) -> {

            Way way = new Way();
            way.setId(key);

            //int sizeNodes = value.getNd().size();
            //System.out.println(sizeNodes);
            ArrayList<Node> nodes = new ArrayList<>();

            value.getNd().forEach((nd) -> {

                if (value.getNd().indexOf(nd) == 0 || value.getNd().indexOf(nd) == value.getNd().size() - 1) {
                    nodes.add(nd);
                    way.setNd(nodes);
                    newWays.put(way.getId(), way);
                }
            });
        });
        return newWays;
    }

    public int approximationNodesItaly(String pathFile) {
        File file = new File(pathFile);
        SAXBuilder saxBuilder = new SAXBuilder();

        float minLat = 0;
        float maxLat = 0;
        float minLon = 0;
        float maxLon = 0;
        float minLatItaly = (float) 35.2924;
        float maxLatItaly = (float) 47.0529;
        float minLonItaly = (float) 6.3732;
        float maxLonItaly = (float) 18.3118;

        int numNodes = 0;
        int numNodesItaly = 0;

        float side1 = 0;
        float side2 = 0;
        float side1Italy = maxLonItaly - minLonItaly;
        float side2Italy = maxLatItaly - minLatItaly;

        float area = 0;
        float areaItaly = side1Italy * side2Italy;

        try {
            Document doc = saxBuilder.build(file); //ottenere il documento
            Element root = doc.getRootElement(); //ottenere la radice
            List children = root.getChildren("bounds"); //ottenere i figli

            for (Iterator it = children.iterator(); it.hasNext();) {
                Element nd = (Element) it.next();

                minLat = (Float.parseFloat(nd.getAttribute("minlat").getValue()));
                maxLat = (Float.parseFloat(nd.getAttribute("maxlat").getValue()));
                minLon = (Float.parseFloat(nd.getAttribute("minlon").getValue()));
                maxLon = (Float.parseFloat(nd.getAttribute("maxlon").getValue()));
            }
        } catch (IOException | NumberFormatException | JDOMException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            Document doc = saxBuilder.build(file); //ottenere il documento
            Element root = doc.getRootElement(); //ottenere la radice
            List children2 = root.getChildren("node"); //ottenere i figli

            for (Iterator it2 = children2.iterator(); it2.hasNext();) {
                Element nd = (Element) it2.next();

                numNodes++;

            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        side1 = maxLon - minLon;
        side2 = maxLat - minLat;
        area = side1 * side2;

        numNodesItaly = (int) ((areaItaly * numNodes) / (area));

        return numNodesItaly;
    }

    public HashMap simulationCreateNodes(int numNodes) {
        HashMap<Long, Node> nodes = new HashMap<>();

        System.out.println("Simulation: " + numNodes + " nodes");

        try {
            for (int i = 0; i < numNodes; i++) {

                Node node = new Node();

                node.setId(new Long(i));
                node.setLat(i);
                node.setLon(i);

                nodes.put(node.getId(), node);
            }
        } catch (NumberFormatException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return nodes;
    }

    /** Print **/
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
            ArrayList<Node> nodi = value.getNd();
            if (nodi != null && nodi.size() > 0) {
                nodi.forEach((nd) -> {
                    System.out.println("   nd: " + nd.getId() + "   lat: " + nd.getLat() + "   lon: " + nd.getLon());
                });
            }

            ArrayList<String[]> tags = value.getTag();
            if (tags != null && tags.size() > 0) {
                tags.forEach((tag) -> {
                    System.out.println("      k: " + tag[0] + "      v: " + tag[1]);
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
}
