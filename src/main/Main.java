package main;

import main.dao.UsuarioDao;
import main.util.Db;
import main.view.*;
import main.view.Menu;

import javax.swing.*;
import java.awt.*;

public class Main  {

    private static UsuarioDao usuarioLogado = null;

    public static void main(String[] args) {

        Db.createDatabase();
        JFrame frame = new JFrame("Simulação");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constantes.larguraTela, Constantes.alturaTela);
        frame.setResizable(false);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        Simulacao telaSimulacao = new Simulacao(mainPanel, cardLayout);
        Menu telaInicial = new Menu(telaSimulacao,mainPanel, cardLayout);
        Login telaLogin = new Login(telaInicial,mainPanel,cardLayout);
        Registro telaRegistro = new Registro(mainPanel,cardLayout);
        Estatisticas telaEstatisticas = new Estatisticas(mainPanel,cardLayout);
        mainPanel.add(telaLogin,"telaLogin");
        mainPanel.add(telaInicial, "telaInicial");
        mainPanel.add(telaSimulacao, "telaSimulacao");
        mainPanel.add(telaRegistro,"telaRegistro");
        mainPanel.add(telaEstatisticas,"telaEstatisticas");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

}
