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

    Dijkstra dijkstra = new Dijkstra();

    public void run(HashSet<Node> nodes_students, HashSet<Route> routes) {
        assegnaCombinazione(routes);
        assegnaFermateCombinazioni(routes, nodes_students);
        valutazioneCombinazioni(routes);
        minValutazioneCombinazione(routes);
        assegnaFermateRoute(routes);
        assegnaFermateStudents(routes, nodes_students);
    }

    private void assegnaCombinazione(HashSet<Route> routes) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            int numFermate = route.getNumFermate();

            switch (numFermate) {
                case 1:
                    creaCombinazioni(route);
                    break;
                case 2:
                    creaCombinazioniDue(route);
                    break;
                case 3:
                    creaCombinazioniTre(route);
                    break;
            }
        }

        System.out.println("\n CREA COMBINAZIONI");
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            System.out.println("TRATTA: " + route.getName());

            for (Iterator<Combination> it2 = route.getCombinazioni().iterator(); it2.hasNext();) {
                Combination combinazione = it2.next();

                for (Iterator<Node> it3 = combinazione.getFermate().iterator(); it3.hasNext();) {
                    Node stop = it3.next();

                    System.out.print("  " + stop.getIndex());

                }
                System.out.println("\n");
            }

        }
    }

    private void creaCombinazioni(Route route) {

        ArrayList<Combination> combinazioni_fermate = new ArrayList<>();

        for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
            Node node_route = it2.next();
            Node partenza = route.getPercorso().getNodes().get(0);
            Node arrivo = route.getPercorso().getNodes().get(route.getPercorso().getNodes().size() - 1);

            ArrayList<Node> stops = null;

            // per tratte fatte solo di due nodi
            if (route.getPercorso().getNodes().size() == 2) {
                stops = new ArrayList<>();

                stops.add(partenza);
                stops.add(arrivo);
            } else if (node_route != partenza && node_route != arrivo) {
                stops = new ArrayList<>();

                stops.add(partenza);
                stops.add(node_route);
                stops.add(arrivo);

            }

            // per evitare ripetizioni
            if (stops != null) {
                if (!Combination.isStops(combinazioni_fermate, stops)) {
                    Combination combination = new Combination();
                    combination.setFermate(stops);
                    combinazioni_fermate.add(combination);
                }
            }

        }
        route.setCombinazioni(combinazioni_fermate);
    }

    private void creaCombinazioniDue(Route route) {
        Node partenza = route.getPercorso().getNodes().get(0);
        Node arrivo = route.getPercorso().getNodes().get(route.getPercorso().getNodes().size() - 1);

        ArrayList<Combination> combinazioni_fermate = new ArrayList<>();

        for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
            Node node_routeUno = it2.next();

            for (Iterator<Node> it3 = route.getPercorso().getNodes().iterator(); it3.hasNext();) {
                Node node_routeDue = it3.next();

                ArrayList<Node> stops = null;

                // per tratte fatte solo di due nodi
                if (route.getPercorso().getNodes().size() == 2) {
                    stops = new ArrayList<>();

                    stops.add(partenza);
                    stops.add(arrivo);
                } else if (node_routeUno != partenza && node_routeUno != arrivo && node_routeDue != partenza && node_routeDue != arrivo && node_routeUno != node_routeDue) {
                    stops = new ArrayList<>();

                    stops.add(partenza);
                    stops.add(node_routeUno);
                    stops.add(node_routeDue);
                    stops.add(arrivo);

                }

                // per evitare ripetizioni
                if (stops != null) {
                    if (!Combination.isStops(combinazioni_fermate, stops)) {
                        Combination combination = new Combination();
                        combination.setFermate(stops);
                        combinazioni_fermate.add(combination);
                    }
                }

            }
            route.setCombinazioni(combinazioni_fermate);
        }
    }

    private void creaCombinazioniTre(Route route) {
        Node partenza = route.getPercorso().getNodes().get(0);
        Node arrivo = route.getPercorso().getNodes().get(route.getPercorso().getNodes().size() - 1);

        ArrayList<Combination> combinazioni_fermate = new ArrayList<>();

        for (Iterator<Node> it2 = route.getPercorso().getNodes().iterator(); it2.hasNext();) {
            Node node_routeUno = it2.next();

            for (Iterator<Node> it3 = route.getPercorso().getNodes().iterator(); it3.hasNext();) {
                Node node_routeDue = it3.next();

                for (Iterator<Node> it4 = route.getPercorso().getNodes().iterator(); it4.hasNext();) {
                    Node node_routeTre = it4.next();

                    ArrayList<Node> stops = null;

                    // per tratte fatte solo di due nodi
                    if (route.getPercorso().getNodes().size() == 2) {
                        stops = new ArrayList<>();

                        stops.add(partenza);
                        stops.add(arrivo);
                    } else if (node_routeUno != partenza && node_routeUno != arrivo && node_routeDue != partenza && node_routeDue != arrivo && node_routeTre != partenza && node_routeTre != arrivo && node_routeUno != node_routeDue && node_routeUno != node_routeTre && node_routeDue != node_routeTre) {
                        stops = new ArrayList<>();

                        stops.add(partenza);
                        stops.add(node_routeUno);
                        stops.add(node_routeDue);
                        stops.add(node_routeTre);
                        stops.add(arrivo);

                    }

                    // per evitare ripetizioni
                    if (stops != null) {
                        if (!Combination.isStops(combinazioni_fermate, stops)) {
                            Combination combination = new Combination();
                            combination.setFermate(stops);
                            combinazioni_fermate.add(combination);
                        }
                    }

                }
                route.setCombinazioni(combinazioni_fermate);
            }
        }
    }

    private void assegnaFermateCombinazioni(HashSet<Route> routes, HashSet<Node> nodes_students) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            for (Iterator<Combination> it2 = route.getCombinazioni().iterator(); it2.hasNext();) {
                Combination combination = it2.next();

                HashMap<Node, Percorso> minPercorsoFermata = new HashMap<>();

                for (Iterator<Node> it3 = nodes_students.iterator(); it3.hasNext();) {
                    Node student = it3.next();

                    if (student.getRoute() == route) {
                        Percorso minPercorso = new Percorso();
                        minPercorso.setDistanza(Double.MAX_VALUE);

                        for (Iterator<Percorso> it4 = student.getPercorsi_dijkstra().iterator(); it4.hasNext();) {
                            Percorso percorso = it4.next();
                            ArrayList<Node> nodes_percorso = percorso.getNodes();
                            Node lastNode = nodes_percorso.get(nodes_percorso.size() - 1);

                            for (Iterator<Node> it5 = combination.getFermate().iterator(); it5.hasNext();) {
                                Node stop = it5.next();

                                if (lastNode == stop) {
                                    if (percorso.getDistanza() <= minPercorso.getDistanza()) {
                                        minPercorso.setNodes(nodes_percorso);
                                        minPercorso.setDistanza(percorso.getDistanza());
                                    }
                                }
                            }

                        }

                        minPercorsoFermata.put(student, minPercorso);
                    }
                }
                combination.setMinPercorsoFermata(minPercorsoFermata);
            }
        }
    }

    private void valutazioneCombinazioni(HashSet<Route> routes) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            for (Iterator<Combination> it2 = route.getCombinazioni().iterator(); it2.hasNext();) {
                Combination combination = it2.next();

                double valutazione = 0;
                double a = 0;
                double b = 0;

                for (Map.Entry<Node, Percorso> entry : combination.getMinPercorsoFermata().entrySet()) {
                    Node student = entry.getKey();
                    Percorso percorso = entry.getValue();

                    a += (double) student.getNum_studenti() * percorso.getDistanza();
                    b += (double) student.getNum_studenti();

                }
                valutazione = a / b;
                combination.setValue(valutazione);
            }
        }

        System.out.println("\n VALUTAZIONE COMBINAZIONI");
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            System.out.println("TRATTA: " + route.getName());

            int i = 1;
            for (Iterator<Combination> it2 = route.getCombinazioni().iterator(); it2.hasNext();) {
                Combination combination = it2.next();

                System.out.println("    COMBINAZIONE: " + i + " v: " + combination.getValue());

                for (Iterator<Node> it3 = combination.getFermate().iterator(); it3.hasNext();) {
                    Node stop = it3.next();

                    System.out.print("      " + stop.getIndex());

                }
                System.out.print("\n");

                for (Map.Entry<Node, Percorso> entry : combination.getMinPercorsoFermata().entrySet()) {
                    Node student = entry.getKey();
                    Percorso percorso = entry.getValue();

                    System.out.println("        studente: " + student.getIndex() + " --> stop: " + percorso.getNodes().get(percorso.getNodes().size() - 1).getIndex());
                }
                System.out.println("-------------------------------------------");
                i++;
            }
            System.out.println("*******************************************");
        }
    }

    private void minValutazioneCombinazione(HashSet<Route> routes) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            Combination minCombination = new Combination();
            minCombination.setValue(Double.MAX_VALUE);

            for (Iterator<Combination> it2 = route.getCombinazioni().iterator(); it2.hasNext();) {
                Combination combination = it2.next();

                if (combination.getValue() <= minCombination.getValue()) {
                    minCombination.setFermate(combination.getFermate());
                    minCombination.setMinPercorsoFermata(combination.getMinPercorsoFermata());
                    minCombination.setValue(combination.getValue());
                }
            }

            route.setMinCombination(minCombination);
        }

        System.out.println("\n COMBINAZIONI MINIME");
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            Combination minCombination = route.getMinCombination();

            System.out.println("TRATTA: " + route.getName());

            System.out.println("    MIN COMBINAZIONE v: " + minCombination.getValue());

            for (Iterator<Node> it3 = minCombination.getFermate().iterator(); it3.hasNext();) {
                Node stop = it3.next();

                System.out.print("      " + stop.getIndex());

            }
            System.out.print("\n");

            for (Map.Entry<Node, Percorso> entry : minCombination.getMinPercorsoFermata().entrySet()) {
                Node student = entry.getKey();
                Percorso percorso = entry.getValue();

                System.out.println("        studente: " + student.getIndex() + " --> stop: " + percorso.getNodes().get(percorso.getNodes().size() - 1).getIndex());
            }
            System.out.println("-------------------------------------------");
        }
    }

    private void assegnaFermateRoute(HashSet<Route> routes) {
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            ArrayList<Node> fermateEffettive = route.getMinCombination().getFermate();

            route.setFermate_effettive(fermateEffettive);
        }
    }

    private void assegnaFermateStudents(HashSet<Route> routes, HashSet<Node> nodes_students) {

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            for (Map.Entry<Node, Percorso> entry : route.getMinCombination().getMinPercorsoFermata().entrySet()) {
                Node student_combination = entry.getKey();
                Percorso minPercorso = entry.getValue();
                Node minStop = minPercorso.getNodes().get(minPercorso.getNodes().size() - 1);

                for (Iterator<Node> it2 = nodes_students.iterator(); it2.hasNext();) {
                    Node student = it2.next();

                    if (student == student_combination) {
                        student.setRealPercorso(minPercorso);
                        student.setRealStop(minStop);
                    }
                }

            }
        }

        System.out.println("\n ASSEGNAZIONE FERMATE A STUDENTI");
        for (Iterator<Node> it1 = nodes_students.iterator(); it1.hasNext();) {
            Node student = it1.next();

            System.out.println("studente: " + student.getIndex() + " fermata: " + student.getRealStop().getIndex());

        }
    }

}
