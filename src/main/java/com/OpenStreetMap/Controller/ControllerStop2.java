package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Combination;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Percorso;
import com.OpenStreetMap.Model.Route;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ControllerStop2 {
    
    public void run(HashSet<Node> nodes_students, HashSet<Route> routes) {
        
        for (Iterator<Route> it = routes.iterator(); it.hasNext();) {
            Route route = it.next();
            pMedian(route);
        }
    }
    
    private static void pMedian(Route route) {
        ArrayList<Node> nodes_route = route.getPercorso().getNodes();
        ArrayList<Node> nodes_students = route.students;
        int numFermate = route.getNumFermate() + 2;
        
        try {
            
            IloCplex model = new IloCplex();
            
            IloNumVar[] x = model.boolVarArray(nodes_route.size());
            IloNumVar[][] y = new IloNumVar[nodes_students.size()][];
            
            for (int i = 0; i < nodes_students.size(); i++) {
                
                y[i] = model.boolVarArray(nodes_route.size());
            }
            
            IloLinearNumExpr function = model.linearNumExpr();
            
            int numS = 0;
            for (int i = 0; i < nodes_students.size(); i++) {
                numS += nodes_students.get(i).getNum_studenti();
                
                for (int j = 0; j < nodes_route.size(); j++) {
                    function.addTerm(y[i][j], nodes_students.get(i).getPercorsi_dijkstra().get(j).getDistanza() * nodes_students.get(i).getNum_studenti());
                }
            }
            model.addMinimize(function);
            
            model.addEq(x[0], 1);
            model.addEq(x[x.length - 1], 1);
            
            IloLinearNumExpr v = model.linearNumExpr();
            for (int i = 0; i < nodes_route.size(); i++) {
                v.addTerm(x[i], 1);
            }
            model.addEq(v, numFermate);
            
            for (int i = 0; i < nodes_students.size(); i++) {
                IloLinearNumExpr v1 = model.linearNumExpr();
                
                for (int j = 0; j < nodes_route.size(); j++) {
                    v1.addTerm(y[i][j], 1);
                }
                model.addEq(v1, 1);
            }
            
            for (int i = 0; i < nodes_students.size(); i++) {
                
                for (int j = 0; j < nodes_route.size(); j++) {
                    model.addLe(y[i][j], x[j]);
                }
            }
            
            if (model.solve()) {
                System.out.println("Solution status: " + model.getStatus());
                System.out.println("Solution value: " + (model.getObjValue() / numS));
                
                Combination minCombination = new Combination();
                minCombination.setValue(model.getObjValue() / numS);
                route.setMinCombination(minCombination);
                
                System.out.println("_______________");
                System.out.print("Fermate:");
                
                ArrayList<Node> fermate_effettive = new ArrayList<>();
                for (int i = 0; i < nodes_route.size(); i++) {
                    if (model.getValue(x[i]) > 0.5) {
                        fermate_effettive.add(nodes_route.get(i)); //set fermate della rotta
                        
                        System.out.print(" " + nodes_route.get(i).getIndex());
                    }
                }
                route.setFermate_effettive(fermate_effettive);
                System.out.println("");
                System.out.println("_______________");
                
                for (int i = 0; i < nodes_students.size(); i++) {
                    System.out.print("nodo: " + nodes_students.get(i).getIndex());
                    for (int j = 0; j < nodes_route.size(); j++) {
                        if (model.getValue(y[i][j]) > 0.5) {
                            nodes_students.get(i).setRealStop(nodes_route.get(j)); //set fermata per studente
                            
                            Percorso realPercorso = new Percorso();
                            realPercorso.setNodes(nodes_students.get(i).getPercorsi_dijkstra().get(j).getNodes());
                            realPercorso.setDistanza(nodes_students.get(i).getPercorsi_dijkstra().get(j).getDistanza());
                            nodes_students.get(i).setRealPercorso(realPercorso); //set percorso verso fermata
                            
                            System.out.print("      fermata: " + nodes_route.get(j).getIndex());
                            System.out.println("");
                        }
                    }
                }
                
            } else {
                System.out.println("Solution status: " + model.getStatus());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
