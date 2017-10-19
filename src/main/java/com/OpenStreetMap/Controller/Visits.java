package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import sun.misc.Queue;

import java.util.*;

public class Visits {

    public void visita(HashMap<Long, Node> nodes, Node startingNode) {

        reset(nodes);

        //visita in profondità
        visitaFrom(startingNode);
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

    private void reset(HashMap<Long, Node> nodes) {

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(0);
        }
    }

}
