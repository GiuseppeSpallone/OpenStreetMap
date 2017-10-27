package com.OpenStreetMap.Controller;

import java.util.ArrayList;

public class ControllerStudenti {

    public ArrayList<float[]> readArea(String stringAreaText) {
        String arraySting[] = null;
        arraySting = splitta(stringAreaText);
        ArrayList<float[]> lat_lon_studenti = new ArrayList<>();

        for (int i = 0; i < arraySting.length; i++) {

            if (arraySting[i].equals("#")) {

                float latitudine = Float.parseFloat(arraySting[i + 1]);
                float longitudine = Float.parseFloat(arraySting[i + 2]);
                float num_studenti = Float.parseFloat(arraySting[i + 3]);

                lat_lon_studenti.add(new float[]{latitudine, longitudine, num_studenti});

            }
        }

        /*for (int i = 0; i < lat_lon_studenti.size(); i++) {
            System.out.println("Utente " + (i+1));
            System.out.println(lat_lon_studenti.get(i)[0] + "," +  lat_lon_studenti.get(i)[1] + " num:" +  lat_lon_studenti.get(i)[2]);
        }*/

        return lat_lon_studenti;
    }

    private static String[] splitta(String s) {
        s = s.trim().replaceAll("\t", " ");
        s = s.trim().replaceAll("\n", " ");
        while (s.contains("  ")) {
            s = s.replace("  ", " ");
        }
        return s.split(" ");
    }
}
