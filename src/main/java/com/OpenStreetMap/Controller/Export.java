package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Export {

    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;

    public void exportMap(File file, HashMap<Long, Node> nodes, HashSet<Arc> arcs) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            System.out.println("EXPORT: " + file.getName());
            System.out.println("   Creato file: " + file.getName());

            out.println(nodes.size() + " " + arcs.size());

            for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
                Node n = it.next();
                out.println(n.getIndex() + " " + n.getId() + " " + n.getX() + " " + n.getY() + " " + n.getLat() + " " + n.getLon());
            }

            for (Iterator<Arc> it = arcs.iterator(); it.hasNext();) {
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

    public void exportReport(File file, HashSet<Node> nodes_students, HashSet<Route> routes) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            System.out.println("EXPORT: " + file.getName());
            System.out.println("   Creato file: " + file.getName());

            for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
                Route route = it.next();

                String name = route.getName();
                double value = route.getMinCombination().getValue();
                int numFermate = route.getNumFermate();
                double distanza = route.getDistanza();
                ArrayList<Node> fermate = route.getFermate_effettive();

                out.println("Tratta: " + name + " Valore: " + value);
                out.print("   Fermate:");

                for (int i = 0; i < fermate.size(); i++) {

                    out.print(" " + fermate.get(i).getIndex());
                }

                out.println("");
                out.println("   Lunghezza: " + (int) distanza + "m");
            }
            out.println("");
            out.println("");

            for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
                Route route = it.next();

                for (Iterator<Node> it1 = nodes_students.iterator(); it1.hasNext();) {
                    Node node = it1.next();
                    Route route_student = node.getRoute();

                    if (route_student == route) {
                        int numStudents = node.getNum_studenti();
                        int realStop = node.getRealStop().getIndex();
                        double realDistanza = node.getRealPercorso().getDistanza();
                        int idealStop = node.getIdealStop().getIndex();
                        double idealDistanza = node.getIdealPercorso().getDistanza();

                        out.println("Nodo: " + node.getIndex() + "  numero studenti: " + numStudents);
                        out.println("   Tratta: " + route_student.getName());
                        out.println("       Fermata ideale: " + idealStop + "; distanza: " + (int) idealDistanza + "m");
                        out.println("       Fermata effettiva: " + realStop + "; distanza: " + (int) realDistanza + "m");
                    }

                }

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
