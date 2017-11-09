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

    public HashSet<Node> applyPercorsi(HashMap<Long, Node> nodes, HashSet<Node> nodes_students, HashSet<Route> routes) {

        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node_student = it.next();

            ArrayList<Percorso> percorsi_euclide = new ArrayList<>();

            for (Iterator<Route> it1 = routes.iterator(); it1.hasNext();) {
                Route route = it1.next();

                if (route == node_student.getRoute()) {
                    for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
                        Node node_route = it2.next();

                        Percorso percorso_euclide = new Percorso();

                        ArrayList<Node> nodes_euclide = new ArrayList<>();
                        nodes_euclide.add(node_student);
                        nodes_euclide.add(node_route);
                        percorso_euclide.setNodes(nodes_euclide);

                        double d = (node_route.getLat() - node_student.getLat()) * (node_route.getLat() - node_student.getLat()) + (node_route.getLon() - node_student.getLon()) * (node_route.getLon() - node_student.getLon());
                        percorso_euclide.setDistanza(d);
                        percorsi_euclide.add(percorso_euclide);
                    }

                    System.out.println("\nEuclide\n");
                    for (int i = 0; i < percorsi_euclide.size(); i++) {
                        System.out.println("Dal nodo studente: " + percorsi_euclide.get(i).getNodes().get(0).getIndex()
                                + " al nodo tratta " + percorsi_euclide.get(i).getNodes().get(1).getIndex()
                                + " distanza: " + percorsi_euclide.get(i).getDistanza());
                    }

                }
            }

            System.out.println("\nEuclide ordinato\n");
            for (int i = 0; i < percorsi_euclide.size(); i++) {
                System.out.println("Dal nodo studente: " + percorsi_euclide.get(i).getNodes().get(0).getIndex()
                        + " al nodo tratta " + percorsi_euclide.get(i).getNodes().get(1).getIndex()
                        + " distanza: " + percorsi_euclide.get(i).getDistanza());
            }

            node_student.setPercorsi_euclide(percorsi_euclide);

            ArrayList<Percorso> percorsi_dijkstra = new ArrayList<>();

            boolean min = false;
            int i = 0;
            while (!min && i < percorsi_euclide.size()) {
                Percorso percorso_dijkstra = dijkstra.run(node_student, percorsi_euclide.get(i).getNodes().get(1), nodes, false);
                percorsi_dijkstra.add(percorso_dijkstra);

                if (percorso_dijkstra.getDistanza() <= percorsi_euclide.get(i).getDistanza()) {
                    min = true;
                }
                i++;
            }
            node_student.setPercorsi_dijkstra(percorsi_dijkstra);

            System.out.println("\nDijkstra\n");
            for (int j = 0; j < percorsi_dijkstra.size(); j++) {
                System.out.println("distanza: " + percorsi_dijkstra.get(j).getDistanza());
            }
        }

        return nodes_students;
    }

    public HashSet<Node> idealPercorso(HashSet<Node> nodes_students) {

        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node_student = it.next();

            Percorso minPercorso = null;
            for (Iterator<Percorso> it2 = node_student.getPercorsi_dijkstra().iterator(); it2.hasNext();) {
                Percorso percorso = it2.next();

                if (minPercorso == null || minPercorso.getDistanza() > percorso.getDistanza()) {
                    minPercorso = percorso;
                }
            }

            node_student.setIdealPercorso(minPercorso);
            Node stop = minPercorso.getNodes().get(minPercorso.getNodes().size() - 1);
            node_student.setIdealStop(stop);

        }
        return nodes_students;
    }

    public String printPercorsi(HashSet<Node> nodes_students) {
        String output_percorsi = "";

        output_percorsi += "NODI STUDENTI: " + nodes_students.size() + "\n";
        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node_student = it.next();

            output_percorsi += "________________________" + "\n";

            for (Iterator<Percorso> it1 = node_student.getPercorsi_dijkstra().iterator(); it1.hasNext();) {
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

    public String printReportStudenti(HashSet<Node> nodes_students) {
        String output = "";

        output += "NODI STUDENTI: " + nodes_students.size() + "\n";
        output += "________________________" + "\n";
        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node node = it.next();

            Route route = node.getRoute();
            Node idealStop = node.getIdealStop();
            Percorso percorso = node.getIdealPercorso();
            Percorso idealPercorso = node.getIdealPercorso();

            output += "ROUTE\n";
            output += "route: " + route.getName() + " stop: " + idealStop.getIndex() + " distanza: " + percorso.getDistanza() + "\n";
        }
        return output;
    }
}
