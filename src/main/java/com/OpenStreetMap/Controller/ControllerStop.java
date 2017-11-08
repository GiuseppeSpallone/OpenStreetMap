package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Combination;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ControllerStop {

    /*
% 1 1
# 41.560204 14.664995
# 41.560986 14.668059
# 41.563812 14.670908
# 1 41.560078 14.668353 15
# 1 41.562477 14.668082 30
# 1 41.560715 14.671404 5
     */
    Dijkstra dijkstra = new Dijkstra();

    public void run(HashSet<Node> nodes_students, HashSet<Route> routes) {
        HashSet<Combination> combinations;
        combinations = creaCombinazioni(routes);
        combinations = assegnaFermate(combinations, nodes_students);
        combinations = valutazione(combinations);
        combinations = valutazioneMinRoutes(combinations, routes);
        assegnaFermataReal(routes, nodes_students);

        printCombinations(combinations);
        printStudent(nodes_students);

    }

    private HashSet<Combination> creaCombinazioni(HashSet<Route> routes) {
        //creo tutte le combinazioni di fermate 
        HashSet<Combination> combinations = new HashSet<>();

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            Percorso percorso_route = route.getPercorso();
            ArrayList<Node> nodes_route = percorso_route.getNodes();
            Node partenza = nodes_route.get(0);
            Node arrivo = nodes_route.get(nodes_route.size() - 1);

            for (int i = 0; i < nodes_route.size(); i++) {
                Node _stop = nodes_route.get(i);

                Combination combination = new Combination();

                ArrayList<Node> stops = new ArrayList<>();
                if (_stop != partenza && _stop != arrivo) {
                    stops.add(partenza);
                    stops.add(_stop);
                    stops.add(arrivo);

                    //per evitare ripetizioni
                    if (!Combination.isStops(combinations, stops)) {
                        combination.setRoute(route);
                        combination.setStops(stops);

                        combinations.add(combination);
                    }

                }
            }
        }

        //stampa combiazione
        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();
            System.out.println("COMBINAZIONE TRATTA: " + combination.getRoute().getName());
            for (Iterator<Node> it2 = combination.getStops().iterator(); it2.hasNext();) {
                Node stop = it2.next();

                System.out.println("Stop " + stop.getIndex());
            }
        }

        return combinations;
    }

    private HashSet<Combination> assegnaFermate(HashSet<Combination> combinations, HashSet<Node> nodes_students) {
        //per ogni combinazione di fermate assegno ad ogni studente la fermata più vicina
        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();

            HashMap<Node, Percorso> students = new HashMap<>();

            for (Iterator<Node> it3 = nodes_students.iterator(); it3.hasNext();) {
                Node node_student = it3.next();
                ArrayList<Percorso> percorsi = node_student.getPercorsi();

                double minDistanza = Double.MAX_VALUE;
                Node stop_student = null;
                Percorso percorso_student = null;

                if (combination.getRoute() == node_student.getRoute()) {

                    for (Iterator<Percorso> it4 = percorsi.iterator(); it4.hasNext();) {
                        Percorso percorso = it4.next();
                        ArrayList<Node> nodes_percorso = percorso.getNodes();
                        double distanza = percorso.getDistanza();
                        Node lastNode = nodes_percorso.get(nodes_percorso.size() - 1);

                        for (Iterator<Node> it2 = combination.getStops().iterator(); it2.hasNext();) {
                            Node stop = it2.next();

                            if (lastNode == stop) {
                                if (distanza <= minDistanza) {
                                    minDistanza = distanza;
                                    stop_student = stop;
                                    percorso_student = percorso;
                                }
                            }

                        }

                    }
                }
                students.put(node_student, percorso_student);
            }
            combination.setStudents(students);
        }
        return combinations;
    }

    private HashSet<Combination> valutazione(HashSet<Combination> combinations) {
        //valutazione per ogni combinazione

        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();
            Route route_combination = combination.getRoute();
            HashMap<Node, Percorso> students = combination.getStudents();
            ArrayList<Node> stops = combination.getStops();

            double value = 0;
            double num = 0;
            double z = 0;
            for (Map.Entry<Node, Percorso> entry : students.entrySet()) {
                Node student = entry.getKey();
                Percorso percorso = entry.getValue();

                value += percorso.getDistanza() * (double) student.getNum_studenti();
                num += (double) student.getNum_studenti();
            }

            z = value / num;
            combination.setValue(z);

        }

        return combinations;
    }

    private HashSet<Combination> valutazioneMinRoutes(HashSet<Combination> combinations, HashSet<Route> routes) {
        //scelta di una combinazione per rotta con minor valutazione

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            double minValue = Double.MAX_VALUE;
            ArrayList<Node> stops_route = new ArrayList<>();

            for (Iterator<Combination> it2 = combinations.iterator(); it2.hasNext();) {
                Combination combination = it2.next();
                Route route_combination = combination.getRoute();
                HashMap<Node, Percorso> students = combination.getStudents();
                ArrayList<Node> stops = combination.getStops();

                if (route_combination == route) {
                    if (combination.getValue() <= minValue) {
                        minValue = combination.getValue();
                        stops_route = stops;
                    }
                }

            }

            route.setFermate(stops_route);
        }

        //stampa
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            System.out.print("TRATTA " + route.getName());

            for (Iterator<Node> it2 = route.getFermate().iterator(); it2.hasNext();) {
                Node stop = it2.next();

                System.out.print(stop.getIndex() + " ");
            }
        }
        return combinations;
    }

    private void assegnaFermataReal(HashSet<Route> routes, HashSet<Node> nodes_students) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            ArrayList<Node> stops = route.getFermate();

            for (Iterator<Node> it3 = nodes_students.iterator(); it3.hasNext();) {
                Node node_student = it3.next();
                ArrayList<Percorso> percorsi = node_student.getPercorsi();

                double minDistanza = Double.MAX_VALUE;
                Node stop_student = null;

                if (route == node_student.getRoute()) {

                    for (Iterator<Percorso> it4 = percorsi.iterator(); it4.hasNext();) {
                        Percorso percorso = it4.next();
                        ArrayList<Node> nodes_percorso = percorso.getNodes();
                        double distanza = percorso.getDistanza();
                        Node lastNode = nodes_percorso.get(nodes_percorso.size() - 1);

                        for (Iterator<Node> it2 = stops.iterator(); it2.hasNext();) {
                            Node stop = it2.next();

                            if (lastNode == stop) {
                                if (distanza <= minDistanza) {
                                    minDistanza = distanza;
                                    stop_student = stop;
                                }
                            }

                        }

                    }
                }
                node_student.setRealStop(stop_student);
            }

        }
    }

    private void printCombinations(HashSet<Combination> combinations) {
        //stampa
        for (Iterator<Combination> it = combinations.iterator(); it.hasNext();) {
            Combination combination = it.next();

            System.out.println("-----------");
            System.out.println("Combinazione " + combination.getValue());
            System.out.println("Route: " + combination.getRoute().getName());

            for (Iterator<Node> it2 = combination.getStops().iterator(); it2.hasNext();) {
                Node node = it2.next();

                System.out.println("Stop: " + node.getIndex());

            }
            System.out.println("-");
            for (Map.Entry<Node, Percorso> entry : combination.getStudents().entrySet()) {
                Node student = entry.getKey();
                Percorso percorso_student = entry.getValue();

                System.out.println("Studente: " + student.getIndex() + " distanza: " + percorso_student.getDistanza());

                for (Iterator<Node> it3 = percorso_student.getNodes().iterator(); it3.hasNext();) {
                    Node node = it3.next();

                    //System.out.println("Nodo: " + node.getIndex());
                }
                System.out.println("-");
            }
        }
    }

    private void printStudent(HashSet<Node> nodes_students) {
        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext();) {
            Node student = it.next();

            System.out.println("Studente: " + student.getIndex());
            System.out.println("Fermata: " + student.getRealStop().getIndex());

        }
    }
}
