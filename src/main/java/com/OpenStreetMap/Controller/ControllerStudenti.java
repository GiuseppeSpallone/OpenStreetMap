package com.OpenStreetMap.Controller;


import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ControllerStudenti {

    public HashSet<Node> read(String stringAreaText, HashMap<Long, Node> nodes, HashSet<Route> routes) {
        reset(nodes);

        String arrayString[] = null;
        arrayString = splitta(stringAreaText);

        HashSet<Node> nodes_students = new HashSet<>();

        int i = 0;
        while (arrayString[i].equals("#")) {

            i++;
            String name_route = arrayString[i];
            i++;
            float latitudine = Float.parseFloat(arrayString[i]);
            i++;
            float longitudine = Float.parseFloat(arrayString[i]);
            i++;
            int num_studenti = Integer.parseInt(arrayString[i]);

            Route route = Route.getRouteByName(routes, name_route);

            Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);
            node.setRoute(route);
            node.setNum_studenti(num_studenti);

            System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon() + " tratta: " + node.getRoute().getName() + " num: " + node.getNum_studenti());

            nodes_students.add(node);

            if (i < arrayString.length - 1)
                i++;

        }
        return nodes_students;

    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    private void reset(HashMap<Long, Node> nodes) {
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setNum_studenti(0);
        }
    }

}
