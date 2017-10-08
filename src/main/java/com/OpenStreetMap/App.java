package com.OpenStreetMap;

import com.OpenStreetMap.Controller.ControllerDatabase;
import com.OpenStreetMap.Controller.ControllerImport;
import com.OpenStreetMap.Model.Way;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import java.io.File;
import java.util.HashMap;

public class App {

    public static void main(String[] args) {
        ControllerImport controllerImport = new ControllerImport();
        ControllerDatabase controllerDatabase = new ControllerDatabase();

        File file = controllerImport.openFile();
        HashMap<Long, Way> ways = controllerImport.createWays(file);

        DB dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");
        controllerDatabase.insertWaysDB(collectionWay, ways);

        String query1 = controllerDatabase.query_selectAllWays(collectionWay);
        //String query2 = controllerDatabase.query_selectWayById(collectionWay, "265529297");
        //String query3 = controllerDatabase.query_selectAllNodes(collectionWay);
        //String query4 = controllerDatabase.query_selectNodesById(collectionWay, "265529297");

        controllerDatabase.createFileJson(query1);

    }


}
