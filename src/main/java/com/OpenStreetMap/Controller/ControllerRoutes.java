package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;

import java.awt.*;
import java.util.*;

public class ControllerRoutes {
    Dijkstra dijkstra = new Dijkstra();

    public HashSet<Route> read(String stringAreaText, HashMap<Long, Node> nodes) {
        String arrayString[] = null;
        arrayString = splitta(stringAreaText);

        HashMap<String, ArrayList<Node>> checkpoints_routes = new HashMap<>();
        ArrayList<Node> nodes_route = null;

        int i = 0;

        while (arrayString[i].equals("%")) {
            nodes_route = new ArrayList<>();
            i++;
            String name = arrayString[i];
            i++;

            while (arrayString[i].equals("#")) {

                i++;
                float latitudine = Float.parseFloat(arrayString[i]);
                i++;
                float longitudine = Float.parseFloat(arrayString[i]);

                Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);

                System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon());

                nodes_route.add(node);

                if (i < arrayString.length - 1)
                    i++;

            }
            checkpoints_routes.put(name, nodes_route);

        }

        HashSet<Route> routes = applyDijkstra(nodes, checkpoints_routes);
        printRoutes(routes);

        return routes;

    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    private HashSet<Route> applyDijkstra(HashMap<Long, Node> nodes, HashMap<String, ArrayList<Node>> checkpoint_routes) {
        HashSet<Route> routes = new HashSet<>();

        for (Map.Entry<String, ArrayList<Node>> entry : checkpoint_routes.entrySet()) {
            String name = entry.getKey();
            ArrayList<Node> checkpoints = entry.getValue();

            ArrayList<Node> percorso = new ArrayList<>();
            double distanza = 0;

            Route route = new Route();
            route.setName(name);

            //set color
            int red = 5 * (int) (Math.random() * 52);
            int green = 5 * (int) (Math.random() * 52);
            int blue = 5 * (int) (Math.random() * 52);
            Color randomColor = new Color(red, green, blue);
            route.setColor(randomColor);

            for (int i = 0; i < checkpoints.size(); i++) {
                if (i != checkpoints.size() - 1) {

                    percorso.addAll(dijkstra.run(checkpoints.get(i), checkpoints.get(i + 1), nodes, false));
                    distanza += percorso.get(percorso.size() - 1).getDistanza();
                }
            }
            route.setNodes(percorso);
            route.setDistanza(distanza);
            routes.add(route);
        }

        return routes;
    }

    private void printRoutes(HashSet<Route> routes) {
        System.out.println("Tratte " + routes.size());

        for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
            Route route = it.next();

            System.out.println("        TRATTA: " + route.getName());
            System.out.println("            DISTANZA: " + route.getDistanza());

            for (Iterator<Node> it1 = route.getNodes().iterator(); it1.hasNext(); ) {
                Node node = it1.next();

                System.out.println("            id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon());
            }
        }
    }
}
