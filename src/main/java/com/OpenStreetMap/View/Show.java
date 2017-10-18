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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.OpenStreetMap.Controller.ControllerDatabase;
import com.OpenStreetMap.Controller.ControllerImport;
import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import jdk.nashorn.internal.scripts.JD;
import net.miginfocom.swing.*;

public class Show extends JFrame {
    ControllerImport controllerImport = new ControllerImport();
    ControllerDatabase controllerDatabase = new ControllerDatabase();

    DB dbStreetMap = null;

    private File file_open = null;
    private File file_export = null;
    private File file_open_export = null;

    HashMap<Long, Node> nodes_export = null;
    HashSet<Arc> arcs_export = null;


    public Show() {
        initComponents();
    }


    private void panel1PropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
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

        controllerDatabase.insertNodesDB(collectionNode, controllerImport.nodes);
        controllerDatabase.insertWaysDB(collectionWay, controllerImport.ways);
        controllerDatabase.insertArcsDB(collectionArc, controllerImport.arcs);
    }

    private void menuItem3ActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void menuItem4ActionPerformed(ActionEvent e) {
        file_open = openFile();

        if (file_open != null) {
            //label1.setText(file_open.getPath());
            menuItem5.setEnabled(true);
        }
    }

    private void menuItem5ActionPerformed(ActionEvent e) {
        controllerImport.create(file_open, 5, 2, 100, true, false);

        if (!controllerImport.nodes.isEmpty() || !controllerImport.arcs.isEmpty()) {
            //label2.setText("Nodi: " + controllerImport.nodes.size() + " Archi: " + controllerImport.arcs.size());
            menuItem6.setEnabled(true);
        }
    }

    private void menuItem6ActionPerformed(ActionEvent e) {
        file_export = selectPath();
        controllerImport.export(file_export, controllerImport.nodes, controllerImport.arcs);

        if (file_export != null) {
        }
        //label3.setText("Esportato con successo");
    }

    private void menuItem7ActionPerformed(ActionEvent e) {
        file_open_export = openFile();

        if (file_open_export != null) {
            if (readFile(file_open_export))
                panel1.repaint();
        }
    }

    private void panel1MouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        Node n = getNodoVicino(x, y);
        System.out.println("index: " + n.getIndex() + "; id: " + n.getId() + "; " + n.getLat() + "," + n.getLon());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
        menuBar1 = new JMenuBar();
        menu2 = new JMenu();
        menuItem4 = new JMenuItem();
        menuItem5 = new JMenuItem();
        menuItem6 = new JMenuItem();
        menuItem7 = new JMenuItem();
        menuItem3 = new JMenuItem();
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
                menuItem4.setText("Carica");
                menuItem4.addActionListener(e -> menuItem4ActionPerformed(e));
                menu2.add(menuItem4);

                //---- menuItem5 ----
                menuItem5.setText("Crea");
                menuItem5.setEnabled(false);
                menuItem5.addActionListener(e -> menuItem5ActionPerformed(e));
                menu2.add(menuItem5);

                //---- menuItem6 ----
                menuItem6.setText("Esporta");
                menuItem6.setEnabled(false);
                menuItem6.addActionListener(e -> menuItem6ActionPerformed(e));
                menu2.add(menuItem6);

                //---- menuItem7 ----
                menuItem7.setText("Disegna");
                menuItem7.addActionListener(e -> menuItem7ActionPerformed(e));
                menu2.add(menuItem7);

                //---- menuItem3 ----
                menuItem3.setText("Esci");
                menuItem3.addActionListener(e -> menuItem3ActionPerformed(e));
                menu2.add(menuItem3);
            }
            menuBar1.add(menu2);

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
                    .addGap(0, 594, Short.MAX_VALUE)
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
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JMenuItem menuItem7;
    private JMenuItem menuItem3;
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

    private boolean readFile(File file) {
        nodes_export = new HashMap<>();
        arcs_export = new HashSet<>();

        FileReader inFile = null;

        try {
            inFile = new FileReader(file);
            BufferedReader in = new BufferedReader(inFile);
            String vs[] = null;
            String s = in.readLine();

            vs = splitta(s);

            int nVertices = Integer.parseInt(vs[0]);
            int nEdges = Integer.parseInt(vs[1]);

            for (int i = 0; i < nVertices; i++) {
                s = in.readLine();
                vs = splitta(s);

                long id = Long.parseLong(vs[1]);
                int x = Integer.parseInt(vs[2]);
                int y = Integer.parseInt(vs[3]);
                float lat = Float.parseFloat(vs[4]);
                float lon = Float.parseFloat(vs[5]);
                int mark = Integer.parseInt(vs[6]);

                Node n = new Node();

                n.setId(id);
                n.setX(x);
                n.setY(y);
                n.setLat(lat);
                n.setLon(lon);
                n.setIndex(i);
                n.setMark(mark);

                nodes_export.put(new Long(i), n);
            }

            for (int i = 0; i < nEdges; i++) {
                s = in.readLine();
                vs = splitta(s);

                long from = Integer.parseInt(vs[0]);
                long to = Integer.parseInt(vs[1]);
                double length = Float.parseFloat(vs[2]);

                Arc a = new Arc(nodes_export.get(from), nodes_export.get(to));
                a.setLength(length);
                a.setIndex(i);

                arcs_export.add(a);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                inFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }

    private Node getNodoVicino(int x, int y) {

        Node ndOut = null;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        if (nodes_export != null && arcs_export != null) {
            for (Node n : nodes_export.values()) {
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
            for (Node n : nodes_export.values()) {
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


    private void disegna(Graphics gg) {

        Graphics2D g = (Graphics2D) gg;
        g.setColor(Color.black);

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        if (nodes_export != null && arcs_export != null) {
            for (Node n : nodes_export.values()) {
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
            for (Arc arc : arcs_export) {
                double x1 = (arc.getFrom().getX() - minX * 1.0) * rap;
                double y1 = (arc.getFrom().getY() - minY * 1.0) * rap;
                double x2 = (arc.getTo().getX() - minX * 1.0) * rap;
                double y2 = (arc.getTo().getY() - minY * 1.0) * rap;

                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            }

            //Stampa nodi
            for (Node n : nodes_export.values()) {
                double x1 = (n.getX() - minX * 1.0) * rap;
                double y1 = (n.getY() - minY * 1.0) * rap;

                /*if(n.getMark() == 0){
                    g.setColor(Color.black);
                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }*/
                if (n.getMark() > 0) {
                    g.setColor(Color.blue);
                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                } else {
                    g.setColor(Color.red);
                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }
/*
                if (nodes_export.size() <= 100) {
                    g.setColor(Color.blue);
                    g.setFont(g.getFont().deriveFont(10f));
                    g.drawString("" + n.getIndex(), (int) x1, (int) y1);
                }*/
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
