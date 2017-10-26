/*
 * Created by JFormDesigner on Wed Oct 11 17:43:14 CEST 2017
 */

package com.OpenStreetMap.View;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.OpenStreetMap.Controller.*;
import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.OpenStreetMap.Model.Route;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class Show extends JFrame {
    ControllerImport controllerImport = new ControllerImport();
    ControllerExport controllerExport = new ControllerExport();
    ControllerDatabase controllerDatabase = new ControllerDatabase();
    ControllerFileMap controllerFileMap = new ControllerFileMap();
    ControllerRoute controllerRoute = new ControllerRoute();
    Visits visits = new Visits();
    Dijkstra dijkstra = new Dijkstra();

    private DB dbStreetMap = null;
    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;
    private HashMap<Long, Node> nodes_paint = null;
    private HashSet<Arc> arcs_paint = null;
    HashMap<Long, Route> routes = null;

    private File file = null;

    public Show() {
        initComponents();
    }

    private void menuItem2ActionPerformed(ActionEvent e) {
        dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        if (dbStreetMap != null) {
            JOptionPane.showMessageDialog(null, "Connesso al DB");
            menuItem2.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(null, "Connessione al DB non riuscita");
        }
    }

    private void menuItem1ActionPerformed(ActionEvent e) {
        DBCollection collectionNode = controllerDatabase.getCollection(dbStreetMap, "Node");
        DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");
        DBCollection collectionArc = controllerDatabase.getCollection(dbStreetMap, "Arc");

        controllerDatabase.insertNodesDB(collectionNode, controllerImport.getNodes());
        controllerDatabase.insertWaysDB(collectionWay, controllerImport.getWays());
        controllerDatabase.insertArcsDB(collectionArc, controllerImport.getArcs());

        JOptionPane.showMessageDialog(null, "Elementi inseriti");
    }

    private void menuItem3ActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void menuItem4ActionPerformed(ActionEvent e) {
        file = openFile();

        if (file != null) {
            controllerImport.create(file, 5, 2, 100, true, false);
            nodes = controllerImport.getNodes();
            arcs = controllerImport.getArcs();

            if (!nodes.isEmpty() || !arcs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Mappa creata nodi: " + nodes.size() + " archi: " + arcs.size());

                menuItem1.setEnabled(true);

                file = selectPath();
                if (esportaMap(file)) {
                    Object[] options = {"Si", "No"};
                    int option = JOptionPane.showOptionDialog(null, "Mappa esportata, disegnare mappa?", null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (option == 0) {
                        disegnaMap(file);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Mappa non esportata");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Mappa non creata");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Mappa non caricata");
        }
    }

    private void menuItem7ActionPerformed(ActionEvent e) {
        file = openFile();
        disegnaMap(file);
    }

    private void panel1MouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        Node n = getNodoVicinoByXY(x, y);
        textArea2.append("# " + n.getLat() + " " + n.getLon() + "\n");
        //JOptionPane.showMessageDialog(null, "index: " + n.getIndex() + "; id: " + n.getId() + "; coordinate: " + n.getLat() + "," + n.getLon());
        System.out.println("index: " + n.getIndex() + "; id: " + n.getId() + "; " + n.getLat() + "," + n.getLon());
    }

    private void menuItem10ActionPerformed(ActionEvent e) {
        JPanel panelVisit = new JPanel();
        JLabel jLabel_lat = new JLabel("Latitudine");
        JTextField jTextField_lat = new JTextField(7);
        JLabel jLabel_lon = new JLabel("Longitudine");
        JTextField jTextField_lon = new JTextField(7);

        panelVisit.add(jLabel_lat);
        panelVisit.add(jTextField_lat);
        panelVisit.add(jLabel_lon);
        panelVisit.add(jTextField_lon);

        Object[] options = {"Visita", "Random", "Map"};
        int option = JOptionPane.showOptionDialog(null, panelVisit, null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        Node startingNode = null;

        if (option == 0) {
            float lat = Float.parseFloat(jTextField_lat.getText());
            float lon = Float.parseFloat(jTextField_lon.getText());

            startingNode = Node.nodeByLatLon(nodes, lat, lon);
        }

        if (option == 1) {
            startingNode = Node.randomNode(nodes);
        }

        if (option == 2) {
            //
        }

        visits.visita(nodes, startingNode);
        panel1.repaint();
    }

    private void menuItem9ActionPerformed(ActionEvent e) {
        JPanel panelDijkstra = new JPanel(new GridLayout(2, 4));
        JLabel jLabel_lat_s = new JLabel("Lat sorgente");
        JTextField jTextField_lat_s = new JTextField(7);
        JLabel jLabel_lon_s = new JLabel("Lon sorgente");
        JTextField jTextField_lon_s = new JTextField(7);
        JLabel jLabel_lat_d = new JLabel("Lat destinazione");
        JTextField jTextField_lat_d = new JTextField(7);
        JLabel jLabel_lon_d = new JLabel("Lon destinazione");
        JTextField jTextField_lon_d = new JTextField(7);

        panelDijkstra.add(jLabel_lat_s);
        panelDijkstra.add(jTextField_lat_s);
        panelDijkstra.add(jLabel_lon_s);
        panelDijkstra.add(jTextField_lon_s);
        panelDijkstra.add(jLabel_lat_d);
        panelDijkstra.add(jTextField_lat_d);
        panelDijkstra.add(jLabel_lon_d);
        panelDijkstra.add(jTextField_lon_d);

        Object[] options = {"Percorso", "Random", "Map"};
        int option = JOptionPane.showOptionDialog(null, panelDijkstra, null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        Node sorgente = null;
        Node destinazione = null;

        if (option == 0) {
            float lat_s = Float.parseFloat(jTextField_lat_s.getText());
            float lon_s = Float.parseFloat(jTextField_lon_s.getText());
            float lat_d = Float.parseFloat(jTextField_lat_d.getText());
            float lon_d = Float.parseFloat(jTextField_lon_d.getText());

            sorgente = Node.nodeByLatLon(nodes, lat_s, lon_s);
            destinazione = Node.nodeByLatLon(nodes, lat_d, lon_d);
        }
        if (option == 1) {
            sorgente = Node.randomNode(nodes);
            destinazione = Node.randomNode(nodes);
        }
        if (option == 2) {
            //
        }

        dijkstra.run(sorgente, destinazione, nodes);
        panel1.repaint();
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
        textArea2.removeAll();

        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(-1);

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();
                arc.setMark(0);
            }
        }
        panel1.repaint();
    }

    private void menuItem8ActionPerformed(ActionEvent e) {
        nodes = null;
        arcs = null;
        nodes_paint = null;
        arcs_paint = null;
        panel1.repaint();

        menuItem11.setEnabled(false);
        menuItem8.setEnabled(false);
        menu3.setEnabled(false);
        menu4.setEnabled(false);
    }


    private void menuItem5ActionPerformed(ActionEvent e) {
        int option = 0;
        final int OK = 0;
        final int ANNULLA = 1;

        routes = new HashMap<>();

        JPanel panelRoutes = new JPanel();
        JLabel jLabel_routes = new JLabel("Numero tratte");
        SpinnerModel spinnerModel_routes = new SpinnerNumberModel(1, 1, 5, 1);
        JSpinner jSpinner_routes = new JSpinner(spinnerModel_routes);

        panelRoutes.add(jLabel_routes);
        panelRoutes.add(jSpinner_routes);

        Object[] options = {"OK", "ANNULLA"};
        option = JOptionPane.showOptionDialog(null, panelRoutes, null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (option == OK) {
            int num_routes = (int) jSpinner_routes.getValue();

            for (int i = 0; i < num_routes; i++) {
                JPanel panelCheckpoint = new JPanel();
                JLabel jLabel_checkpoint = new JLabel("Numero checkpoint");
                SpinnerModel spinnerModel_checkpoint = new SpinnerNumberModel(2, 2, 15, 1);
                JSpinner jSpinner_checkpoint = new JSpinner(spinnerModel_checkpoint);

                panelCheckpoint.add(jLabel_checkpoint);
                panelCheckpoint.add(jSpinner_checkpoint);

                option = JOptionPane.showOptionDialog(null, panelCheckpoint, null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (option == OK) {
                    int num_checkpoint = (int) jSpinner_checkpoint.getValue();

                    JPanel panelRoute = new JPanel(new GridLayout(num_checkpoint + 1, 3));

                    ArrayList<JTextField> lat = new ArrayList<>();
                    ArrayList<JTextField> lon = new ArrayList<>();
                    ArrayList<Node> routeNodes = new ArrayList<>();

                    JLabel jLabel_empty = new JLabel("");
                    JLabel jLabel_lat = new JLabel("Lat");
                    JLabel jLabel_lon = new JLabel("Lon");

                    panelRoute.add(jLabel_empty);
                    panelRoute.add(jLabel_lat);
                    panelRoute.add(jLabel_lon);

                    for (int j = 0; j < num_checkpoint; j++) {
                        JLabel jLabel_check = new JLabel("Checkpoint " + (j + 1));
                        JTextField jTextField_lat = new JTextField(7);
                        JTextField jTextField_lon = new JTextField(7);

                        lat.add(jTextField_lat);
                        lon.add(jTextField_lon);

                        panelRoute.add(jLabel_check);
                        panelRoute.add(jTextField_lat);
                        panelRoute.add(jTextField_lon);
                    }
                    option = JOptionPane.showOptionDialog(null, panelRoute, null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (option == OK) {
                        for (int k = 0; k < lat.size(); k++) {
                            float latitudine = 0;
                            float longitudine = 0;

                            latitudine = Float.parseFloat(lat.get(k).getText());
                            longitudine = Float.parseFloat(lon.get(k).getText());

                            Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);
                            routeNodes.add(node);
                        }

                        Route route = controllerRoute.createRoute(nodes, routeNodes);

                        routes.put((long) i, route);

                        panel1.repaint();
                    } else if (option == ANNULLA) {
                        System.out.println("Annullato scelta latitudine e longitudine");
                    }
                } else if (option == ANNULLA) {
                    System.out.println("Annullato scelta numero checkpoint");
                }
            }

        } else if (option == ANNULLA) {
            System.out.println("Annullato scelta numero rotte");
        }
        controllerRoute.printRoutes(routes);


    }

    private void button1ActionPerformed(ActionEvent e) {
        ArrayList<Node> routeNodes = new ArrayList<>();


        String area = textArea2.getText().toString();

        ArrayList<float[]> checkpoint = controllerRoute.readArea(area);

        for (int i = 0; i < checkpoint.size(); i++) {
            float latitudine = checkpoint.get(i)[0];
            float longitudine = checkpoint.get(i)[1];

            Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);
            routeNodes.add(node);
        }

        Route route = controllerRoute.createRoute(nodes, routeNodes);

        //routes.put((long)i,route);

        panel1.repaint();

    }





    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
        menuBar1 = new JMenuBar();
        menu2 = new JMenu();
        menuItem4 = new JMenuItem();
        menuItem7 = new JMenuItem();
        menuItem11 = new JMenuItem();
        menuItem8 = new JMenuItem();
        menuItem3 = new JMenuItem();
        menu3 = new JMenu();
        menuItem10 = new JMenuItem();
        menu4 = new JMenu();
        menuItem9 = new JMenuItem();
        menu5 = new JMenu();
        menuItem5 = new JMenuItem();
        menu1 = new JMenu();
        menuItem2 = new JMenuItem();
        menuItem1 = new JMenuItem();
        panel1 = new JPanel(){

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                disegna(g);
            }

        };
        panel2 = new JPanel();
        panel3 = new JPanel();
        scrollPane3 = new JScrollPane();
        textArea2 = new JTextArea();
        label1 = new JLabel();
        button1 = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== menu2 ========
            {
                menu2.setText("Mappa");

                //---- menuItem4 ----
                menuItem4.setText("Carica / Esporta");
                menuItem4.addActionListener(e -> menuItem4ActionPerformed(e));
                menu2.add(menuItem4);

                //---- menuItem7 ----
                menuItem7.setText("Disegna ");
                menuItem7.addActionListener(e -> menuItem7ActionPerformed(e));
                menu2.add(menuItem7);

                //---- menuItem11 ----
                menuItem11.setText("Reset ");
                menuItem11.setEnabled(false);
                menuItem11.addActionListener(e -> menuItem11ActionPerformed(e));
                menu2.add(menuItem11);

                //---- menuItem8 ----
                menuItem8.setText("Cancella");
                menuItem8.setEnabled(false);
                menuItem8.addActionListener(e -> menuItem8ActionPerformed(e));
                menu2.add(menuItem8);

                //---- menuItem3 ----
                menuItem3.setText("Esci");
                menuItem3.addActionListener(e -> menuItem3ActionPerformed(e));
                menu2.add(menuItem3);
            }
            menuBar1.add(menu2);

            //======== menu3 ========
            {
                menu3.setText("Visite");
                menu3.setEnabled(false);

                //---- menuItem10 ----
                menuItem10.setText("Profondit\u00e0");
                menuItem10.addActionListener(e -> menuItem10ActionPerformed(e));
                menu3.add(menuItem10);
            }
            menuBar1.add(menu3);

            //======== menu4 ========
            {
                menu4.setText("Percorso");
                menu4.setEnabled(false);

                //---- menuItem9 ----
                menuItem9.setText("Dijkstra");
                menuItem9.addActionListener(e -> menuItem9ActionPerformed(e));
                menu4.add(menuItem9);
            }
            menuBar1.add(menu4);

            //======== menu5 ========
            {
                menu5.setText("Tratte");
                menu5.setEnabled(false);

                //---- menuItem5 ----
                menuItem5.setText("Crea");
                menuItem5.addActionListener(e -> menuItem5ActionPerformed(e));
                menu5.add(menuItem5);
            }
            menuBar1.add(menu5);

            //======== menu1 ========
            {
                menu1.setText("Database");

                //---- menuItem2 ----
                menuItem2.setText("Connetti");
                menuItem2.addActionListener(e -> menuItem2ActionPerformed(e));
                menu1.add(menuItem2);

                //---- menuItem1 ----
                menuItem1.setText("Inserisci");
                menuItem1.setEnabled(false);
                menuItem1.addActionListener(e -> menuItem1ActionPerformed(e));
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //======== panel1 ========
        {
            panel1.setBackground(Color.white);
            panel1.setBorder(LineBorder.createBlackLineBorder());
            panel1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    panel1MouseClicked(e);
                }
            });

            // JFormDesigner evaluation mark
            panel1.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGap(0, 767, Short.MAX_VALUE)
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //======== panel2 ========
        {

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //======== panel3 ========
        {

            //======== scrollPane3 ========
            {
                scrollPane3.setViewportView(textArea2);
            }

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(15, Short.MAX_VALUE))
            );
        }

        //---- label1 ----
        label1.setText("Tratte");

        //---- button1 ----
        button1.setText("Crea");
        button1.setEnabled(false);
        button1.addActionListener(e -> button1ActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(label1, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(button1)
                    .addGap(34, 34, 34))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
    private JMenuBar menuBar1;
    private JMenu menu2;
    private JMenuItem menuItem4;
    private JMenuItem menuItem7;
    private JMenuItem menuItem11;
    private JMenuItem menuItem8;
    private JMenuItem menuItem3;
    private JMenu menu3;
    private JMenuItem menuItem10;
    private JMenu menu4;
    private JMenuItem menuItem9;
    private JMenu menu5;
    private JMenuItem menuItem5;
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem1;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JScrollPane scrollPane3;
    private JTextArea textArea2;
    private JLabel label1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private File openFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Scegli file streetMap");

        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String pathFile = jFileChooser.getSelectedFile().getPath();

            File file = new File(pathFile);

            System.out.println("SCELTA FILE");
            System.out.println("   Aperto file: " + pathFile);

            return file;
        } else {
            System.out.println("Nessun file selezionato");
            return null;
        }
    }

    private File selectPath() {
        FileNameExtensionFilter grfFilter = new FileNameExtensionFilter("osm.grf files (*osm.grf)", "osm.grf");
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Scegli cartella destinazione");
        jFileChooser.addChoosableFileFilter(grfFilter);
        jFileChooser.setFileFilter(grfFilter);

        File file = null;

        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile();

            return file;
        } else {
            System.out.println("Nessun percorso selezionato");
            return null;
        }


    }

    private Node getNodoVicinoByXY(int x, int y) {

        Node ndOut = null;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        if (nodes_paint != null && arcs_paint != null) {
            for (Node n : nodes_paint.values()) {
                if (n.getX() > maxX) {
                    maxX = n.getX();
                }
                if (n.getX() < minX) {
                    minX = n.getX();
                }
                if (n.getY() > maxY) {
                    maxY = n.getY();
                }
                if (n.getY() < minY) {
                    minY = n.getY();
                }
            }

            double w = ((maxX - minX));
            double h = ((maxY - minY));

            double rap = 1;
            double rh = h / w;
            double rhC = (panel1.getSize().height * 1.0) / (panel1.getSize().width * 1.0);
            if (rh > rhC) {
                rap = (panel1.getSize().height * 1.0) / h;
            } else {
                rap = (panel1.getSize().width * 1.0) / w;
            }

            double dist = Double.MAX_VALUE;

            //Stampa nodi
            for (Node n : nodes_paint.values()) {
                double x1 = (n.getX() - minX * 1.0) * rap;
                double y1 = (n.getY() - minY * 1.0) * rap;
                double d = (x - x1) * (x - x1) + (y - y1) * (y - y1);
                if (d < dist) {
                    dist = d;
                    ndOut = n;
                }

            }
        }
        return ndOut;
    }

    private boolean disegnaMap(File file) {
        if (file != null) {
            if (controllerFileMap.readFile(file)) {
                nodes = controllerFileMap.getNodes();
                arcs = controllerFileMap.getArcs();

                //per risolvere bug --> disegna parte del grafo quando si crea la mappa importata
                nodes_paint = nodes;
                arcs_paint = arcs;

                panel1.repaint();
                menu3.setEnabled(true);
                menu4.setEnabled(true);
                menuItem11.setEnabled(true);
                menuItem8.setEnabled(true);
                menu5.setEnabled(true);
                button1.setEnabled(true);

                return true;
            }
        }
        return false;
    }

    private boolean esportaMap(File file) {
        if (file != null) {
            controllerExport.export(file, nodes, arcs);
            nodes = controllerExport.getNodes();
            arcs = controllerExport.getArcs();

            //manca controllo nodes, arcs is empty
            return true;
        }

        return false;
    }

    private void disegna(Graphics gg) {

        Graphics2D g = (Graphics2D) gg;
        g.setColor(Color.black);

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        if (nodes_paint != null && arcs_paint != null) {
            for (Node n : nodes_paint.values()) {
                if (n.getX() > maxX) {
                    maxX = n.getX();
                }
                if (n.getX() < minX) {
                    minX = n.getX();
                }
                if (n.getY() > maxY) {
                    maxY = n.getY();
                }
                if (n.getY() < minY) {
                    minY = n.getY();
                }
            }

            double w = ((maxX - minX));
            double h = ((maxY - minY));

            double rap = 1;
            double rh = h / w;
            double rhC = (panel1.getSize().height * 1.0) / (panel1.getSize().width * 1.0);
            if (rh > rhC) {
                rap = (panel1.getSize().height * 1.0) / h;
            } else {
                rap = (panel1.getSize().width * 1.0) / w;
            }

            //Stampa archi
            for (Arc arc : arcs_paint) {

                if (arc.getMark() == 1) {
                    g.setColor(Color.blue);
                    g.setStroke(new BasicStroke(2));
                }
                if (arc.getMark() == 0) {
                    g.setColor(Color.black);
                    g.setStroke(new BasicStroke(1));

                }
                double x1 = (arc.getFrom().getX() - minX * 1.0) * rap;
                double y1 = (arc.getFrom().getY() - minY * 1.0) * rap;
                double x2 = (arc.getTo().getX() - minX * 1.0) * rap;
                double y2 = (arc.getTo().getY() - minY * 1.0) * rap;

                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            }

            //Stampa nodi
            for (Node n : nodes_paint.values()) {
                double x1 = (n.getX() - minX * 1.0) * rap;
                double y1 = (n.getY() - minY * 1.0) * rap;

                if (n.getMark() == -1) {
                    g.setColor(Color.black);
                }
                if (n.getMark() == 1) {
                    g.setColor(Color.blue);

                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }
                if (n.getMark() == 0) {
                    g.setColor(Color.red);

                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }
            }
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Show().setVisible(true);
            }
        });
    }

}
