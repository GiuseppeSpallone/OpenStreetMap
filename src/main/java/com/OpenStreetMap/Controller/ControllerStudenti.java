package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;

import java.util.*;

public class ControllerStudenti {

    Dijkstra dijkstra = new Dijkstra();
    GoogleCoordinate googleCoordinate = new GoogleCoordinate();

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

            if (i < arrayString.length - 1) {
                i++;
            }

        }

        while (arrayString[i].equals("*")) {

            i++;
            String name_route = arrayString[i];
            i++;
            String paese = arrayString[i];
            i++;
            int num_studenti = Integer.parseInt(arrayString[i]);

            Route route = Route.getRouteByName(routes, name_route);

            double[] lat_lon = googleCoordinate.loadGoogleCoordinate(paese);
            float latitudine = (float) lat_lon[0];
            float longitudine = (float) lat_lon[1];
            System.out.println(paese + " coordinate: " + latitudine + " " + longitudine);

            Node node = Node.nodeVicinoByLatLon(nodes, latitudine, longitudine);
            node.setRoute(route);
            node.setNum_studenti(num_studenti);

            System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon() + " tratta: " + node.getRoute().getName() + " num: " + node.getNum_studenti());

            nodes_students.add(node);

            if (i < arrayString.length - 1) {
                i++;
            }

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
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
            Node node = it.next();
            node.setNum_studenti(0);
        }
    }

    public HashMap<Node, HashSet<Percorso>> allRoute(HashMap<Long, Node> nodes, HashSet<Node> nodes_students, HashSet<Route> routes) {
        //tutte le route
        HashMap<Node, HashSet<Percorso>> students_percorsi = new HashMap<>();

        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node_student = it.next();

            HashSet<Percorso> percorsi_students = new HashSet<>();

            for (Iterator<Route> it1 = routes.iterator(); it1.hasNext();) {
                Route route = it1.next();

                for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
                    Node node_route = it2.next();
                    Percorso percorso = dijkstra.run(node_student, node_route, nodes, false);
                    percorsi_students.add(percorso);
                }
            }
            students_percorsi.put(node_student, percorsi_students);
        }
        System.out.print(printPercorsi(students_percorsi));

        return students_percorsi;
    }

    public HashMap<Node, HashSet<Percorso>> route(HashMap<Long, Node> nodes, HashSet<Node> nodes_students, HashSet<Route> routes) {
        //solo route prefissata
        HashMap<Node, HashSet<Percorso>> students_percorsi = new HashMap<>();

        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node_student = it.next();

            HashSet<Percorso> percorsi_students = new HashSet<>();

            for (Iterator<Route> it1 = routes.iterator(); it1.hasNext();) {
                Route route = it1.next();

                if (node_student.getRoute() == route) {

                    for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
                        Node node_route = it2.next();
                        Percorso percorso = dijkstra.run(node_student, node_route, nodes, false);
                        percorsi_students.add(percorso);
                    }
                }
            }
            students_percorsi.put(node_student, percorsi_students);
        }
        System.out.print(printPercorsi(students_percorsi));
        return students_percorsi;
    }

    public HashMap<Node, Percorso> ideal(HashMap<Node, HashSet<Percorso>> students, HashSet<Route> routes, boolean ideal) {
        HashMap<Node, Percorso> students_min_percorsi = new HashMap<>();

        for (Map.Entry<Node, HashSet<Percorso>> entry : students.entrySet()) {
            Node node = entry.getKey();
            HashSet<Percorso> percorsi = entry.getValue();

            Percorso minPercorso = null;
            for (Iterator<Percorso> it = percorsi.iterator(); it.hasNext();) {
                Percorso percorso = it.next();

                if (minPercorso == null || minPercorso.getDistanza() > percorso.getDistanza()) {
                    minPercorso = percorso;
                }
            }

            Node stop = minPercorso.getNodes().get(minPercorso.getNodes().size() - 1);
            Route stopRoute = Route.getRouteByNode(routes, stop);

            if (ideal) {
                node.setIdealRoute(stopRoute);
                node.setIdealStopIdealRoute(stop);
                node.setIdealPercorso(minPercorso);
            } else {
                node.setIdealStop(stop);
                node.setPercorso(minPercorso);
            }

            students_min_percorsi.put(node, minPercorso);
        }

        System.out.println("FERMATE IDEALI -->");
        System.out.print(printPercorso(students_min_percorsi));
        return students_min_percorsi;
    }

    public String printPercorsi(HashMap<Node, HashSet<Percorso>> students_percorsi) {
        String output_percorsi = "";

        output_percorsi += "NODI STUDENTI: " + students_percorsi.size() + "\n";
        for (Iterator<HashSet<Percorso>> it = students_percorsi.values().iterator(); it.hasNext();) {
            HashSet<Percorso> percorsi = it.next();

            output_percorsi += "________________________" + "\n";

            for (Iterator<Percorso> it1 = percorsi.iterator(); it1.hasNext();) {
                Percorso percorso = it1.next();

                output_percorsi += "\nDISTANZA PERCORSO: " + percorso.getDistanza() + "\n";

                for (Iterator<Node> it2 = percorso.getNodes().iterator(); it2.hasNext();) {
                    Node node = it2.next();

                    output_percorsi += "id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon() + "\n";
                }
            }
        }
        return output_percorsi;
    }

    public String printPercorso(HashMap<Node, Percorso> students_min_percorsi) {
        String output_percorso = "";

        output_percorso += "NODI STUDENTI: " + students_min_percorsi.size() + "\n";
        for (Iterator<Percorso> it = students_min_percorsi.values().iterator(); it.hasNext();) {
            Percorso percorso = it.next();

            output_percorso += "________________________" + "\n";

            output_percorso += "\nDISTANZA PERCORSO: " + percorso.getDistanza() + "\n";

            for (Iterator<Node> it2 = percorso.getNodes().iterator(); it2.hasNext();) {
                Node node = it2.next();

                output_percorso += "id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon() + "\n";
            }

        }
        return output_percorso;
    }

    public String printReportStudenti(HashSet<Node> nodes_students) {
        String output = "";

        output += "NODI STUDENTI: " + nodes_students.size() + "\n";
        output += "________________________" + "\n";
        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node = it.next();

            Route route = node.getRoute();
            Route idealRoute = node.getIdealRoute();
            Node idealStop = node.getIdealStop();
            Node idealStopIdealRoute = node.getIdealStopIdealRoute();
            Percorso percorso = node.getPercorso();
            Percorso idealPercorso = node.getIdealPercorso();

            output += "ROUTE PREFISSATA\n";
            output += "route: " + route.getName() + " stop: " + idealStop.getIndex() + " distanza: " + percorso.getDistanza() + "\n";
            output += "ROUTE IDEALE\n";
            output += "route: " + idealRoute.getName() + " stop: " + idealStopIdealRoute.getIndex() + " distanza: " + idealPercorso.getDistanza() + "\n";
        }
        return output;
    }
}
