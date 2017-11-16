package test;

public class Parameter {
    private double value;
    
    private int[] studenti;
    private int[] fermate; 
    private int[][] distanze;
    private int p;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    

    public int[] getStudenti() {
        return studenti;
    }

    public void setStudenti(int[] studenti) {
        this.studenti = studenti;
    }

    public int[] getFermate() {
        return fermate;
    }

    public void setFermate(int[] fermate) {
        this.fermate = fermate;
    }

    public int[][] getDistanze() {
        return distanze;
    }

    public void setDistanze(int[][] distanze) {
        this.distanze = distanze;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
    
    
}
