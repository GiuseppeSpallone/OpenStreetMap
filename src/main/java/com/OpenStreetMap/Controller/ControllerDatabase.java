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

            ArrayList<Node> nodes = value.getNd();
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

            ArrayList<Node> nodes_approximate = value.getNd_approximate();
            if (nodes_approximate != null && nodes_approximate.size() > 0) {
                ArrayList<BasicDBObject> nodes_approximateObject = new ArrayList<>();

                nodes_approximate.forEach((nd) -> {
                    BasicDBObject nodeObject = new BasicDBObject();

                    nodeObject.append("id_node", nd.getId());
                    nodeObject.append("lon_node", nd.getLon());
                    nodeObject.append("lat_node", nd.getLat());

                    nodes_approximateObject.add(nodeObject);

                });
                wayObject.append("nd_approximate", nodes_approximateObject);
            }

            wayObject.append("bicycle", value.isBicycle());
            wayObject.append("foot", value.isFoot());
            wayObject.append("electrified", value.isElectrified());
            wayObject.append("highway", value.getHighway());
            wayObject.append("lanes", value.getLanes());
            wayObject.append("maxspeed", value.getMaxspeed());
            wayObject.append("name", value.getName());
            wayObject.append("oneway", value.isOneway());
            wayObject.append("bridge", value.isBridge());
            wayObject.append("layer", value.getLayer());
            wayObject.append("tunnel", value.isTunnel());
            wayObject.append("railway", value.getRailway());

            collectionWay.insert(wayObject);
        });
    }

    public void insertNodesDB(DBCollection collectionWay, HashMap<Long, Node> nodes) {
        nodes.forEach((key, value) -> {
            BasicDBObject nodeObject = new BasicDBObject();

            nodeObject.append("id_node", value.getId());
            nodeObject.append("lat", value.getLat());
            nodeObject.append("lon", value.getLon());
            nodeObject.append("highway", value.getHighway());
            nodeObject.append("traffic_calming", value.getTraffic_calming());
            nodeObject.append("addr_city", value.getAddr_city());
            nodeObject.append("addr_country", value.getAddr_country());
            nodeObject.append("addr_housenumber", value.getAddr_housenumber());
            nodeObject.append("postcode", value.getPostcode());
            nodeObject.append("addr_street", value.getAddr_street());
            nodeObject.append("amenity", value.getAmenity());
            nodeObject.append("name", value.getName());
            nodeObject.append("phone", value.getPhone());
            nodeObject.append("atm", value.isAtm());
            nodeObject.append("tourism", value.getTourism());
            nodeObject.append("fee", value.isFee());
            nodeObject.append("dispensing", value.isDispensing());
            nodeObject.append("building", value.getBuilding());
            nodeObject.append("parking", value.getParking());
            nodeObject.append("access", value.isAccess());
            nodeObject.append("barrier", value.getBarrier());
            nodeObject.append("bicycle", value.isBicycle());
            nodeObject.append("foot", value.isFoot());
            nodeObject.append("shop", value.getShop());
            nodeObject.append("natural", value.getNatural());
            nodeObject.append("office", value.getOffice());
            nodeObject.append("bus", value.isBus());
            nodeObject.append("public_transport", value.getPublic_transport());
            nodeObject.append("leisure", value.getLeisure());
            nodeObject.append("vehicle", value.isVehicle());
            nodeObject.append("aeroway", value.getAeroway());
            nodeObject.append("entrance", value.isEntrance());
            nodeObject.append("wheelchair", value.isWheelchair());
            nodeObject.append("man_made", value.getMan_made());

            collectionWay.insert(nodeObject);
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

    public String query_selectAllNodes(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

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

    public String query_selectAllNodesFromWays(DBCollection collectionWay) {
        String jsonString = "";
        DBCursor cursor = collectionWay.find();

        while (cursor.hasNext()) {
            jsonString += cursor.next().get("nd");
        }
        return jsonString;
    }

    public String query_selectNodeById(DBCollection collectionWay, String id_node) {
        String jsonString = "";
        BasicDBObject query = new BasicDBObject();
        query.put("nd.1.id_node", Long.parseLong(id_node));


        DBCursor cursor = collectionWay.find(query);

        while (cursor.hasNext()) {
            jsonString += cursor.next();
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
