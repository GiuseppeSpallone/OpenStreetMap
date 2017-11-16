package com.Test;

public class Parameter {
    private double value;
    private long time;
    
    private int seedRandom;
    private int[] nodiStudenti;
    private int minNumStudenti; //domanda minim per ogni nodo studenti
    private int maxNumStudenti; //domanda massima per ogni nodo studenti
    private int numTotStudenti;
    private int[] numFermateCandidate; 
    private int[][] distanze;
    private int minDistanza; //distanza minima tra nodo studenti e fermata candidata
    private int maxDistanza; //distanza massima tra nodo studenti e fermata candidata
    private int numFermateDaFissare;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSeedRandom() {
        return seedRandom;
    }

    public void setSeedRandom(int seedRandom) {
        this.seedRandom = seedRandom;
    }

    public int[] getNodiStudenti() {
        return nodiStudenti;
    }

    public void setNodiStudenti(int[] nodiStudenti) {
        this.nodiStudenti = nodiStudenti;
    }

    public int getMinNumStudenti() {
        return minNumStudenti;
    }

    public void setMinNumStudenti(int minNumStudenti) {
        this.minNumStudenti = minNumStudenti;
    }

    public int getMaxNumStudenti() {
        return maxNumStudenti;
    }

    public void setMaxNumStudenti(int maxNumStudenti) {
        this.maxNumStudenti = maxNumStudenti;
    }

    public int getNumTotStudenti() {
        return numTotStudenti;
    }

    public void setNumTotStudenti(int numTotStudenti) {
        this.numTotStudenti = numTotStudenti;
    }

    public int[] getNumFermateCandidate() {
        return numFermateCandidate;
    }

    public void setNumFermateCandidate(int[] numFermateCandidate) {
        this.numFermateCandidate = numFermateCandidate;
    }

    public int[][] getDistanze() {
        return distanze;
    }

    public void setDistanze(int[][] distanze) {
        this.distanze = distanze;
    }

    public int getMinDistanza() {
        return minDistanza;
    }

    public void setMinDistanza(int minDistanza) {
        this.minDistanza = minDistanza;
    }

    public int getMaxDistanza() {
        return maxDistanza;
    }

    public void setMaxDistanza(int maxDistanza) {
        this.maxDistanza = maxDistanza;
    }

    public int getNumFermateDaFissare() {
        return numFermateDaFissare;
    }

    public void setNumFermateDaFissare(int numFermateDaFissare) {
        this.numFermateDaFissare = numFermateDaFissare;
    }

    
}
