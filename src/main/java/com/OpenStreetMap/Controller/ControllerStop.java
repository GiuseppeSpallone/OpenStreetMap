package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;
import com.sun.javafx.geom.Matrix3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ControllerStop {

    /*
% 1 0
# 41.560204 14.664995
# 41.560986 14.668059
# 41.561756 14.669296
% 2 0
# 41.563812 14.670908
# 41.565075 14.674816
# 1 41.559925 14.66891 15
# 1 41.5643 14.664442 20
# 1 41.561012 14.66641 7
# 2 41.563778 14.672616 7
# 2 41.56556 14.671876 6
     */
    Dijkstra dijkstra = new Dijkstra();

    public HashMap<Node, Double> stop(HashMap<Long, Node> nodes, HashSet<Route> routes, HashSet<Node> nodes_students) {

        HashMap<Node, Double> stops_students = new HashMap<>();

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            Percorso percorso_route = route.getPercorso();
            ArrayList<Node> nodes_route = percorso_route.getNodes();
            Node partenza = nodes_route.get(0);
            Node arrivo = nodes_route.get(nodes_route.size() - 1);

            ArrayList<Node> stops = new ArrayList<>();
            stops.add(partenza);
            stops.add(arrivo);
            route.setFermate(stops);

            for (Iterator<Node> it2 = nodes_students.iterator(); it2.hasNext();) {
                Node node_student = it2.next();
                Node idealStop = node_student.getIdealStop();

                if (node_student.getRoute() == route) {
                    double minDistanza = Double.MAX_VALUE;
                    Node stop = null;

                    for (int i = 0; i < stops.size(); i++) {
                        Percorso percorso = dijkstra.run(idealStop, stops.get(i), nodes, false);
                        double distanza = percorso.getDistanza();

                        if (distanza <= minDistanza) {
                            minDistanza = distanza;
                            stop = stops.get(i);
                        }
                    }
                    node_student.setRealStop(stop);
                    stops_students.put(node_student, minDistanza);
                }

            }

        }
        return stops_students;
    }

    public void oneStop(HashMap<Long, Node> nodes, HashSet<Route> routes, HashSet<Node> nodes_students) {
        String out = "";

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            Percorso percorso_route = route.getPercorso();
            ArrayList<Node> nodes_route = percorso_route.getNodes();
            Node partenza = nodes_route.get(0);
            Node arrivo = nodes_route.get(nodes_route.size() - 1);

            //HashMap<Node, ArrayList<Node>> ideal_stops = new HashMap<>();
            
            ArrayList < Node > ideal_stops = new ArrayList<>();
            Double distanza_ideal_stops = Double.MAX_VALUE;

            for (int i = 0; i < nodes_route.size(); i++) {
                Node _stop = nodes_route.get(i);

                ArrayList<Node> stops = new ArrayList<>();
                if (_stop != partenza && _stop != arrivo) {
                    stops.add(partenza);
                    stops.add(_stop);
                    stops.add(arrivo);

                    HashMap<Node, Double> stops_students = new HashMap<>();

                    for (Iterator<Node> it2 = nodes_students.iterator(); it2.hasNext();) {
                        Node node_student = it2.next();
                        Node idealStop = node_student.getIdealStop();

                        double minDistanza = Double.MAX_VALUE;
                        Node stop = null;

                        if (node_student.getRoute() == route) {

                            for (int j = 0; j < stops.size(); j++) {
                                Percorso percorso = dijkstra.run(idealStop, stops.get(j), nodes, false);
                                double distanza = percorso.getDistanza();

                                if (distanza <= minDistanza) {
                                    minDistanza = distanza;
                                    stop = stops.get(j);
                                }
                            }
                            stops_students.put(node_student, minDistanza);

                        }
                    }

                    double value = 0;
                    double num = 0;
                    for (Map.Entry<Node, Double> entry : stops_students.entrySet()) {
                        Node node = entry.getKey();

                        Double distanza = entry.getValue();
                        int numStudents = node.getNum_studenti();

                        value = +(numStudents * distanza);
                        num = +numStudents;
                    }
                    double z = value / num;

                    if (z <= distanza_ideal_stops) {
                        distanza_ideal_stops = z;
                        ideal_stops = stops;
                    }

                    /*double midDistanza_realStop = Double.MAX_VALUE;
                    Node realStop = null;
                    for (Iterator<Node> it2 = ideal_stops.iterator(); it2.hasNext();) {
                        Node node = it2.next();

                        Percorso percorso = dijkstra.run(node.getIdealStop(), node, nodes, false);
                        double distanza = percorso.getDistanza();

                        if (distanza <= midDistanza_realStop) {
                            midDistanza_realStop = distanza;
                            realStop = node;

                        }
                    }*/
                }
            }

            route.setFermate(ideal_stops);

            out += "DISTANZA MEDIA: " + distanza_ideal_stops + "\n";
            out += "FERMATE: " + ideal_stops.get(0).getIndex() + " " + ideal_stops.get(1).getIndex() + " " + ideal_stops.get(2).getIndex() + "\n\n";

        }

        System.out.print(out);
    }
}
