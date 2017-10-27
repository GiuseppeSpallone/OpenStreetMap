package com.OpenStreetMap.View;

import com.OpenStreetMap.Controller.Database;
import com.mongodb.DB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    private String username = null;
    private String password = null;
    Database database = new Database();

    private JTextField textField1;
    private JButton ENTRAButton;
    private JPasswordField passwordField1;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;


    public Home() {
        ENTRAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean connectUser = false;
                username = textField1.getText();
                password = passwordField1.getText();

                DB db = database.connectDB("localhost", 27017, "StreetMap");

                if (db != null) {
                    System.out.println("Connesso al database");
                    if (username != null || password != null) {

                        connectUser = database.connectUser(db, username, password);

                        if (connectUser) {
                            System.out.println("Accesso al database");
                        } else {
                            JOptionPane.showMessageDialog(null, "Username o password errati");
                            System.out.println("Username o password errati");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Inserisci username e password");
                        System.out.println("Inserisci username e password");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Database non connesso");
                    System.out.println("Database non connesso");
                }
            }
        });
    }

    public static void main(String args[]) {
        JFrame jFrame = new JFrame("Stop Bus");
        jFrame.setContentPane(new Home().panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(700, 500));
        jFrame.pack();
        jFrame.setVisible(true);

    }


    /**
     * @noinspection ALL
     */

    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

}

