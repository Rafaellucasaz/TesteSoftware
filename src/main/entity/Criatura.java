package main.entity;

import main.Constantes;

import java.awt.*;

public class Criatura {

    private int id;
    private double ouro;
    private double posX;
    private Color color;

    public Criatura(int id){
        this.id = id;
        this.ouro = 1000000;
        this.posX = posInicial();
        randomColor();
    }

    public double getOuro() {
        return ouro;
    }

    public void setOuro(double ouro) {
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    private void randomColor(){
        int r = (int)(Math.random() * 256);
        int g = (int)(Math.random() * 256);
        int b = (int)(Math.random() * 256);
        Color color = new Color(r, g, b);
        setColor(color);
    }
    private double posInicial (){
        return Constantes.comecoHorizonte + Math.random() * (Constantes.finalHorizonte - 10);
    }

    public void move(){
        this.posX += (Math.random() * 2) - 1;
        if(this.posX > Constantes.finalHorizonte || this.posX < Constantes.comecoHorizonte){
            this.posX = posInicial();  //redefino a criatura para uma posição aleatória de novo, acredito que pode dá uma dinâmica legal
        }
    }

    public Criatura criaturaMaisProx(Criatura[] criaturas){
        Criatura criaturaMaisProx = null;
        double menorDist = Constantes.finalHorizonte;
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
        this.ouro += Math.ceil(criatura.ouro/2);
        criatura.ouro = Math.floor(criatura.ouro/2);

    }
}
