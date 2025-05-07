package main;

import main.entity.Criatura;

import java.util.Scanner;

public class Simulacao {
      static Scanner sc = new Scanner(System.in);
      static final int nCriaturas = 4;
//    public static final int horizonte = 100;
      static Criatura[] criaturas = new Criatura[nCriaturas];
      static int rodadas = 10;


    public static void main(String[] args) {
        int rodadaAtual = 0;


        for(int i = 0; i < nCriaturas;i++){
            Criatura criatura = new Criatura(i);
            criaturas[i] = criatura;
        }

        while(rodadaAtual < rodadas){
            System.out.println("Rodada atual:" + rodadaAtual);
            for(int i = 0; i<nCriaturas;i++){
                System.out.println("-----------------------------------------");
                System.out.println("Criatura de id: " + criaturas[i].getId());
                System.out.println("Posição: " + criaturas[i].getPosX());
                System.out.println("Ouro: " + criaturas[i].getOuro());
            }

            for(int i = 0; i<nCriaturas;i++){
                criaturas[i].roubar(criaturas);
            }

            for(int i = 0; i<nCriaturas;i++){
                criaturas[i].move();

            }

            System.out.println("\n\nAperte enter para avançar a rodada!");
            sc.nextLine();
            rodadaAtual++;
        }

    }
}
