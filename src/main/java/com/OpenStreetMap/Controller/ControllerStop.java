package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ControllerStop {
    
    public double stop(int numFermate, HashSet<Route> routes, HashSet<Node> nodes_students) {
        
        ArrayList<ArrayList<Node>> stops = new ArrayList<>();
        
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            Percorso percorso_route = route.getPercorso();
            ArrayList<Node> nodes_route = percorso_route.getNodes();
            
            for (Iterator<Node> it2 = nodes_route.iterator(); it2.hasNext();) {
                Node node_route = it2.next();
                
                for (int i = 0; i < numFermate; i++) {
                    ArrayList<Node> fermate = new ArrayList<>();
                    fermate.add(node_route);
                    stops.add(fermate);
                }
                
            }
            
        }
        return 0;
    }
    
}
