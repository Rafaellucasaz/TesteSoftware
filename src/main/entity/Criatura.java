package main.entity;

import main.Simulacao;

public class Criatura {

    private int id;
    private int ouro;
    private double posX;
    private final int horizonte = 20;

    public Criatura(int id){
        this.id = id;
        this.ouro = 1000000;
        this.posX = posInicial();
    }

    public int getOuro() {
        return ouro;
    }

    public void setOuro(int ouro) {
        this.ouro = ouro;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double posInicial (){
         return Math.random() * horizonte;
    }

    public void move(){
        this.posX += (Math.random() * 2) - 1;
        if(this.posX > horizonte || this.posX < 0){
            this.posX = posInicial();  //redifino a criatura para uma posição aleatória de novo, acredito que pode dá uma dinâmica legal
        }
    }

    public Criatura criaturaMaisProx(Criatura[] criaturas){
        Criatura criaturaMaisProx = null;
        double menorDist = 101;
        double distAtual;
        for(int i = 0; i < criaturas.length;i++){
            if(criaturas[i].id != this.id){
                distAtual = Math.abs(criaturas[i].posX - this.posX);
                if(distAtual < menorDist){
                    criaturaMaisProx = criaturas[i];
                    menorDist = distAtual;
                }
            }
        }
        return criaturaMaisProx;
    }
    public void roubar(Criatura[] criaturas){
        Criatura criatura = criaturaMaisProx(criaturas);
        criatura.ouro = criatura.ouro/2;
        this.ouro += criatura.ouro;
    }
}
