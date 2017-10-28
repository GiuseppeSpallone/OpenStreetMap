package com.OpenStreetMap.Controller;


import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;

import java.util.*;

public class ControllerStudenti {
    Dijkstra dijkstra = new Dijkstra();

    public HashSet<Node> read(String stringAreaText, HashMap<Long, Node> nodes, HashSet<Route> routes) {
        reset(nodes);

        String arrayString[] = null;
        arrayString = splitta(stringAreaText);

        HashSet<Node> nodes_students = new HashSet<>();

        int i = 0;
        while (arrayString[i].equals("#")) {

            i++;
            String name_route = arrayString[i];
            i++;
            float latitudine = Float.parseFloat(arrayString[i]);
            i++;
            float longitudine = Float.parseFloat(arrayString[i]);
            i++;
            int num_studenti = Integer.parseInt(arrayString[i]);

            Route route = Route.getRouteByName(routes, name_route);

            Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);
            node.setRoute(route);
            node.setNum_studenti(num_studenti);

            System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon() + " tratta: " + node.getRoute().getName() + " num: " + node.getNum_studenti());

            nodes_students.add(node);

            if (i < arrayString.length - 1)
                i++;

        }
        return nodes_students;

    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    private void reset(HashMap<Long, Node> nodes) {
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setNum_studenti(0);
        }
    }

    public HashMap<Node, HashMap<ArrayList<Node>, Double>> allStop(HashMap<Long, Node> nodes, HashSet<Node> nodes_students, HashSet<Route> routes) {
        HashMap<Node, HashMap<ArrayList<Node>, Double>> students_percorsi = new HashMap();
        HashMap<ArrayList<Node>, Double> percorsi = null;

        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext(); ) {
            Node node_student = it.next();

            for (Iterator<Route> it1 = routes.iterator(); it1.hasNext(); ) {
                Route route = it1.next();

                if (node_student.getRoute() == route) {
                    percorsi = new HashMap<>();
                    double distanza = 0;

                    for (Iterator<Node> it2 = route.getNodes().iterator(); it2.hasNext(); ) {
                        Node node_route = it2.next();

                        ArrayList<Node> percorso = new ArrayList<>();

                        percorso = dijkstra.run(node_student, node_route, nodes, false);
                        distanza = percorso.get(percorso.size() - 1).getDistanza();
                        percorsi.put(percorso, distanza);
                    }

                }
            }
            students_percorsi.put(node_student, percorsi);
        }

        printPercorsi(students_percorsi);
        return students_percorsi;
    }

    public void idealStop(HashMap<Node, HashSet<ArrayList<Node>>> students_percorsi) {

    }

    private void printPercorsi(HashMap<Node, HashMap<ArrayList<Node>, Double>> students_percorsi) {
        System.out.println("NODI STUDENTI: " + students_percorsi.size());

        for (Iterator<HashMap<ArrayList<Node>, Double>> it = students_percorsi.values().iterator(); it.hasNext(); ) {
            HashMap<ArrayList<Node>, Double> percorsi = it.next();

            System.out.println("________________________");

            for (Map.Entry<ArrayList<Node>, Double> entry : percorsi.entrySet()) {
                ArrayList<Node> percorso = entry.getKey();
                Double distanza = entry.getValue();

                System.out.println("        DISTANZA PERCORSO: " + distanza);

                for (Iterator<Node> it2 = percorso.iterator(); it2.hasNext(); ) {
                    Node node = it2.next();

                    System.out.println("            id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon());
                }
            }
        }
    }


}
