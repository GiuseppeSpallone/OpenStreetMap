package com.OpenStreetMap;

import com.OpenStreetMap.Controller.ControllerDatabase;
import com.OpenStreetMap.Controller.ControllerImport;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Way;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class App {

    public static void main(String[] args) {
        ControllerImport controllerImport = new ControllerImport();
        ControllerDatabase controllerDatabase = new ControllerDatabase();

        File file = controllerImport.openFile();
        //HashMap<Long, Way> ways = controllerImport.createWays(file);
        HashMap<Long, Node> nodes = controllerImport.createNodes(file);

        DB dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        //DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");
        DBCollection collectionNode = controllerDatabase.getCollection(dbStreetMap, "Node");
        //controllerDatabase.insertWaysDB(collectionWay, ways);
        controllerDatabase.insertNodesDB(collectionNode, nodes);

        //String query1 = controllerDatabase.query_selectAllWays(collectionWay);
        //String query2 = controllerDatabase.query_selectWayById(collectionWay, "265529297");
        //String query3 = controllerDatabase.query_selectAllNodesFromWays(collectionWay);
        //String query4 = controllerDatabase.query_selectNodeById(collectionWay, "2711797568");
        String query5 = controllerDatabase.query_selectAllNodes(collectionNode);

        controllerDatabase.createFileJson(query5);

        //simulation(900000000, 900000000);


    }

    public static void simulation(int numNodes, int numWay) {
        System.out.println("Start simulation...");

        long startTime = System.nanoTime();

        ControllerImport controllerImport = new ControllerImport();
        ControllerDatabase controllerDatabase = new ControllerDatabase();

        HashMap<Long, Way> ways_simulate = controllerImport.simulateWays(numNodes, numWay);

        DB dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way Simulate");
        controllerDatabase.insertWaysDB(collectionWay, ways_simulate);

        long endTime = System.nanoTime();
        double duration = (double) (endTime - startTime) / 1000000000;
        System.out.println("Timer simulation: " + duration);
    }


}
