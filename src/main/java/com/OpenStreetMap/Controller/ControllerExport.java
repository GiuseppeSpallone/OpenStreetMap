package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ControllerExport {
    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;

    public void export(File file, HashMap<Long, Node> nodes, HashSet<Arc> arcs) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            System.out.println("EXPORT: " + file.getName());
            System.out.println("   Creato file: " + file.getName());

            out.println(nodes.size() + " " + arcs.size());

            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
                Node n = it.next();
                out.println(n.getIndex() + " " + n.getId() + " " + n.getX() + " " + n.getY() + " " + n.getLat() + " " + n.getLon());
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

    public HashMap<Long, Node> getNodes() {
        return nodes;
    }

    public HashSet<Arc> getArcs() {
        return arcs;
    }
}
