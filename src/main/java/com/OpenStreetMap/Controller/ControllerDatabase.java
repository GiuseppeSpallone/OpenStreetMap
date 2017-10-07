package com.OpenStreetMap.Controller;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerDatabase {
    /** Connect database and get collection **/
    public static DB connectDB(String host, int port, String database) {
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

    public static DBCollection getCollection(DB db, String collection) {
        DBCollection dbCollection = db.getCollection(collection);
        return dbCollection;
    }

    /** Insert database **/
    public static void insertWaysDB(DBCollection collectionWay, HashMap<Long, Way> ways) {
        ways.forEach((key, value) -> {
            BasicDBObject wayObject = new BasicDBObject();

            wayObject.append("id_way", value.getId());

            ArrayList<Node> nodi = value.getNd();
            if (nodi != null && nodi.size() > 0) {
                ArrayList<BasicDBObject> nodesObject = new ArrayList<>();

                nodi.forEach((nd) -> {
                    BasicDBObject nodeObject = new BasicDBObject();

                    nodeObject.append("id_node", nd.getId());
                    nodeObject.append("lon_node", nd.getLon());
                    nodeObject.append("lat_node", nd.getLat());

                    nodesObject.add(nodeObject);

                });
                wayObject.append("nd", nodesObject);
            }

            ArrayList<String[]> tags = value.getTag();
            if (tags != null && tags.size() > 0) {
                ArrayList<BasicDBObject> tagsObject = new ArrayList<>();

                tags.forEach((tag) -> {
                    BasicDBObject tagObject = new BasicDBObject();

                    tagObject.append("k", tag[0]);
                    tagObject.append("v", tag[1]);

                    tagsObject.add(tagObject);
                });
                wayObject.append("tag", tagsObject);
            }
            collectionWay.insert(wayObject);
        });
    }

    /** Query database **/
    public static String query_selectAllWays(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public static String query_selectWayById(DBCollection collectionWay, String id_way) {
        String jsonString = "";
        BasicDBObject query = new BasicDBObject();
        query.put("id_way", Long.parseLong(id_way));

        DBCursor cursor = collectionWay.find(query);

        while (cursor.hasNext()) {
            jsonString += cursor.next();
        }
        return jsonString;
    }

    public static String query_selectAllNodes(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next().get("nd");
        }
        return jsonString;
    }

    public static String query_selectNodesById(DBCollection collectionWay, String id_node) {
        return null;
    }

    /** Create JSON file from query **/
    public static void createFileJson(String json) {
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
