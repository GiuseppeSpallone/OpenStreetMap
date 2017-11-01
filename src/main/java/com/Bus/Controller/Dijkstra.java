package com.Bus.Controller;

import com.Bus.Model.Arc;
import com.Bus.Model.Node;

import java.util.*;

public class Dijkstra {

    public ArrayList<Node> run(Node sorgente, Node destinazione, HashMap<Long, Node> nodes, boolean mark) {
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

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
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
        ArrayList<Node> percorso = setPredecessorePercorso(sorgente, destinazione);

        if (mark)
            setMarkPercorso(percorso);

        System.out.print(printPercorso(percorso));

        return percorso;
    }

    private void reset(HashMap<Long, Node> nodes) {

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setDistanza(Double.MAX_VALUE);
            node.setPredecessore(null);
            node.setMark(-1);

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();
                arc.setMark(0);
            }
        }
    }

    private ArrayList<Node> setPredecessorePercorso(Node sorgente, Node destinazione) {
        ArrayList<Node> percorso = new ArrayList<>();

        percorso.add(destinazione);

        Node nd = destinazione;

        while (nd != sorgente) {
            nd = nd.getPredecessore();
            percorso.add(nd);
        }
        Collections.reverse(percorso);

        return percorso;
    }

    private void setMarkPercorso(ArrayList<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setMark(1);

            if (i != nodes.size() - 1) {
                Arc arc = Arc.arcByFromTo(nodes.get(i), nodes.get(i + 1));
                arc.setMark(1);
            }
        }
    }

    public String printPercorso(ArrayList<Node> percorso) {
        String output_dijkstra = "";
        output_dijkstra += "DISTANZA: " + percorso.get(percorso.size() - 1).getDistanza() + "\n";

        for (Iterator<Node> it = percorso.iterator(); it.hasNext(); ) {
            Node nd = it.next();
            output_dijkstra += "id: " + nd.getId() + "; index: " + nd.getIndex() + "; distanza: " + nd.getDistanza() + "\n";
        }

        return output_dijkstra;
    }


}

