/*
 * Created by JFormDesigner on Wed Oct 11 17:43:14 CEST 2017
 */

package com.OpenStreetMap.View;

import com.OpenStreetMap.Controller.ControllerRoutes;
import com.OpenStreetMap.Controller.ImportMap;
import com.OpenStreetMap.Controller.Dijkstra;
import com.OpenStreetMap.Controller.Database;
import com.OpenStreetMap.Controller.Visit;
import com.OpenStreetMap.Controller.ControllerStudenti;
import com.OpenStreetMap.Controller.ImportPlotMap;
import com.OpenStreetMap.Controller.ExportMap;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    Visit visit = new Visit();
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
            importMap.create(file, 5, 2, 100, false, false);
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
                            reset_item.setEnabled(true);
                            cancella_item.setEnabled(true);
                            tabbedPane.setEnabled(true);

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
            reset_item.setEnabled(true);
            cancella_item.setEnabled(true);
            tabbedPane.setEnabled(true);

            mappa_panel.repaint();
        }
    }

    private void panel1MouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        Node n = getNodoVicinoByXY(x, y);
        System.out.println("index: " + n.getIndex() + "; id: " + n.getId() + "; " + n.getLat() + "," + n.getLon());

        JPanel panelMouseListener = new JPanel();
        if (n.getNum_studenti() <= 0) {
            JLabel node_label = new JLabel("index: " + n.getIndex() + "; id: " + n.getId() + " lat: " + n.getLat() + " lon: " + n.getLon());
            panelMouseListener.add(node_label);
        } else {
            JLabel node_label = new JLabel("index: " + n.getIndex() + "; id: " + n.getId() + " lat: " + n.getLat() + " lon: " + n.getLon() + " num: " + n.getNum_studenti());
            panelMouseListener.add(node_label);
        }


        final int TRATTE = 0;
        final int UTENTI = 1;
        final int DIJKSTRA = 2;
        final int VISITA = 3;
        final int ANNULLA = 4;

        Object[] options = {"Tratte", "Utenti", "Dijkstra", "Visita", "Annulla"};
        int option = JOptionPane.showOptionDialog(null, panelMouseListener, "Nodo selezionato", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (option) {
            case TRATTE:
                tratte_area.append("# " + n.getLat() + " " + n.getLon() + "\n");
                break;
            case UTENTI:
                utenti_area.append("# " + n.getLat() + " " + n.getLon() + "\n");
                break;
            case DIJKSTRA:
                Object[] options2 = {"Sorgente", "Destinazione", "Annulla"};
                int option2 = JOptionPane.showOptionDialog(null, null, "Dijkstra", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);

                if (option2 == 0) {
                    sorgenteLat_text.setText(String.valueOf(n.getLat()));
                    sorgenteLon_text.setText(String.valueOf(n.getLon()));
                }
                if (option2 == 1) {
                    destinazioneLat_text.setText(String.valueOf(n.getLat()));
                    destinazioneLon_text.setText(String.valueOf(n.getLon()));
                }
                if (option2 == 2) {
                    break;
                }
                break;
            case VISITA:
                partenzaLat_text.setText(String.valueOf(n.getLat()));
                partenzaLon_text.setText(String.valueOf(n.getLon()));
                break;
            case ANNULLA:
                break;
        }
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
        resetComponent();
        resetMark();
        resetStudent();
        routes = null;
        nodes_students = null;

        mappa_panel.repaint();
    }

    private void menuItem8ActionPerformed(ActionEvent e) {
        resetComponent();
        resetMark();
        cancel();

        mappa_panel.repaint();

    }

    private void button1ActionPerformed(ActionEvent e) {

        String area = tratte_area.getText().toString();

        routes = controllerRoutes.read(area, nodes);

        String output_routes = controllerRoutes.printRoutes(routes);
        tratteOutput_area.setText(output_routes);

        mappa_panel.repaint();

    }

    private void button2ActionPerformed(ActionEvent e) {
        String area = utenti_area.getText().toString();

        nodes_students = controllerStudenti.read(area, nodes, routes);
        String output_nodes_students = "";
        for (Iterator<Node> it = nodes_students.iterator(); it.hasNext(); ) {
            Node node = it.next();

            output_nodes_students += "id: " + node.getId() + " index: " + node.getIndex() + " lat: " + node.getLat() + " lon: " + node.getLon() + " tratta: " + node.getRoute().getName() + " num: " + node.getNum_studenti() + "\n";

        }
        utentiOutput_area.setText(output_nodes_students);

        mappa_panel.repaint();
    }

    private void rDijkstra_buttonActionPerformed(ActionEvent e) {
        Node sorgente = Node.randomNode(nodes);
        Node destinazione = Node.randomNode(nodes);

        sorgenteLat_text.setText(String.valueOf(sorgente.getLat()));
        sorgenteLon_text.setText(String.valueOf(sorgente.getLon()));
        destinazioneLat_text.setText(String.valueOf(destinazione.getLat()));
        destinazioneLon_text.setText(String.valueOf(destinazione.getLon()));
    }

    private void rVisita_buttonActionPerformed(ActionEvent e) {
        Node startingNode = Node.randomNode(nodes);

        partenzaLat_text.setText(String.valueOf(startingNode.getLat()));
        partenzaLon_text.setText(String.valueOf(startingNode.getLon()));
    }

    private void dijkstra_buttonActionPerformed(ActionEvent e) {
        float lat_s = Float.parseFloat(sorgenteLat_text.getText());
        float lon_s = Float.parseFloat(sorgenteLon_text.getText());
        float lat_d = Float.parseFloat(destinazioneLat_text.getText());
        float lon_d = Float.parseFloat(destinazioneLon_text.getText());

        Node sorgente = Node.nodeByLatLon(nodes, lat_s, lon_s);
        Node destinazione = Node.nodeByLatLon(nodes, lat_d, lon_d);

        ArrayList<Node> percorso = dijkstra.run(sorgente, destinazione, nodes, true);

        String output_dijkstra = dijkstra.printPercorso(percorso);
        dijkstra_area.setText(output_dijkstra);

        mappa_panel.repaint();
    }

    private void visita_buttonActionPerformed(ActionEvent e) {
        float lat = Float.parseFloat(partenzaLat_text.getText());
        float lon = Float.parseFloat(partenzaLon_text.getText());

        Node startingNode = Node.nodeByLatLon(nodes, lat, lon);

        ArrayList<Node> visit_nodes = visit.visita(nodes, startingNode);

        String output_visit = visit.printVisit(visit_nodes);
        visita_area.setText(output_visit);

        mappa_panel.repaint();
    }

    private void stopIdeal_buttonActionPerformed(ActionEvent e) {
        HashMap<Node, HashMap<ArrayList<Node>, Double>> allStop = controllerStudenti.allStop(nodes, nodes_students, routes);
        String output_fermate = controllerStudenti.printPercorsi(allStop);
        stop_area.setText(output_fermate);

        HashMap<Node, HashMap<ArrayList<Node>, Double>> idealStop = controllerStudenti.idealStop(allStop);
        String output_fermateIdeal = controllerStudenti.printPercorsi(idealStop);
        idealStop_area.setText(output_fermateIdeal);
    }

    private void allStopIdeal_buttonActionPerformed(ActionEvent e) {
        HashMap<Node, HashMap<ArrayList<Node>, Double>> allRoute = controllerStudenti.allRoute(nodes, nodes_students, routes);
        String output_fermate = controllerStudenti.printPercorsi(allRoute);
        allStop_area.setText(output_fermate);

        HashMap<Node, HashMap<ArrayList<Node>, Double>> idealStop = controllerStudenti.idealStop(allRoute);
        String output_fermateIdeal = controllerStudenti.printPercorsi(idealStop);
        ideaAllStop_area.setText(output_fermateIdeal);
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
        menu1 = new JMenu();
        menuItem2 = new JMenuItem();
        menuItem1 = new JMenuItem();
        tabbedPane = new JTabbedPane();
        mappa_panel = new JPanel(){

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                disegna(g);
            }

        };
        tratte_panel = new JPanel();
        scrollPane2 = new JScrollPane();
        tratte_area = new JTextArea();
        tratte_button = new JButton();
        scrollPane3 = new JScrollPane();
        tratteOutput_area = new JTextArea();
        utenti_panel = new JPanel();
        scrollPane1 = new JScrollPane();
        utenti_area = new JTextArea();
        utenti_button = new JButton();
        scrollPane4 = new JScrollPane();
        utentiOutput_area = new JTextArea();
        stopIdeal_button = new JButton();
        scrollPane5 = new JScrollPane();
        stop_area = new JTextArea();
        scrollPane8 = new JScrollPane();
        idealStop_area = new JTextArea();
        scrollPane9 = new JScrollPane();
        allStop_area = new JTextArea();
        scrollPane10 = new JScrollPane();
        ideaAllStop_area = new JTextArea();
        allStopIdeal_button = new JButton();
        altro_panel = new JPanel();
        scrollPane6 = new JScrollPane();
        dijkstra_area = new JTextArea();
        sorgenteLat_text = new JTextField();
        destinazioneLat_text = new JTextField();
        partenzaLat_text = new JTextField();
        partenza_label = new JLabel();
        rDijkstra_button = new JButton();
        rVisita_button = new JButton();
        dijkstra_button = new JButton();
        visita_button = new JButton();
        sorgenteLon_text = new JTextField();
        destinazioneLon_text = new JTextField();
        partenzaLon_text = new JTextField();
        sorgente_label = new JLabel();
        destinazione_label = new JLabel();
        scrollPane7 = new JScrollPane();
        visita_area = new JTextArea();

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
            tabbedPane.setPreferredSize(null);

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
                        java.awt.Color.red), mappa_panel.getBorder())); mappa_panel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                mappa_panel.setLayout(new FlowLayout());
            }
            tabbedPane.addTab("Mappa", mappa_panel);

            //======== tratte_panel ========
            {

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(tratte_area);
                }

                //---- tratte_button ----
                tratte_button.setText("Crea");
                tratte_button.addActionListener(e -> button1ActionPerformed(e));

                //======== scrollPane3 ========
                {

                    //---- tratteOutput_area ----
                    tratteOutput_area.setEditable(false);
                    scrollPane3.setViewportView(tratteOutput_area);
                }

                GroupLayout tratte_panelLayout = new GroupLayout(tratte_panel);
                tratte_panel.setLayout(tratte_panelLayout);
                tratte_panelLayout.setHorizontalGroup(
                    tratte_panelLayout.createParallelGroup()
                        .addGroup(tratte_panelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 465, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                            .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 465, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(tratte_panelLayout.createSequentialGroup()
                            .addGap(119, 119, 119)
                            .addComponent(tratte_button, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(845, Short.MAX_VALUE))
                );
                tratte_panelLayout.setVerticalGroup(
                    tratte_panelLayout.createParallelGroup()
                        .addGroup(tratte_panelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(tratte_panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 495, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 495, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(tratte_button)
                            .addContainerGap(15, Short.MAX_VALUE))
                );
            }
            tabbedPane.addTab("Tratte", tratte_panel);

            //======== utenti_panel ========
            {

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(utenti_area);
                }

                //---- utenti_button ----
                utenti_button.setText("Crea");
                utenti_button.addActionListener(e -> button2ActionPerformed(e));

                //======== scrollPane4 ========
                {

                    //---- utentiOutput_area ----
                    utentiOutput_area.setEditable(false);
                    scrollPane4.setViewportView(utentiOutput_area);
                }

                //---- stopIdeal_button ----
                stopIdeal_button.setText("Route");
                stopIdeal_button.addActionListener(e -> stopIdeal_buttonActionPerformed(e));

                //======== scrollPane5 ========
                {

                    //---- stop_area ----
                    stop_area.setEditable(false);
                    scrollPane5.setViewportView(stop_area);
                }

                //======== scrollPane8 ========
                {

                    //---- idealStop_area ----
                    idealStop_area.setEditable(false);
                    scrollPane8.setViewportView(idealStop_area);
                }

                //======== scrollPane9 ========
                {
                    scrollPane9.setViewportView(allStop_area);
                }

                //======== scrollPane10 ========
                {
                    scrollPane10.setViewportView(ideaAllStop_area);
                }

                //---- allStopIdeal_button ----
                allStopIdeal_button.setText("All");
                allStopIdeal_button.addActionListener(e -> {
			button1ActionPerformed(e);
			allStopIdeal_buttonActionPerformed(e);
		});

                GroupLayout utenti_panelLayout = new GroupLayout(utenti_panel);
                utenti_panel.setLayout(utenti_panelLayout);
                utenti_panelLayout.setHorizontalGroup(
                    utenti_panelLayout.createParallelGroup()
                        .addGroup(utenti_panelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(utenti_panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addComponent(scrollPane9, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(scrollPane10))
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addComponent(scrollPane5, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(scrollPane8))
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(utenti_panelLayout.createParallelGroup()
                                .addComponent(stopIdeal_button)
                                .addComponent(allStopIdeal_button, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
                                .addComponent(utenti_button, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(15, Short.MAX_VALUE))
                );
                utenti_panelLayout.setVerticalGroup(
                    utenti_panelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, utenti_panelLayout.createSequentialGroup()
                            .addGroup(utenti_panelLayout.createParallelGroup()
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addGroup(utenti_panelLayout.createParallelGroup()
                                        .addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addGap(82, 82, 82)
                                    .addComponent(utenti_button)))
                            .addGroup(utenti_panelLayout.createParallelGroup()
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(utenti_panelLayout.createParallelGroup()
                                        .addComponent(scrollPane5, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scrollPane8)))
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addGap(74, 74, 74)
                                    .addComponent(stopIdeal_button)))
                            .addGroup(utenti_panelLayout.createParallelGroup()
                                .addGroup(utenti_panelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(utenti_panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(scrollPane9, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                        .addComponent(scrollPane10, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                    .addContainerGap(12, Short.MAX_VALUE))
                                .addGroup(GroupLayout.Alignment.TRAILING, utenti_panelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                                    .addComponent(allStopIdeal_button)
                                    .addGap(84, 84, 84))))
                );
            }
            tabbedPane.addTab("Utenti", utenti_panel);

            //======== altro_panel ========
            {

                //======== scrollPane6 ========
                {

                    //---- dijkstra_area ----
                    dijkstra_area.setEditable(false);
                    scrollPane6.setViewportView(dijkstra_area);
                }

                //---- partenza_label ----
                partenza_label.setText("partenza");

                //---- rDijkstra_button ----
                rDijkstra_button.setText("R");
                rDijkstra_button.addActionListener(e -> rDijkstra_buttonActionPerformed(e));

                //---- rVisita_button ----
                rVisita_button.setText("R");
                rVisita_button.addActionListener(e -> rVisita_buttonActionPerformed(e));

                //---- dijkstra_button ----
                dijkstra_button.setText("OK");
                dijkstra_button.addActionListener(e -> dijkstra_buttonActionPerformed(e));

                //---- visita_button ----
                visita_button.setText("OK");
                visita_button.addActionListener(e -> visita_buttonActionPerformed(e));

                //---- sorgente_label ----
                sorgente_label.setText("sorgente");

                //---- destinazione_label ----
                destinazione_label.setText("destinazione");

                //======== scrollPane7 ========
                {
                    scrollPane7.setViewportView(visita_area);
                }

                GroupLayout altro_panelLayout = new GroupLayout(altro_panel);
                altro_panel.setLayout(altro_panelLayout);
                altro_panelLayout.setHorizontalGroup(
                    altro_panelLayout.createParallelGroup()
                        .addGroup(altro_panelLayout.createSequentialGroup()
                            .addGap(35, 35, 35)
                            .addGroup(altro_panelLayout.createParallelGroup()
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addComponent(partenza_label)
                                    .addGap(54, 54, 54)
                                    .addGroup(altro_panelLayout.createParallelGroup()
                                        .addComponent(partenzaLon_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(partenzaLat_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(rVisita_button))
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addGroup(altro_panelLayout.createParallelGroup()
                                        .addComponent(sorgente_label)
                                        .addComponent(destinazione_label))
                                    .addGap(27, 27, 27)
                                    .addGroup(altro_panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(sorgenteLat_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sorgenteLon_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(destinazioneLon_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(destinazioneLat_text, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(rDijkstra_button)))
                            .addGap(18, 18, 18)
                            .addGroup(altro_panelLayout.createParallelGroup()
                                .addComponent(scrollPane6, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                .addComponent(scrollPane7, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                            .addGap(57, 57, 57)
                            .addGroup(altro_panelLayout.createParallelGroup()
                                .addComponent(dijkstra_button)
                                .addComponent(visita_button))
                            .addContainerGap(245, Short.MAX_VALUE))
                );
                altro_panelLayout.setVerticalGroup(
                    altro_panelLayout.createParallelGroup()
                        .addGroup(altro_panelLayout.createSequentialGroup()
                            .addGroup(altro_panelLayout.createParallelGroup()
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addGap(138, 138, 138)
                                    .addComponent(dijkstra_button))
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addGap(45, 45, 45)
                                    .addGroup(altro_panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(altro_panelLayout.createSequentialGroup()
                                            .addGroup(altro_panelLayout.createParallelGroup()
                                                .addGroup(altro_panelLayout.createSequentialGroup()
                                                    .addComponent(sorgenteLat_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(sorgenteLon_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(altro_panelLayout.createSequentialGroup()
                                                    .addGap(26, 26, 26)
                                                    .addComponent(sorgente_label)))
                                            .addGroup(altro_panelLayout.createParallelGroup()
                                                .addGroup(GroupLayout.Alignment.TRAILING, altro_panelLayout.createSequentialGroup()
                                                    .addGap(1, 1, 1)
                                                    .addComponent(destinazioneLat_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(destinazioneLon_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(altro_panelLayout.createSequentialGroup()
                                                    .addGroup(altro_panelLayout.createParallelGroup()
                                                        .addGroup(altro_panelLayout.createSequentialGroup()
                                                            .addGap(21, 21, 21)
                                                            .addComponent(rDijkstra_button))
                                                        .addGroup(altro_panelLayout.createSequentialGroup()
                                                            .addGap(59, 59, 59)
                                                            .addComponent(destinazione_label)))
                                                    .addGap(27, 27, 27))))
                                        .addComponent(scrollPane6, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE))))
                            .addGroup(altro_panelLayout.createParallelGroup()
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                                    .addGroup(altro_panelLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, altro_panelLayout.createSequentialGroup()
                                            .addGroup(altro_panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(partenza_label)
                                                .addComponent(rVisita_button))
                                            .addGap(122, 122, 122))
                                        .addGroup(GroupLayout.Alignment.TRAILING, altro_panelLayout.createSequentialGroup()
                                            .addComponent(visita_button)
                                            .addGap(116, 116, 116))
                                        .addGroup(GroupLayout.Alignment.TRAILING, altro_panelLayout.createSequentialGroup()
                                            .addComponent(partenzaLat_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(partenzaLon_text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addGap(104, 104, 104))))
                                .addGroup(altro_panelLayout.createSequentialGroup()
                                    .addGap(50, 50, 50)
                                    .addComponent(scrollPane7, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 44, Short.MAX_VALUE))))
                );
            }
            tabbedPane.addTab("Altro", altro_panel);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 604, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(9, Short.MAX_VALUE))
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
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem1;
    private JTabbedPane tabbedPane;
    private JPanel mappa_panel;
    private JPanel tratte_panel;
    private JScrollPane scrollPane2;
    private JTextArea tratte_area;
    private JButton tratte_button;
    private JScrollPane scrollPane3;
    private JTextArea tratteOutput_area;
    private JPanel utenti_panel;
    private JScrollPane scrollPane1;
    private JTextArea utenti_area;
    private JButton utenti_button;
    private JScrollPane scrollPane4;
    private JTextArea utentiOutput_area;
    private JButton stopIdeal_button;
    private JScrollPane scrollPane5;
    private JTextArea stop_area;
    private JScrollPane scrollPane8;
    private JTextArea idealStop_area;
    private JScrollPane scrollPane9;
    private JTextArea allStop_area;
    private JScrollPane scrollPane10;
    private JTextArea ideaAllStop_area;
    private JButton allStopIdeal_button;
    private JPanel altro_panel;
    private JScrollPane scrollPane6;
    private JTextArea dijkstra_area;
    private JTextField sorgenteLat_text;
    private JTextField destinazioneLat_text;
    private JTextField partenzaLat_text;
    private JLabel partenza_label;
    private JButton rDijkstra_button;
    private JButton rVisita_button;
    private JButton dijkstra_button;
    private JButton visita_button;
    private JTextField sorgenteLon_text;
    private JTextField destinazioneLon_text;
    private JTextField partenzaLon_text;
    private JLabel sorgente_label;
    private JLabel destinazione_label;
    private JScrollPane scrollPane7;
    private JTextArea visita_area;
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

    private void resetComponent() {
        tratte_area.setText("");
        tratteOutput_area.setText("");
        utenti_area.setText("");
        utentiOutput_area.setText("");
        sorgenteLat_text.setText("");
        sorgenteLon_text.setText("");
        destinazioneLat_text.setText("");
        destinazioneLon_text.setText("");
        dijkstra_area.setText("");
        partenzaLat_text.setText("");
        partenzaLon_text.setText("");
        visita_area.setText("");
        stop_area.setText("");
        idealStop_area.setText("");
    }

    private void resetMark() {
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(-1);

            for (Iterator<Arc> it1 = node.nd_arcs.iterator(); it1.hasNext(); ) {
                Arc arc = it1.next();
                arc.setMark(0);
            }
        }
    }

    private void resetStudent() {
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setNum_studenti(0);
        }
    }

    private void cancel() {
        nodes = null;
        arcs = null;
        nodes_paint = null;
        arcs_paint = null;
        routes = null;
        nodes_students = null;

        reset_item.setEnabled(false);
        cancella_item.setEnabled(false);
        tabbedPane.setEnabled(false);
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

                //studenti
                Route route = n.getRoute();
                int num_studenti = n.getNum_studenti();

                if (num_studenti > 0) {
                    g.setColor(route.getColor());
                    g.fillOval((int) x1, (int) y1, num_studenti, num_studenti);
                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }

                switch (mark) {
                    case -1:
                        g.setColor(Color.black);
                        break;
                    case 0:
                        g.setColor(Color.red);
                        g.setFont(g.getFont().deriveFont(10f));
                        g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                        break;
                    case 1:
                        g.setColor(Color.blue);
                        g.setFont(g.getFont().deriveFont(10f));
                        g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                        break;
                }
            }

            //Disegna tratte
            if (routes != null) {
                for (Iterator<Route> it = routes.iterator(); it.hasNext(); ) {
                    Route r = it.next();

                    g.setColor(r.getColor());

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
