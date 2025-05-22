package main;

import main.view.Menu;
import main.view.Simulacao;
import javax.swing.*;
import java.awt.*;

public class Main  {


    public static void main(String[] args) {

        JFrame frame = new JFrame("Simulação");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constantes.larguraTela, Constantes.alturaTela);
        frame.setResizable(false);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        Simulacao telaSimulacao = new Simulacao(mainPanel, cardLayout);
        Menu telaInicial = new Menu(telaSimulacao,mainPanel, cardLayout);

        mainPanel.add(telaInicial, "telaInicial");
        mainPanel.add(telaSimulacao, "telaSimulacao");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

}
