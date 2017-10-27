/*
 * Created by JFormDesigner on Wed Oct 11 17:43:14 CEST 2017
 */

package com.OpenStreetMap.View;

import java.awt.*;
import java.awt.event.*;
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
    ImportMap importMap = new ImportMap();
    ExportMap exportMap = new ExportMap();
    Database database = new Database();
    ImportPlotMap importPlotMap = new ImportPlotMap();
    ControllerRoutes controllerRoutes = new ControllerRoutes();
    ControllerStudenti controllerStudenti = new ControllerStudenti();
    Visits visits = new Visits();
    Dijkstra dijkstra = new Dijkstra();

    private DB dbStreetMap = null;
    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;
    private HashMap<Long, Node> nodes_paint = null;
    private HashSet<Arc> arcs_paint = null;
    private HashSet<Route> routes = null;
    private HashSet<Node> nodes_students = null;

    boolean plotMap = true;

    private File file = null;

    public Show() {
        initComponents();
    }

    private void menuItem2ActionPerformed(ActionEvent e) {
        dbStreetMap = database.connectDB("localhost", 27017, "StreetMap");
        if (dbStreetMap != null) {
            JOptionPane.showMessageDialog(null, "Connesso al DB");
            menuItem2.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(null, "Connessione al DB non riuscita");
        }
    }

    private void menuItem1ActionPerformed(ActionEvent e) {
        DBCollection collectionNode = database.getCollection(dbStreetMap, "Node");
        DBCollection collectionWay = database.getCollection(dbStreetMap, "Way");
        DBCollection collectionArc = database.getCollection(dbStreetMap, "Arc");

        database.insertNodesDB(collectionNode, importMap.getNodes());
        database.insertWaysDB(collectionWay, importMap.getWays());
        database.insertArcsDB(collectionArc, importMap.getArcs());

        JOptionPane.showMessageDialog(null, "Elementi inseriti");
    }

    private void menuItem3ActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void menuItem4ActionPerformed(ActionEvent e) {
        file = openFile();

        if (file != null) {
            importMap.create(file, 5, 2, 100, true, false);
            nodes = importMap.getNodes();
            arcs = importMap.getArcs();

            if (!nodes.isEmpty() || !arcs.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Mappa creata nodi: " + nodes.size() + " archi: " + arcs.size());

                menuItem1.setEnabled(true);

                file = selectPath();
                if (esportaMap(file)) {
                    Object[] options = {"Si", "No"};
                    int option = JOptionPane.showOptionDialog(null, "Mappa esportata, disegnare mappa?", null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (option == 0) {
                        if (disegnaMap(file)) {
                            menu3.setEnabled(true);
                            menu4.setEnabled(true);
                            reset_item.setEnabled(true);
                            cancella_item.setEnabled(true);
                            tratte_button.setEnabled(true);
                            utenti_button.setEnabled(true);
                            mappa_panel.repaint();
                        }
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
        if (disegnaMap(file)) {
            menu3.setEnabled(true);
            menu4.setEnabled(true);
            reset_item.setEnabled(true);
            cancella_item.setEnabled(true);
            tratte_button.setEnabled(true);
            utenti_button.setEnabled(true);
            mappa_panel.repaint();
        }
    }

    private void panel1MouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        Node n = getNodoVicinoByXY(x, y);
        JOptionPane.showMessageDialog(null, "index: " + n.getIndex() + "; id: " + n.getId() + "; coordinate: " + n.getLat() + "," + n.getLon());
        tratte_area.append("# " + n.getLat() + " " + n.getLon() + "\n");
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
        mappa_panel.repaint();
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

        dijkstra.run(sorgente, destinazione, nodes, true);
        mappa_panel.repaint();
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
        tratte_area.removeAll();
        routes = null;


        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(-1);

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();
                arc.setMark(0);
            }
        }
        mappa_panel.repaint();
    }

    private void menuItem8ActionPerformed(ActionEvent e) {
        nodes = null;
        arcs = null;
        nodes_paint = null;
        arcs_paint = null;
        routes = null;
        tratte_area.removeAll();
        mappa_panel.repaint();

        reset_item.setEnabled(false);
        cancella_item.setEnabled(false);
        menu3.setEnabled(false);
        menu4.setEnabled(false);
    }


    /*private void menuItem5ActionPerformed(ActionEvent e) {
        int option = 0;
        final int OK = 0;
        final int ANNULLA = 1;

        routes = new HashSet<>();

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

                        Route route = controllerRoutes.createRoute(nodes, routeNodes);

                        routes.add(route);

                        mappa_panel.repaint();
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
        //controllerRoutes.printRoutes(routes);


    }*/

    private void button1ActionPerformed(ActionEvent e) {

        String area = tratte_area.getText().toString();

        routes = controllerRoutes.read(area, nodes);

        mappa_panel.repaint();

    }

    private void button2ActionPerformed(ActionEvent e) {
        String area = utenti_area.getText().toString();
        ArrayList<float[]> lat_lon_stu = controllerStudenti.readArea(area);

        for (int i = 0; i < lat_lon_stu.size(); i++) {
            float latitudine = lat_lon_stu.get(i)[0];
            float longitudine = lat_lon_stu.get(i)[1];
            float num_studenti = lat_lon_stu.get(i)[2];

            Node node = Node.nodeByLatLon(nodes, latitudine, longitudine);
            node.setNum_studenti((int) num_studenti);

        }

        mappa_panel.repaint();
    }

    private void menuItem5ActionPerformed(ActionEvent e) {
        // TODO add your code here
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
        menuBar1 = new JMenuBar();
        mappa_menu = new JMenu();
        caricaEsporta_item = new JMenuItem();
        disegna_item = new JMenuItem();
        reset_item = new JMenuItem();
        cancella_item = new JMenuItem();
        esci_item = new JMenuItem();
        menu3 = new JMenu();
        menuItem10 = new JMenuItem();
        menu4 = new JMenu();
        menuItem9 = new JMenuItem();
        menu1 = new JMenu();
        menuItem2 = new JMenuItem();
        menuItem1 = new JMenuItem();
        tabbedPane = new JTabbedPane();
        mappa_panel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                disegna(g);
            }

        };
        tratte_panel = new JPanel();
        scrollPane2 = new JScrollPane();
        tratte_area = new JTextArea();
        scrollPane1 = new JScrollPane();
        utenti_area = new JTextArea();
        tratte_label = new JLabel();
        utenti_label = new JLabel();
        tratte_button = new JButton();
        utenti_button = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== mappa_menu ========
            {
                mappa_menu.setText("Mappa");

                //---- caricaEsporta_item ----
                caricaEsporta_item.setText("Carica / Esporta");
                caricaEsporta_item.addActionListener(e -> menuItem4ActionPerformed(e));
                mappa_menu.add(caricaEsporta_item);

                //---- disegna_item ----
                disegna_item.setText("Disegna ");
                disegna_item.addActionListener(e -> menuItem7ActionPerformed(e));
                mappa_menu.add(disegna_item);

                //---- reset_item ----
                reset_item.setText("Reset ");
                reset_item.setEnabled(false);
                reset_item.addActionListener(e -> menuItem11ActionPerformed(e));
                mappa_menu.add(reset_item);

                //---- cancella_item ----
                cancella_item.setText("Cancella");
                cancella_item.setEnabled(false);
                cancella_item.addActionListener(e -> menuItem8ActionPerformed(e));
                mappa_menu.add(cancella_item);

                //---- esci_item ----
                esci_item.setText("Esci");
                esci_item.addActionListener(e -> menuItem3ActionPerformed(e));
                mappa_menu.add(esci_item);
            }
            menuBar1.add(mappa_menu);

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

        //======== tabbedPane ========
        {

            //======== mappa_panel ========
            {
                mappa_panel.setBackground(Color.white);
                mappa_panel.setBorder(LineBorder.createBlackLineBorder());
                mappa_panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        panel1MouseClicked(e);
                    }
                });

                // JFormDesigner evaluation mark
                mappa_panel.setBorder(new javax.swing.border.CompoundBorder(
                        new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                                java.awt.Color.red), mappa_panel.getBorder()));
                mappa_panel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                        if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                    }
                });


                GroupLayout mappa_panelLayout = new GroupLayout(mappa_panel);
                mappa_panel.setLayout(mappa_panelLayout);
                mappa_panelLayout.setHorizontalGroup(
                        mappa_panelLayout.createParallelGroup()
                                .addGap(0, 984, Short.MAX_VALUE)
                );
                mappa_panelLayout.setVerticalGroup(
                        mappa_panelLayout.createParallelGroup()
                                .addGap(0, 568, Short.MAX_VALUE)
                );
            }
            tabbedPane.addTab("Mappa", mappa_panel);

            //======== tratte_panel ========
            {

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(tratte_area);
                }

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(utenti_area);
                }

                //---- tratte_label ----
                tratte_label.setText("Tratte");

                //---- utenti_label ----
                utenti_label.setText("Utenti");

                //---- tratte_button ----
                tratte_button.setText("Crea");
                tratte_button.setEnabled(false);
                tratte_button.addActionListener(e -> button1ActionPerformed(e));

                //---- utenti_button ----
                utenti_button.setText("Crea");
                utenti_button.setEnabled(false);
                utenti_button.addActionListener(e -> button2ActionPerformed(e));

                GroupLayout tratte_panelLayout = new GroupLayout(tratte_panel);
                tratte_panel.setLayout(tratte_panelLayout);
                tratte_panelLayout.setHorizontalGroup(
                        tratte_panelLayout.createParallelGroup()
                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                        .addGroup(tratte_panelLayout.createParallelGroup()
                                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                                        .addGap(16, 16, 16)
                                                        .addComponent(tratte_label))
                                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                                        .addContainerGap()
                                                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 475, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(tratte_panelLayout.createParallelGroup()
                                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                                        .addComponent(utenti_label)
                                                        .addGap(430, 430, 430))
                                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                                        .addGap(16, 16, 16)
                                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))))
                                .addGroup(tratte_panelLayout.createSequentialGroup()
                                        .addGap(160, 160, 160)
                                        .addComponent(tratte_button, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 320, Short.MAX_VALUE)
                                        .addComponent(utenti_button, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                                        .addGap(186, 186, 186))
                );
                tratte_panelLayout.setVerticalGroup(
                        tratte_panelLayout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, tratte_panelLayout.createSequentialGroup()
                                        .addContainerGap(21, Short.MAX_VALUE)
                                        .addGroup(tratte_panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(tratte_label)
                                                .addComponent(utenti_label))
                                        .addGap(18, 18, 18)
                                        .addGroup(tratte_panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(tratte_panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(tratte_button)
                                                .addComponent(utenti_button))
                                        .addContainerGap())
                );
            }
            tabbedPane.addTab("Tratte", tratte_panel);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tabbedPane)
                                .addGap(6, 6, 6))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 604, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(2, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
    private JMenuBar menuBar1;
    private JMenu mappa_menu;
    private JMenuItem caricaEsporta_item;
    private JMenuItem disegna_item;
    private JMenuItem reset_item;
    private JMenuItem cancella_item;
    private JMenuItem esci_item;
    private JMenu menu3;
    private JMenuItem menuItem10;
    private JMenu menu4;
    private JMenuItem menuItem9;
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem1;
    private JTabbedPane tabbedPane;
    private JPanel mappa_panel;
    private JPanel tratte_panel;
    private JScrollPane scrollPane2;
    private JTextArea tratte_area;
    private JScrollPane scrollPane1;
    private JTextArea utenti_area;
    private JLabel tratte_label;
    private JLabel utenti_label;
    private JButton tratte_button;
    private JButton utenti_button;
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
            double rhC = (mappa_panel.getSize().height * 1.0) / (mappa_panel.getSize().width * 1.0);
            if (rh > rhC) {
                rap = (mappa_panel.getSize().height * 1.0) / h;
            } else {
                rap = (mappa_panel.getSize().width * 1.0) / w;
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
            if (importPlotMap.readFile(file)) {
                nodes = importPlotMap.getNodes();
                arcs = importPlotMap.getArcs();

                //per risolvere bug --> disegna parte del grafo quando si crea la mappa importata
                nodes_paint = nodes;
                arcs_paint = arcs;

                return true;
            }
        }
        return false;
    }

    private boolean esportaMap(File file) {
        if (file != null) {
            exportMap.export(file, nodes, arcs);
            nodes = exportMap.getNodes();
            arcs = exportMap.getArcs();

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
            double rhC = (mappa_panel.getSize().height * 1.0) / (mappa_panel.getSize().width * 1.0);
            if (rh > rhC) {
                rap = (mappa_panel.getSize().height * 1.0) / h;
            } else {
                rap = (mappa_panel.getSize().width * 1.0) / w;
            }

            //Disegna archi
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

            //Disegna nodi
            for (Node n : nodes_paint.values()) {
                double x1 = (n.getX() - minX * 1.0) * rap;
                double y1 = (n.getY() - minY * 1.0) * rap;
                int mark = n.getMark();
                int num_studenti = n.getNum_studenti();

                if (num_studenti > 0) {
                    g.setColor(Color.yellow);
                    g.drawOval((int) x1, (int) y1, num_studenti, num_studenti);
                }

                if (mark == -1) {
                    g.setColor(Color.black);
                }
                if (mark == 1) {
                    g.setColor(Color.blue);

                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }
                if (mark == 0) {
                    g.setColor(Color.red);

                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }
            }

            //Disegna tratte
            if (routes != null) {
                for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
                    Route r = it.next();

                    Color randomColor = new Color((int) (Math.random() * 0x1000000));
                    g.setColor(randomColor);

                    for (int i = 0; i < r.getNodes().size(); i++) {
                        Node r_n = r.getNodes().get(i);

                        double x = (r_n.getX() - minX * 1.0) * rap;
                        double y = (r_n.getY() - minY * 1.0) * rap;

                        g.setFont(g.getFont().deriveFont(10f));
                        g.drawString("" + r_n.getIndex(), (int) x, (int) y);

                        if (i != r.getNodes().size() - 1) {
                            Arc arc = Arc.arcByFromTo(r_n, r.getNodes().get(i + 1));

                            double x1 = (arc.getFrom().getX() - minX * 1.0) * rap;
                            double y1 = (arc.getFrom().getY() - minY * 1.0) * rap;
                            double x2 = (arc.getTo().getX() - minX * 1.0) * rap;
                            double y2 = (arc.getTo().getY() - minY * 1.0) * rap;

                            g.setStroke(new BasicStroke(2));
                            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                        }
                    }
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
