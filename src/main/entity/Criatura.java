package main.entity;

import main.Constantes;

import java.awt.*;
import java.util.List;

public class Criatura {

    private int id;
    private double ouro;
    private double posX;
    private Color color;
    private Tipos tipo;

    public Criatura(int id, Tipos tipo, double ouro) {
        this.tipo = tipo;
        setId(id);
        setOuro(ouro);
        setPosX(posInicial());
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
        if (posX > Constantes.finalHorizonte || posX < Constantes.comecoHorizonte) {
            this.posX = posInicial();
        } else {
            this.posX = posX;
        }
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

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    private void randomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        setColor(new Color(r, g, b));
    }

    private double posInicial() {
        return Constantes.comecoHorizonte + Math.random() * (Constantes.finalHorizonte - 10);
    }

    public void move() {
        double novaPosX = this.posX + (Math.random() * 2) - 1;
        setPosX(novaPosX);
    }

    public Criatura criaturaMaisProx(List<Criatura> criaturas) {
        Criatura criaturaMaisProx = null;
        double menorDist = Constantes.finalHorizonte;
        double distAtual;

        for (Criatura c : criaturas) {
            if (c.id != this.id) {
                distAtual = Math.abs(c.posX - this.posX);
                if (distAtual < menorDist) {
                    criaturaMaisProx = c;
                    menorDist = distAtual;
                }
            }
        }
        //distância mínima para a colisão
        double dist;
        if(this.getTipo() == Tipos.guardiao){
            dist = 3;
        }
        else{
            dist = 1;
        }
        if (menorDist <= dist ) {
            if(this.getTipo() == Tipos.minion && criaturaMaisProx.getTipo() == Tipos.minion){
                criaturas.remove(this);
                criaturas.remove(criaturaMaisProx);
                criaturas.add(new Criatura(criaturaMaisProx.getId(), Tipos.cluster, this.getOuro() + criaturaMaisProx.getOuro()));

            }
            else if(this.getTipo() == Tipos.guardiao && criaturaMaisProx.getTipo() == Tipos.cluster){
                this.setOuro(criaturaMaisProx.getOuro());
                criaturas.remove(criaturaMaisProx);
            }
            return null;
        }

        return criaturaMaisProx;
    }

    public void roubar(List<Criatura> criaturas) {
        Criatura criatura = criaturaMaisProx(criaturas);
        if (criatura == null) {
            return; // Cluster formado, não rouba
        }
        if(criatura.getTipo()!= Tipos.guardiao){
            this.ouro += Math.ceil(criatura.ouro / 2);
            criatura.ouro = Math.floor(criatura.ouro / 2);
        }

    }
}
