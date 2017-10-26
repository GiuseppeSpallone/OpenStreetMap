package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ControllerRoute {
    Dijkstra dijkstra = new Dijkstra();

   /* public HashMap<Long, Route> createRoutes(int numRoutes, HashMap<Long, Node> nodes, ArrayList<Node> routeNodes) {
        HashMap<Long, Route> routes = new HashMap<>();

        for (int i = 0; i < numRoutes; i++) {
            Route route = createRoute(nodes, routeNodes);
            Long idFirstNode = route.getNodes().get(0).getId();
            routes.put(idFirstNode, route);
        }

        printRoutes(routes);
        return routes;
    }*/

    public Route createRoute(HashMap<Long, Node> nodes, ArrayList<Node> routeNodes) {
        Route route = new Route();

        ArrayList<Node> percorso_finale = new ArrayList<>();
        ArrayList<ArrayList<Node>> percorso_totale = new ArrayList<>();

        for (int i = 0; i < routeNodes.size(); i++) {
            if (i != routeNodes.size() - 1) {

                ArrayList<Node> percorso = new ArrayList<>();
                percorso = dijkstra.run(routeNodes.get(i), routeNodes.get(i + 1), nodes);
                percorso_totale.add(percorso);
            }
        }

        for (Iterator<ArrayList<Node>> it = percorso_totale.iterator(); it.hasNext(); ) {
            ArrayList<Node> nodi = it.next();

            for (Iterator<Node> it1 = nodi.iterator(); it1.hasNext(); ) {
                Node nodo = it1.next();

                percorso_finale.add(nodo);
            }
        }

        setMarkPercorso(percorso_finale);

        //route.setName("");
        route.setNodes(percorso_finale);
        //route.setDistanza(distanza);

        //printRoute(route);

        return route;
    }

    private void setMarkPercorso(ArrayList<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setMark(1);

            if (i != nodes.size() - 1) {
                Arc arc = Arc.arcByFromTo(nodes.get(i), nodes.get(i + 1));
                arc.setMark(1);
            }
        }
    }

    public void printRoute(Route route) {
        System.out.println("             TRATTA");

        System.out.println("                    PERCORSO --> ");
        System.out.println("                                 DISTANZA " + route.getDistanza());
        for (Iterator<Node> it = route.getNodes().iterator(); it.hasNext(); ) {
            Node nd = it.next();
            System.out.println("                              id: " + nd.getId() + "; index: " + nd.getIndex());
        }
    }

    public void printRoutes(HashMap<Long, Route> routes) {
        for (Iterator<Route> it = routes.values().iterator(); it.hasNext(); ) {
            Route route = it.next();

            System.out.println("TRATTE -->");
            printRoute(route);
        }
    }

    public ArrayList<float[]> readArea(String stringAreaText) {
        String arraySting[] = null;
        arraySting = splitta(stringAreaText);
        ArrayList<float[]> lat_lon = new ArrayList<>();

        for (int i = 0; i < arraySting.length; i++) {

            if (arraySting[i].equals("#")) {

                float latitudine = Float.parseFloat(arraySting[i + 1]);
                float longitudine = Float.parseFloat(arraySting[i + 2]);

                lat_lon.add(new float[]{latitudine, longitudine});

            }
        }

        for (int i = 0; i < lat_lon.size(); i++) {
            System.out.println("Checkpoint " + (i+1));
            System.out.println(lat_lon.get(i)[0] + "," +  lat_lon.get(i)[1]);
        }

        return lat_lon;
    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }
}
