package com.OpenStreetMap.Model;

import sun.misc.Queue;

import java.util.*;

public class Visite {

    public void visita(HashMap<Long, Node> nodes, Node startingNode) {

        int marked = 0;
        //HashSet<Long> queue = new HashSet<>(nodes.size());
        //queue.add(startingNode.getId());

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(0);
        }

        //visita in profondità
        visitaFrom(startingNode);


        for (Iterator<Node> it1 = nodes.values().iterator(); it1.hasNext(); ) {
            Node node = it1.next();
            if (node.getMark() == 1) {
                marked++;
            }
        }
        System.out.println("VISIT --> FROM: " + startingNode.getId());
        System.out.println("     NODES: " + nodes.size() + "; MARKED: " + marked);
    }

    private void visitaFrom(Node node) {
        node.setMark(1);

        for (Iterator<Arc> it = node.getNd_arcs().iterator(); it.hasNext(); ) {
            Arc arc = it.next();

            if (arc.getFrom() == node) {
                if (arc.getTo().getMark() == 0) {
                    visitaFrom(arc.getTo());
                }
            }
        }
    }

}
