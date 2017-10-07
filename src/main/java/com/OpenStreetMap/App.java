package com.OpenStreetMap;

import com.OpenStreetMap.Controller.ControllerDatabase;
import com.OpenStreetMap.Controller.ControllerImport;
import com.mongodb.*;

public class App {

    public static void main(String[] args) {
        ControllerDatabase controllerDatabase = new ControllerDatabase();
        ControllerImport controllerImport = new ControllerImport();

        DB dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");

        //String pathFile = controllerImport.openFile();
        //HashMap<Long, Way> ways = controllerImport.createWays(pathFile);
        //controllerImport.insertDB(db, ways);

        String query1 = controllerDatabase.query_selectAllWays(collectionWay);
        String query2 = controllerDatabase.query_selectWayById(collectionWay, "265529297");
        String query3 = controllerDatabase.query_selectAllNodes(collectionWay);
        String query4 = controllerDatabase.query_selectNodesById(collectionWay, "265529297");

        controllerDatabase.createFileJson(query1);

    }



}
