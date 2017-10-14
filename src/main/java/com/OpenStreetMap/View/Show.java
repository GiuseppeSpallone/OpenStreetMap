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

import com.OpenStreetMap.Controller.ControllerImport;
import com.OpenStreetMap.Model.Arc;
import com.OpenStreetMap.Model.Node;
import net.miginfocom.swing.*;

public class Show extends JFrame {
    ControllerImport controllerImport = new ControllerImport();

    private File file_open = null;
    private File file_export = null;
    private File file_open_export = null;

    HashMap<Long, Node> nodes_export = null;
    HashSet<Arc> arcs_export = null;


    public Show() {
        initComponents();
    }

    private void button1ActionPerformed(ActionEvent e) {

        file_open = openFile();

        if (file_open != null) {
            label1.setText(file_open.getPath());
            button2.setEnabled(true);
        }

    }

    private void button2ActionPerformed(ActionEvent e) {

        controllerImport.create(file_open, 5, 2, 100, true, false);

        if (!controllerImport.nodes.isEmpty() || !controllerImport.arcs.isEmpty()) {
            label2.setText("Nodi: " + controllerImport.nodes.size() + " Archi: " + controllerImport.arcs.size());
            button3.setEnabled(true);
        }


    }

    private void button3ActionPerformed(ActionEvent e) {
        file_export = selectPath();
        controllerImport.export(file_export, controllerImport.nodes, controllerImport.arcs);

        if (file_export != null)
            label3.setText("Esportato con successo");

    }

    private void button4ActionPerformed(ActionEvent e) {
        file_open_export = openFile();

        if(file_open_export != null){
            if (readFile(file_open_export))
                panel1.repaint();
        }
    }

    private void panel1PropertyChange(PropertyChangeEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
        button1 = new JButton();
        label1 = new JLabel();
        button2 = new JButton();
        label2 = new JLabel();
        button3 = new JButton();
        button4 = new JButton();
        panel1 = new JPanel() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                disegna(g);
            }

        };
        label3 = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();

        //---- button1 ----
        button1.setText("CARICA");
        button1.addActionListener(e -> button1ActionPerformed(e));

        //---- label1 ----
        label1.setBackground(Color.gray);

        //---- button2 ----
        button2.setText("CREA ");
        button2.setEnabled(false);
        button2.addActionListener(e -> button2ActionPerformed(e));

        //---- button3 ----
        button3.setText("ESPORTA");
        button3.setEnabled(false);
        button3.addActionListener(e -> button3ActionPerformed(e));

        //---- button4 ----
        button4.setText("DISEGNA");
        button4.addActionListener(e -> button4ActionPerformed(e));

        //======== panel1 ========
        {
            panel1.setBackground(Color.white);
            panel1.setBorder(LineBorder.createBlackLineBorder());

            // JFormDesigner evaluation mark
            panel1.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), panel1.getBorder()));
            panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });


            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGap(0, 784, Short.MAX_VALUE)
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGap(0, 391, Short.MAX_VALUE)
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(button3, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(label3, GroupLayout.PREFERRED_SIZE, 281, GroupLayout.PREFERRED_SIZE)))
                                .addGap(24, 24, 24)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(button2, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(button4, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(15, Short.MAX_VALUE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(button2, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(button3, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(button4, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Giuseppe Spallone
    private JButton button1;
    private JLabel label1;
    private JButton button2;
    private JLabel label2;
    private JButton button3;
    private JButton button4;
    private JPanel panel1;
    private JLabel label3;
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
        }else{
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

                int x = Integer.parseInt(vs[1]);
                int y = Integer.parseInt(vs[2]);
                float lat = Float.parseFloat(vs[3]);
                float lon = Float.parseFloat(vs[4]);

                Node n = new Node();

                n.setX(x);
                n.setY(y);
                n.setLat(lat);
                n.setLon(lon);
                n.setIndex(i);

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
                if (nodes_export.size() <= 100) {
                    g.setColor(Color.blue);
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
