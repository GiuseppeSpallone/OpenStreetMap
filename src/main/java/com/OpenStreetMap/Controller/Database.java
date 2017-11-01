package com.OpenStreetMap.Controller;

import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Way;
import com.mongodb.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    DB dbStreetMap = null;

    /**
     * Connect database and get collection
     **/
    public DB connectDB(String host, int port, String database) {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        try {
            MongoClient mongo = new MongoClient(host, port);
            DB db = mongo.getDB(database);
            return db;
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean connectUser(DB db, String username, String password){
        boolean connect = false;
        String json = null;
        DBCollection user = db.getCollection("User");

        BasicDBObject query = new BasicDBObject();
        query.put("username", username);
        query.put("password", password);

        DBCursor cursor = user.find(query);

        while (cursor.hasNext()) {
            json += cursor.next();
        }
        if(json != null){
            connect = true;
        }
        return connect;
    }

    public DBCollection getCollection(DB db, String collection) {
        DBCollection dbCollection = db.getCollection(collection);
        return dbCollection;
    }

    /**
     * Insert database
     **/
    public void insertWaysDB(DBCollection collectionWay, HashMap<Long, Way> ways) {
        ways.forEach((key, value) -> {
            BasicDBObject wayObject = new BasicDBObject();

            wayObject.append("id_way", value.getId());

            ArrayList<Node> nodes = value.nd;
            if (nodes != null && nodes.size() > 0) {
                ArrayList<BasicDBObject> nodesObject = new ArrayList<>();

                nodes.forEach((nd) -> {
                    BasicDBObject nodeObject = new BasicDBObject();

                    nodeObject.append("id_node", nd.getId());
                    nodeObject.append("lon_node", nd.getLon());
                    nodeObject.append("lat_node", nd.getLat());

                    nodesObject.add(nodeObject);

                });
                wayObject.append("nd", nodesObject);
            }

            collectionWay.insert(wayObject);
        });
    }

    public void insertNodesDB(DBCollection collectionNode, HashMap<Long, Node> nodes) {
        nodes.forEach((key, value) -> {
            BasicDBObject nodeObject = new BasicDBObject();

            nodeObject.append("id_node", value.getId());
            nodeObject.append("lat", value.getLat());
            nodeObject.append("lon", value.getLon());

            collectionNode.insert(nodeObject);
        });
    }

    public void insertArcsDB(DBCollection collectionArc, HashSet<Arc> arcs) {
        arcs.forEach((value) -> {
            BasicDBObject arcObject = new BasicDBObject();

            arcObject.append("id_node_from", value.getFrom().getId());
            arcObject.append("id_node_to", value.getTo().getId());
            arcObject.append("length", value.getLength());

            collectionArc.insert(arcObject);
        });
    }

    /**
     * Query database
     **/
    public String query_selectAllWays(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public String query_selectAllNodes(DBCollection collectionNode) {
        String jsonString = "";
        DBCursor cursor = collectionNode.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public String query_selectWayById(DBCollection collectionWay, String id_way) {
        String jsonString = "";
        BasicDBObject query = new BasicDBObject();
        query.put("id_way", Long.parseLong(id_way));

        DBCursor cursor = collectionWay.find(query);

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public String query_selectNodeById(DBCollection collectionNode, String id_node) {
        String jsonString = "";
        BasicDBObject query = new BasicDBObject();
        query.put("id_node", Long.parseLong(id_node));


        DBCursor cursor = collectionNode.find(query);

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public String query_selectAllNodesFromWays(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next().get("nd");
        }
        return jsonString;
    }

    /**
     * Create JSON file from query
     **/
    public void createFileJson(String json) {
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("json files (*.json)", "json");

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Scegli cartella destinazione");
        jFileChooser.addChoosableFileFilter(jsonFilter);
        jFileChooser.setFileFilter(jsonFilter);

        File file;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = jFileChooser.getSelectedFile();
                file.createNewFile();
                file.canWrite();
                fileWriter = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
