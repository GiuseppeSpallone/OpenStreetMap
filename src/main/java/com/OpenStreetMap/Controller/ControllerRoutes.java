package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.*;

public class ControllerRoutes {

    private final String RED = "#ff0000";
    private final String YELLOW = "#fffc00";
    private final String GREEN = "#228b22";
    private final String BLUE = "#0100ff";
    private final String ORANGE = "#ffa500";

    Dijkstra dijkstra = new Dijkstra();
    GoogleCoordinate googleCoordinate = new GoogleCoordinate();

    public HashSet<Route> read(String stringAreaText, HashMap<Long, Node> nodes) {
        String arrayString[] = null;
        arrayString = splitta(stringAreaText);

        HashMap<Route, ArrayList<Node>> checkpoints_routes = new HashMap<>();
        ArrayList<Node> nodes_route = null;

        int i = 0;

        while (arrayString[i].equals("%")) {
            nodes_route = new ArrayList<>();
            i++;
            String name = arrayString[i];
            i++;
            int numFermate = Integer.parseInt(arrayString[i]);
            i++;
            String colorRoute = arrayString[i];
            i++;

            Route route = new Route();

            String stringColor = "";
            switch (colorRoute) {
                case "red":
                    stringColor = RED;
                    break;
                case "yellow":
                    stringColor = YELLOW;
                    break;
                case "green":
                    stringColor = GREEN;
                    break;
                case "blue":
                    stringColor = BLUE;
                    break;
                case "orange":
                    stringColor = ORANGE;
                    break;

            }
            Color color = Color.decode(stringColor);
            route.setColor(color);
            route.setName(name);
            route.setNumFermate(numFermate);

            while (arrayString[i].equals("#")) {

                i++;
                float latitudine = Float.parseFloat(arrayString[i]);
                i++;
                float longitudine = Float.parseFloat(arrayString[i]);

                Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);

                System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon());

                nodes_route.add(node);

                if (i < arrayString.length - 1) {
                    i++;
                }

            }

            while (arrayString[i].equals("*")) {

                i++;
                String paese = arrayString[i];

                double[] lat_lon = googleCoordinate.loadGoogleCoordinate(paese);
                float latitudine = (float) lat_lon[0];
                float longitudine = (float) lat_lon[1];
                System.out.println(paese + " coordinate: " + latitudine + " " + longitudine);

                Node node = Node.nodeVicinoByLatLon(nodes, latitudine, longitudine);

                System.out.println("     Nodo lat: " + node.getLat() + " lon: " + node.getLon());

                nodes_route.add(node);

                if (i < arrayString.length - 1) {
                    i++;
                }

            }
            checkpoints_routes.put(route, nodes_route);

        }

        HashSet<Route> routes = applyDijkstra(nodes, checkpoints_routes);
        System.out.print(printRoutes(routes));

        return routes;

    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    private HashSet<Route> applyDijkstra(HashMap<Long, Node> nodes, HashMap<Route, ArrayList<Node>> checkpoint_routes) {
        HashSet<Route> routes = new HashSet<>();

        for (Map.Entry<Route, ArrayList<Node>> entry : checkpoint_routes.entrySet()) {
            Route route = entry.getKey();
            ArrayList<Node> checkpoints = entry.getValue();

            ArrayList<Percorso> percorso = new ArrayList<>();

            //set color
            /*int red = 5 * (int) (Math.random() * 52);
            int green = 5 * (int) (Math.random() * 52);
            int blue = 5 * (int) (Math.random() * 52);
            Color randomColor = new Color(red, green, blue);
            route.setColor(randomColor);*/
            for (int i = 0; i < checkpoints.size(); i++) {
                if (i != checkpoints.size() - 1) {
                    Percorso pezzo = dijkstra.run(checkpoints.get(i), checkpoints.get(i + 1), nodes, false);
                    percorso.add(pezzo);
                }
            }

            //unire i pezzi dei percorsi
            Percorso p = new Percorso();
            ArrayList<Node> nodi_p = new ArrayList<>();
            double d = 0;
            for (Iterator<Percorso> it = percorso.iterator(); it.hasNext();) {
                Percorso p_ = it.next();

                d += p_.getDistanza();

                for (Iterator<Node> it1 = p_.getNodes().iterator(); it1.hasNext();) {
                    Node n_ = it1.next();

                    nodi_p.add(n_);

                }
            }
            p.setNodes(nodi_p);
            p.setDistanza(d);

            route.setPercorso(p);
            route.setDistanza(p.getDistanza());
            routes.add(route);
        }

        return routes;
    }

    public String printRoutes(HashSet<Route> routes) {
        String output_routes = "";
        output_routes += "NUMERO TRATTE " + routes.size() + "\n";
        output_routes += "________________________" + "\n";

        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();

            output_routes += "\nTratta: " + route.getName() + "\n";
            output_routes += "numero fermate: " + route.getNumFermate() + "\n";
            output_routes += "distanza: " + route.getDistanza() + "\n";

            for (Iterator<Node> it1 = route.getPercorso().getNodes().iterator(); it1.hasNext();) {
                Node node = it1.next();

                output_routes += "id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon() + "\n";
            }
        }
        return output_routes;
    }

}
