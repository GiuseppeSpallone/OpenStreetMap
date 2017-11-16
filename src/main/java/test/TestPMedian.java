package test;

import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TestPMedian {

    public static void main(String[] args) {

        Parameter parameter1 = setParameters(20, 100, 1);
        Parameter parameter2 = setParameters(20, 100, 2);
        Parameter parameter3 = setParameters(20, 100, 3);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);

        for (int i = 0; i < parameters.size(); i++) {
            run(parameters.get(i));
        }

        File file = selectPath();
        export(file, parameters);

    }

    private static Parameter setParameters(int s, int f, int p) {
        //insieme studenti
        Random r = new Random(1);

        int studenti[] = new int[s];

        for (int i = 0; i < s; i++) {
            studenti[i] = r.nextInt(10) + 1;
        }

        //insieme fermate candidate
        int fermate[] = new int[f];

        //distranze studente - fermata candidata
        int distanze[][] = new int[s][f];

        for (int i = 0; i < s; i++) {
            int d = r.nextInt(50) + 10;

            int k = r.nextInt(10) - 4;

            if (k == 0) {
                k = -1;
            }

            for (int j = 0; j < f; j++) {
                distanze[i][j] = r.nextInt(100) + 10;
                //distanze[i][j] = d;
                d = d + k;
                if ((d + k) < 0) {
                    k = k * -1;
                    d = d + (2 * k);
                }

                if (r.nextInt(100) < 10) {
                    k = r.nextInt(10) - 4;
                }
            }
        }

        Parameter parameter = new Parameter();
        parameter.setStudenti(studenti);
        parameter.setFermate(fermate);
        parameter.setDistanze(distanze);
        parameter.setP(p);

        return parameter;
    }

    private static void run(Parameter parameter) {
        int[] studenti = parameter.getStudenti();
        int[] fermate = parameter.getFermate();
        int[][] distanze = parameter.getDistanze();
        int p = parameter.getP();

        try {

            IloCplex model = new IloCplex();

            /**
             * insieme di variabili*
             */
            IloNumVar[] x = model.boolVarArray(fermate.length);
            IloNumVar[][] y = new IloNumVar[studenti.length][];

            for (int i = 0; i < studenti.length; i++) {

                y[i] = model.boolVarArray(fermate.length);
            }

            /**
             * funzione obiettivo*
             */
            IloLinearNumExpr function = model.linearNumExpr();

            for (int i = 0; i < studenti.length; i++) {
                for (int j = 0; j < fermate.length; j++) {
                    function.addTerm(y[i][j], distanze[i][j] * studenti[i]);
                }
            }
            model.addMinimize(function);

            /**
             * vincoli*
             */
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

            /**
             * soluzione*
             */
            if (model.solve()) {
                parameter.setValue(model.getObjValue());
                System.out.println("Solution status: " + model.getStatus());
                System.out.println("Solution value: " + model.getObjValue());

                for (int i = 0; i < fermate.length; i++) {
                    if (model.getValue(x[i]) > 0.5) {
                        System.out.print("fermata " + (i + 1) + " ");
                    }
                }
                System.out.println("");
                System.out.println("Solution status: " + model.getStatus());

            } else {
                System.out.println("Solution status: " + model.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void export(File file, ArrayList<Parameter> parameters) {

        FileWriter outFile = null;

        try {
            outFile = new FileWriter(file);
            PrintWriter out = new PrintWriter(outFile);

            out.println("V   \tS   \tN   \tF");

            for (int i = 0; i < parameters.size(); i++) {
                double valore = (parameters.get(i).getValue() / parameters.get(i).getStudenti().length);
                int numStudenti = parameters.get(i).getStudenti().length;
                int numNodi = parameters.get(i).getFermate().length;
                int numFermate = parameters.get(i).getP();
                out.println(valore + "\t" + numStudenti + "\t" + numNodi + "\t" + numFermate);
                out.println("---------------------------------------------");

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
