package com.Test;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    // seed, nodi domanda, range domanda, nodi offerta, range distanza, p

    public ArrayList<Parameter> p() {

        Parameter parameter1 = setParameters(1, 100, 5, 150, 200, 5, 150, 1);
        Parameter parameter2 = setParameters(1, 100, 5, 150, 200, 5, 150, 2);
        Parameter parameter3 = setParameters(1, 100, 5, 150, 200, 5, 150, 3);
        Parameter parameter4 = setParameters(1, 100, 5, 150, 200, 5, 150, 4);
        Parameter parameter5 = setParameters(1, 100, 5, 150, 200, 5, 150, 5);
        Parameter parameter6 = setParameters(1, 100, 5, 150, 200, 5, 150, 6);
        Parameter parameter7 = setParameters(1, 100, 5, 150, 200, 5, 150, 7);
        Parameter parameter8 = setParameters(1, 100, 5, 150, 200, 5, 150, 8);
        Parameter parameter9 = setParameters(1, 100, 5, 150, 200, 5, 150, 9);
        Parameter parameter10 = setParameters(1, 100, 5, 150, 200, 5, 150, 10);
        Parameter parameter11 = setParameters(1, 100, 5, 150, 200, 5, 150, 50);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);
        parameters.add(parameter4);
        parameters.add(parameter5);
        parameters.add(parameter6);
        parameters.add(parameter7);
        parameters.add(parameter8);
        parameters.add(parameter9);
        parameters.add(parameter10);
        parameters.add(parameter11);

        return parameters;
    }

    public ArrayList<Parameter> nodiOfferta() {

        Parameter parameter1 = setParameters(1, 100, 5, 50, 25, 5, 100, 1);
        Parameter parameter2 = setParameters(1, 100, 5, 50, 50, 5, 100, 1);
        Parameter parameter3 = setParameters(1, 100, 5, 50, 75, 5, 100, 1);
        Parameter parameter4 = setParameters(1, 100, 5, 50, 100, 5, 100, 1);
        Parameter parameter5 = setParameters(1, 100, 5, 50, 125, 5, 100, 1);
        Parameter parameter6 = setParameters(1, 100, 5, 50, 150, 5, 100, 1);
        Parameter parameter7 = setParameters(1, 100, 5, 50, 175, 5, 100, 1);
        Parameter parameter8 = setParameters(1, 100, 5, 50, 200, 5, 100, 1);
        Parameter parameter9 = setParameters(1, 100, 5, 50, 225, 5, 100, 1);
        Parameter parameter10 = setParameters(1, 100, 5, 50, 250, 5, 100, 1);
        Parameter parameter11 = setParameters(1, 100, 5, 50, 1000, 5, 100, 1);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);
        parameters.add(parameter4);
        parameters.add(parameter5);
        parameters.add(parameter6);
        parameters.add(parameter7);
        parameters.add(parameter8);
        parameters.add(parameter9);
        parameters.add(parameter10);
        parameters.add(parameter11);

        return parameters;
    }

    public ArrayList<Parameter> nodiDomanda() {

        Parameter parameter1 = setParameters(1, 25, 5, 50, 100, 5, 100, 1);
        Parameter parameter2 = setParameters(1, 50, 5, 50, 100, 5, 100, 1);
        Parameter parameter3 = setParameters(1, 75, 5, 50, 100, 5, 100, 1);
        Parameter parameter4 = setParameters(1, 100, 5, 50, 100, 5, 100, 1);
        Parameter parameter5 = setParameters(1, 125, 5, 50, 100, 5, 100, 1);
        Parameter parameter6 = setParameters(1, 150, 5, 50, 100, 5, 100, 1);
        Parameter parameter7 = setParameters(1, 175, 5, 50, 100, 5, 100, 1);
        Parameter parameter8 = setParameters(1, 200, 5, 50, 100, 5, 100, 1);
        Parameter parameter9 = setParameters(1, 225, 5, 50, 100, 5, 100, 1);
        Parameter parameter10 = setParameters(1, 250, 5, 50, 100, 5, 100, 1);
        Parameter parameter11 = setParameters(1, 1000, 5, 50, 100, 5, 100, 1);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);
        parameters.add(parameter4);
        parameters.add(parameter5);
        parameters.add(parameter6);
        parameters.add(parameter7);
        parameters.add(parameter8);
        parameters.add(parameter9);
        parameters.add(parameter10);
        parameters.add(parameter11);

        return parameters;
    }

    public ArrayList<Parameter> domandaMax() {

        Parameter parameter1 = setParameters(1, 50, 5, 10, 100, 5, 100, 1);
        Parameter parameter2 = setParameters(1, 50, 5, 20, 100, 5, 100, 1);
        Parameter parameter3 = setParameters(1, 50, 5, 30, 100, 5, 100, 1);
        Parameter parameter4 = setParameters(1, 50, 5, 40, 100, 5, 100, 1);
        Parameter parameter5 = setParameters(1, 50, 5, 50, 100, 5, 100, 1);
        Parameter parameter6 = setParameters(1, 50, 5, 60, 100, 5, 100, 1);
        Parameter parameter7 = setParameters(1, 50, 5, 70, 100, 5, 100, 1);
        Parameter parameter8 = setParameters(1, 50, 5, 80, 100, 5, 100, 1);
        Parameter parameter9 = setParameters(1, 50, 5, 90, 100, 5, 100, 1);
        Parameter parameter10 = setParameters(1, 50, 5, 100, 100, 5, 100, 1);
        Parameter parameter11 = setParameters(1, 50, 5, 1000, 100, 5, 100, 1);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);
        parameters.add(parameter4);
        parameters.add(parameter5);
        parameters.add(parameter6);
        parameters.add(parameter7);
        parameters.add(parameter8);
        parameters.add(parameter9);
        parameters.add(parameter10);
        parameters.add(parameter11);

        return parameters;
    }

    public ArrayList<Parameter> distanzeMax() {

        Parameter parameter1 = setParameters(1, 50, 5, 50, 100, 5, 25, 1);
        Parameter parameter2 = setParameters(1, 50, 5, 50, 100, 5, 50, 1);
        Parameter parameter3 = setParameters(1, 50, 5, 50, 100, 5, 75, 1);
        Parameter parameter4 = setParameters(1, 50, 5, 50, 100, 5, 100, 1);
        Parameter parameter5 = setParameters(1, 50, 5, 50, 100, 5, 125, 1);
        Parameter parameter6 = setParameters(1, 50, 5, 50, 100, 5, 150, 1);
        Parameter parameter7 = setParameters(1, 50, 5, 50, 100, 5, 175, 1);
        Parameter parameter8 = setParameters(1, 50, 5, 50, 100, 5, 200, 1);
        Parameter parameter9 = setParameters(1, 50, 5, 50, 100, 5, 225, 1);
        Parameter parameter10 = setParameters(1, 50, 5, 50, 100, 5, 250, 1);
        Parameter parameter11 = setParameters(1, 50, 5, 50, 100, 5, 1000, 1);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);
        parameters.add(parameter4);
        parameters.add(parameter5);
        parameters.add(parameter6);
        parameters.add(parameter7);
        parameters.add(parameter8);
        parameters.add(parameter9);
        parameters.add(parameter10);
        parameters.add(parameter11);

        return parameters;
    }

    private static Parameter setParameters(int seedRandom, int numNodiStudenti, int minNumStudenti, int maxNumStudenti, int numFermateCandidate, int minDistanza, int maxDistanza, int numFermateDaFissare) {

        Random r = new Random(seedRandom);

        //insieme studenti
        int numTotStudenti = 0;
        int nodiStudenti[] = new int[numNodiStudenti];
        for (int i = 0; i < numNodiStudenti; i++) {
            int numStudenti = r.nextInt(maxNumStudenti) + minNumStudenti;
            nodiStudenti[i] = numStudenti;
            numTotStudenti += numStudenti;
            System.out.println("studenti: " + numStudenti);
        }

        //insieme fermate candidate
        int fermateCandidate[] = new int[numFermateCandidate];

        //insieme distanze studente - fermata candidata casuale
        int distanze[][] = new int[numNodiStudenti][numFermateCandidate];

        for (int i = 0; i < distanze.length; i++) {
            for (int j = 0; j < numFermateCandidate; j++) {
                int distanza = r.nextInt(maxDistanza) + minDistanza;
                distanze[i][j] = distanza;
                System.out.println("distanza: " + distanza);
            }
        }

//        //insieme distanze studente - fermata candidata con variazione        
//        for (int i = 0; i < numNodiStudenti; i++) {
//            int d = r.nextInt(50) + 10;
//
//            int k = r.nextInt(10) - 4;
//
//            if (k == 0) {
//                k = -1;
//            }
//
//            for (int j = 0; j < numFermateCandidate; j++) {
//                int distanza = r.nextInt(maxDistanza) + minDistanza;
//                distanze[i][j] = distanza;
//                System.out.println("distanza: " + distanza);
//                d = d + k;
//                if ((d + k) < 0) {
//                    k = k * -1;
//                    d = d + (2 * k);
//                }
//
//                if (r.nextInt(100) < 10) {
//                    k = r.nextInt(10) - 4;
//                }
//            }
//        }
        Parameter parameter = new Parameter();
        parameter.setSeedRandom(seedRandom);
        parameter.setNodiStudenti(nodiStudenti);
        parameter.setMinNumStudenti(minNumStudenti);
        parameter.setMaxNumStudenti(maxNumStudenti);
        parameter.setNumTotStudenti(numTotStudenti);
        parameter.setNumFermateCandidate(fermateCandidate);
        parameter.setDistanze(distanze);
        parameter.setMinDistanza(minDistanza);
        parameter.setMaxDistanza(maxDistanza);
        parameter.setNumFermateDaFissare(numFermateDaFissare);

        return parameter;
    }
}
