package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import sun.misc.Queue;

import java.util.*;

public class Visit {
    ArrayList<Node> nodes = new ArrayList<>();

    public ArrayList<Node> visita(HashMap<Long, Node> nodes, Node startingNode) {

        reset(nodes);

        //visita in profondità
        ArrayList<Node> visit_nodes = visitaFrom(startingNode);
        System.out.print(printVisit(visit_nodes));

        return visit_nodes;
    }

    private ArrayList<Node> visitaFrom(Node node) {
        node.setMark(1);
        nodes.add(node);

        for (Iterator<Arc> it = node.getNd_arcs().iterator(); it.hasNext(); ) {
            Arc arc = it.next();

            arc.setMark(1);

            if (arc.getFrom() == node) {
                if (arc.getTo().getMark() == 0) {
                    visitaFrom(arc.getTo());
                }
            }
        }

        return nodes;
    }

    private void reset(HashMap<Long, Node> nodes) {

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(0);

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();
                arc.setMark(0);
            }
        }
    }

    public String printVisit(ArrayList<Node> nodes) {
        String output_visit = "";
        output_visit += "VISITA -->" + "\n";

        for (Iterator<Node> it = nodes.iterator(); it.hasNext(); ) {
            Node node = it.next();

            output_visit += "        id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon() + "\n";
        }
        return output_visit;
    }

}
