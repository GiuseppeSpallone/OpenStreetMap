package com.Bus.Controller;

import com.Bus.Model.Arc;
import com.Bus.Model.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ImportPlotMap {
    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;

    public boolean readFile(File file) {
        nodes = new HashMap<>();
        arcs = new HashSet<>();

        FileReader inFile = null;

        try {
            inFile = new FileReader(file);
            BufferedReader in = new BufferedReader(inFile);
            String vs[] = null;
            String s = in.readLine();

            vs = splitta(s);

            int nVertices = Integer.parseInt(vs[0]);
            int nEdges = Integer.parseInt(vs[1]);

            for (int i = 0; i < nVertices; i++) {
                s = in.readLine();
                vs = splitta(s);

                long id = Long.parseLong(vs[1]);
                int x = Integer.parseInt(vs[2]);
                int y = Integer.parseInt(vs[3]);
                float lat = Float.parseFloat(vs[4]);
                float lon = Float.parseFloat(vs[5]);
                //int mark = Integer.parseInt(vs[6]);

                Node n = new Node();

                n.setId(id);
                n.setX(x);
                n.setY(y);
                n.setLat(lat);
                n.setLon(lon);
                n.setIndex(i);
                //n.setMark(mark);

                nodes.put(new Long(i), n);
            }

            for (int i = 0; i < nEdges; i++) {
                s = in.readLine();
                vs = splitta(s);

                long from = Integer.parseInt(vs[0]);
                long to = Integer.parseInt(vs[1]);
                double length = Float.parseFloat(vs[2]);

                Arc a = new Arc(nodes.get(from), nodes.get(to));
                a.setLength(length);
                a.setIndex(i);

                arcs.add(a);
            }


            for (Iterator<Arc> it = arcs.iterator(); it.hasNext(); ) {
                Arc a = it.next();

                Node from = a.getFrom();
                Node to = a.getTo();

                from.nd_arcs.add(a);
                to.nd_arcs.add(a);
            }

            in.close();


        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                inFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    public HashMap<Long, Node> getNodes() {
        return nodes;
    }

    public HashSet<Arc> getArcs() {
        return arcs;
    }
}
