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

        //reset distanza e predecessore
        reset(nodes);

        //PriorityQueue<Node> queue = new PriorityQueue<>();
        ArrayList<Node> queue = new ArrayList<>();

        ArrayList<Double> distanzaPercorso = new ArrayList<>();
        ArrayList<Node> percorso = new ArrayList<>();

        sorgente.setDistanza(5);
        sorgente.setPredecessore(null);
        queue.add(sorgente);

        Node node = queue.get(0);

        while (node != destinazione) {
            double min = queue.get(0).getDistanza();
            for (Node i : queue) {
                if (i.getDistanza() < min) {
                    node = i;
                }
            }

            for (Iterator<Arc> it1 = node.getNd_arcs().iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();

                Node from = arc.getFrom();
                Node to = arc.getTo();

                if (to.getDistanza() == Double.MAX_VALUE) {
                    to.setDistanza(from.getDistanza() + arc.getLength());
                    to.setPredecessore(from);
                    queue.add(to);

                    percorso.add(from);
                    distanzaPercorso.add(from.getDistanza());

                } else {
                    if (to.getDistanza() > from.getDistanza() + arc.getLength()) {
                        queue.remove(to);
                        percorso.remove(to);
                        distanzaPercorso.remove(to.getDistanza());

                        to.setDistanza(from.getDistanza() + arc.getLength());
                        to.setPredecessore(from);
                        queue.add(to);

                        percorso.add(from);
                        distanzaPercorso.add(to.getDistanza());


                    }
                }

            }
        }

        System.out.println("ciaoooo");
        for (Iterator<Double> it = distanzaPercorso.iterator(); it.hasNext(); ) {
            double distanza = it.next();
            System.out.println("distanza" + distanza);
        }

        for (Iterator<Node> it = percorso.iterator(); it.hasNext(); ) {
            Node nodo = it.next();
            System.out.println("percorso --> index: " + nodo.getIndex() + "; id: " + nodo.getId());
        }
    }


}

