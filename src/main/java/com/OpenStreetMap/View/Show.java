/*
 * Created by JFormDesigner on Wed Oct 11 17:43:14 CEST 2017
 */

package com.OpenStreetMap.View;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.OpenStreetMap.Controller.*;
import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class Show extends JFrame {
    ControllerImport controllerImport = new ControllerImport();
    ControllerExport controllerExport = new ControllerExport();
    ControllerDatabase controllerDatabase = new ControllerDatabase();
    ControllerFileMap controllerFileMap = new ControllerFileMap();
    Visits visits = new Visits();
    Algorithms algorithms = new Algorithms();

    private DB dbStreetMap = null;
    private HashMap<Long, Node> nodes = null;
    private HashSet<Arc> arcs = null;
    private HashMap<Long, Node> nodes_paint = null;
    private HashSet<Arc> arcs_paint = null;

    private File file = null;

    public Show() {
        initComponents();
    }

    private void menuItem2ActionPerformed(ActionEvent e) {
        dbStreetMap = controllerDatabase.connectDB("localhost", 27017, "StreetMap");
        if (dbStreetMap != null)
            menuItem1.setEnabled(true);
    }

    private void menuItem1ActionPerformed(ActionEvent e) {
        DBCollection collectionNode = controllerDatabase.getCollection(dbStreetMap, "Node");
        DBCollection collectionWay = controllerDatabase.getCollection(dbStreetMap, "Way");
        DBCollection collectionArc = controllerDatabase.getCollection(dbStreetMap, "Arc");

        controllerDatabase.insertNodesDB(collectionNode, controllerImport.getNodes());
        controllerDatabase.insertWaysDB(collectionWay, controllerImport.getWays());
        controllerDatabase.insertArcsDB(collectionArc, controllerImport.getArcs());
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

                file = selectPath();
                if(esportaMap(file)){
                    Object[] options = {"Si", "No"};
                    int option = JOptionPane.showOptionDialog(null, "Mappa esportata, disegnare mappa?", null, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    if (option == 0) {
                        disegnaMap(file);
                    }
                } else{
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

        Node n = getNodoVicino(x, y);
        JOptionPane.showMessageDialog(null, "index: " + n.getIndex() + "; id: " + n.getId() + "; " + n.getLat() + "," + n.getLon());
        System.out.println("index: " + n.getIndex() + "; id: " + n.getId() + "; " + n.getLat() + "," + n.getLon());
    }

    private void menuItem10ActionPerformed(ActionEvent e) {
        Node startingNode = Node.randomNode(nodes);
        visits.visita(nodes, startingNode);
        panel1.repaint();
    }

    private void menuItem9ActionPerformed(ActionEvent e) {
        JPanel panelVisit = new OptionPaneVisit();
        JOptionPane.showMessageDialog(null, panelVisit, null, JOptionPane.INFORMATION_MESSAGE);
        //Long s = 1567597028L;
        //Long d = 2314745275L;
        //Node sorgente = nodes_export.get(s);
        //Node destinazione = nodes_export.get(d);
        Node sorgente = Node.randomNode(nodes);
        Node destinazione = Node.randomNode(nodes);
        algorithms.dijkstra(sorgente, destinazione, nodes);
        panel1.repaint();
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
        for (Iterator<Node> it = nodes.values().iterator(); it.hasNext(); ) {
            Node node = it.next();
            node.setMark(-1);
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

    private void menuItem6ActionPerformed(ActionEvent e) {
        // TODO add your code here
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
                    .addGap(0, 986, Short.MAX_VALUE)
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGap(0, 592, Short.MAX_VALUE)
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
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
    private JMenu menu1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem1;
    private JPanel panel1;
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

    private Node getNodoVicino(int x, int y) {

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
