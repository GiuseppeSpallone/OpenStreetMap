package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;

import java.util.*;

public class Algorithms {

    private void reset(HashMap<Long, Node> nodes) {

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setDistanza(Double.MAX_VALUE);
            node.setPredecessore(null);
            node.setMark(0);
        }
    }


    public void dijkstra(Node sorgente, Node destinazione, HashMap<Long, Node> nodes) {
        System.out.println("DIJKSTRA --> ");
        System.out.println("             SORGENTE id: " + sorgente.getId() + "; index: " + sorgente.getIndex() + "; coordinate: " + sorgente.getLat() + "," + sorgente.getLon());
        System.out.println("             DESTINAZIONE id: " + destinazione.getId() + "; index: " + destinazione.getIndex() + "; coordinate: " + destinazione.getLat() + "," + destinazione.getLon());

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

        System.out.println("             DISTANZA --> " + destinazione.getDistanza());
        percorso(sorgente, destinazione);

    }

    public void percorso(Node sorgente, Node destinazione) {
        System.out.println("             PERCORSO --> ");

        System.out.println("                            id: " + destinazione.getId() + "; index: " + destinazione.getIndex());
        destinazione.setMark(1);

        Node nd = destinazione.getPredecessore();


        while (nd != sorgente) {
            System.out.println("                            id: " + nd.getId() + "; index: " + nd.getIndex());
            nd = nd.getPredecessore();
            nd.setMark(1);
        }

        System.out.println("                            id: " + sorgente.getId() + "; index: " + sorgente.getIndex());
        sorgente.setMark(1);
    }

}

