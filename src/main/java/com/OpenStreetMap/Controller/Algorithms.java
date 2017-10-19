package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;

import java.util.*;

public class Algorithms {


    public void dijkstra(Node sorgente, Node destinazione, HashMap<Long, Node> nodes) {
        System.out.println("DIJKSTRA --> ");
        System.out.println("             SORGENTE index: " + sorgente.getIndex() + "; id: " + sorgente.getId() + "; coordinate: " + sorgente.getLat() + "," + sorgente.getLon());
        System.out.println("             DESTINAZIONE index: " + destinazione.getIndex() + "; id: " + destinazione.getId() + "; coordinate: " + destinazione.getLat() + "," + destinazione.getLon());

        //reset distanza e predecessore
        reset(nodes);

        PriorityQueue<Node> queue = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return (int) (n1.getDistanza() - n2.getDistanza());
            }
        });

        sorgente.setDistanza(0);
        sorgente.setPredecessore(null);
        queue.add(sorgente); //inserisco sorgente nella coda

        Node node = sorgente;

        while (node != destinazione) {
            node = queue.poll(); //estraggo nodo con distanza minore

            for (Iterator<Arc> it1 = node.getNd_arcs().iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();

                Node from = arc.getFrom();
                Node to = arc.getTo();

                if (to.getDistanza() == Double.MAX_VALUE) {
                    to.setDistanza(from.getDistanza() + arc.getLength());
                    to.setPredecessore(from);

                    queue.add(to);

                } else {
                    if (to.getDistanza() > from.getDistanza() + arc.getLength()) {
                        queue.remove(to);

                        to.setDistanza(from.getDistanza() + arc.getLength());
                        to.setPredecessore(from);

                        queue.add(to);
                    }
                }

            }
        }


        ArrayList<Node> percorso = percorso(sorgente, destinazione);
        printPercorso(percorso);

    }

    private void reset(HashMap<Long, Node> nodes) {

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setDistanza(Double.MAX_VALUE);
            node.setPredecessore(null);
            node.setMark(0);
        }
    }

    private ArrayList<Node> percorso(Node sorgente, Node destinazione) {
        ArrayList<Node> percorso = new ArrayList<>();

        destinazione.setMark(1);
        percorso.add(destinazione);

        Node nd = destinazione.getPredecessore();
        while (nd != sorgente) {
            nd = nd.getPredecessore();
            nd.setMark(1);
            percorso.add(nd);
        }
        sorgente.setMark(1);
        percorso.add(sorgente);

        return percorso;
    }

    private void printPercorso(ArrayList<Node> percorso) {
        System.out.println("             DISTANZA --> " + percorso.get(0).getDistanza());

        System.out.println("             PERCORSO --> ");
        for (Iterator<Node> it = percorso.iterator(); it.hasNext(); ) {
            Node nd = it.next();
            System.out.println("                            id: " + nd.getId() + "; index: " + nd.getIndex());
        }
    }

}

