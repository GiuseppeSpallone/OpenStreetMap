package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ControllerRoute {
    Algorithms algorithms = new Algorithms();

    public ArrayList<Node> route(HashMap<Long, Node> nodes, ArrayList<Node> routeNodes) {
        ArrayList<Node> percorso_finale = new ArrayList<>();
        ArrayList<ArrayList<Node>> percorso_totale = new ArrayList<>();

        for (int i = 0; i < routeNodes.size(); i++) {
            if (i != routeNodes.size() - 1) {
                ArrayList<Node> percorso = new ArrayList<>();
                percorso = algorithms.dijkstra(routeNodes.get(i), routeNodes.get(i + 1), nodes);
                percorso_totale.add(percorso);
            }
        }

        for (Iterator<ArrayList<Node>> it = percorso_totale.iterator(); it.hasNext(); ) {
            ArrayList<Node> nodi = it.next();

            for (Iterator<Node> it1 = nodi.iterator(); it1.hasNext(); ) {
                Node nodo = it1.next();

                nodo.setMark(1);
                percorso_finale.add(nodo);
            }
        }
        printPercorso(percorso_finale);
        return percorso_finale;
    }

    private void printPercorso(ArrayList<Node> percorso) {
        System.out.println("TRATTA");

        System.out.println("       PERCORSO --> ");
        for (Iterator<Node> it = percorso.iterator(); it.hasNext(); ) {
            Node nd = it.next();
            System.out.println("                            id: " + nd.getId() + "; index: " + nd.getIndex());
        }
    }
}
