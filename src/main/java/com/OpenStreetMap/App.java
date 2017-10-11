package com.OpenStreetMap;

import com.OpenStreetMap.Controller.ControllerDatabase;
import com.OpenStreetMap.Controller.ControllerImport;
import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Way;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class App {

    public static void main(String[] args) {
        ControllerImport controllerImport = new ControllerImport();
        ControllerDatabase controllerDatabase = new ControllerDatabase();

        //HashMap<Long, Way> ways = controllerImport.ways;
        //HashMap<Long, Node> nodes = controllerImport.nodes;
        //HashSet<Arc> arcs = controllerImport.arcs;

        //DB dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        //DBCollection collectionNode = controllerDatabase.getCollection(dbStreetMap, "Node");
        //DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");
        //DBCollection collectionArc = controllerDatabase.getCollection(dbStreetMap, "Arc");

        //controllerDatabase.insertNodesDB(collectionNode, nodes);
        //controllerDatabase.insertWaysDB(collectionWay, ways);
        //controllerDatabase.insertArcsDB(collectionArc, arcs);

        //String query1 = controllerDatabase.query_selectAllWays(collectionWay);
        //String query2 = controllerDatabase.query_selectWayById(collectionWay, "265529297");
        //String query3 = controllerDatabase.query_selectAllNodesFromWays(collectionWay);
        //String query4 = controllerDatabase.query_selectNodeById(collectionNode, "2711797568");
        ///String query5 = controllerDatabase.query_selectAllNodes(collectionNode);
        //controllerDatabase.createFileJson(query5);
    }
}
