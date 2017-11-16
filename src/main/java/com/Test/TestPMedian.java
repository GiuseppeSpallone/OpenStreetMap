package com.Test;

import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TestPMedian {

    public static void main(String[] args) {
        Controller controller = new Controller();

        ArrayList<Parameter> parameters = controller.nodiDomanda();

        for (int i = 0; i < parameters.size(); i++) {
            System.out.println("***** Parameter " + (i + 1) + " *****");
            run(parameters.get(i));
        }

        File file = selectPath();
        export(file, parameters);

    }

    private static void run(Parameter parameter) {
        long startTime = System.currentTimeMillis();

        int[] studenti = parameter.getNodiStudenti();
        int[] fermate = parameter.getNumFermateCandidate();
        int[][] distanze = parameter.getDistanze();
        int p = parameter.getNumFermateDaFissare();

        try {

            IloCplex model = new IloCplex();

            //variabili
            IloNumVar[] x = model.boolVarArray(fermate.length);
            IloNumVar[][] y = new IloNumVar[studenti.length][];

            for (int i = 0; i < studenti.length; i++) {

                y[i] = model.boolVarArray(fermate.length);
            }

            //funzione obiettivo
            IloLinearNumExpr function = model.linearNumExpr();

            for (int i = 0; i < studenti.length; i++) {
                for (int j = 0; j < fermate.length; j++) {
                    function.addTerm(y[i][j], distanze[i][j] * studenti[i]);
                }
            }
            model.addMinimize(function);

            //vincolo 1
            IloLinearNumExpr v = model.linearNumExpr();
            for (int i = 0; i < fermate.length; i++) {
                v.addTerm(x[i], 1);
            }
            model.addEq(v, p);

            //vincolo 2
            for (int i = 0; i < studenti.length; i++) {
                IloLinearNumExpr v1 = model.linearNumExpr();

                for (int j = 0; j < fermate.length; j++) {
                    v1.addTerm(y[i][j], 1);
                }
                model.addEq(v1, 1);
            }

            //vincolo 3
            for (int i = 0; i < studenti.length; i++) {

                for (int j = 0; j < fermate.length; j++) {
                    model.addLe(y[i][j], x[j]);
                }
            }

            //soluzione
            if (model.solve()) {
                parameter.setValue(model.getObjValue());
                System.out.println("Solution status: " + model.getStatus());
                System.out.println("Solution value: " + model.getObjValue());

            } else {
                System.out.println("Solution status: " + model.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long time = (endTime - startTime);
        parameter.setTime(time);
    }

    public static void export(File file, ArrayList<Parameter> parameters) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            for (int i = 0; i < parameters.size(); i++) {
                Parameter parameter = parameters.get(i);

                int valore = (int) (parameter.getValue() / parameter.getNumTotStudenti());
                long time = parameter.getTime();
                int seed = parameter.getSeedRandom();
                int domanda = parameter.getNodiStudenti().length;
                int minDomanda = parameter.getMinNumStudenti();
                int maxDomanda = parameter.getMaxNumStudenti();
                int totDomanda = parameter.getNumTotStudenti();
                int offerta = parameter.getNumFermateCandidate().length;
                int minDistanza = parameter.getMinDistanza();
                int maxDistanza = parameter.getMaxDistanza();
                int p = parameter.getNumFermateDaFissare();

                out.println(valore + " \t " + time + " \t " + seed + " \t " + domanda + " \t " + " \t " + minDomanda + " \t " + maxDomanda + " \t " + totDomanda + " \t " + offerta + " \t " + " \t " + minDistanza + " \t " + maxDistanza + " \t " + p);

            }

            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                outFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static File selectPath() {
        FileNameExtensionFilter grfFilter;

        grfFilter = new FileNameExtensionFilter("txt files (*txt)", "txt");

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
}
