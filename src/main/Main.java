package main;

import main.controller.*;
import main.dao.UsuarioDao;
import main.util.Db;
import main.view.*;

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
        EstatisticasView estatisticasView = new EstatisticasView();
        SimulacaoView simulacaoView = new SimulacaoView();
        MenuView menuView = new MenuView();
        LoginView loginView = new LoginView();
        RegistroView registroView = new RegistroView();

        SimulacaoController simulacaoController = new SimulacaoController(simulacaoView,mainPanel,cardLayout);
        EstatisticasController estatisticasController = new EstatisticasController(estatisticasView,mainPanel,cardLayout);
        RegistroController registroController = new RegistroController(registroView,mainPanel,cardLayout);
        MenuController menuController = new MenuController(menuView,simulacaoController,estatisticasController, mainPanel,cardLayout);
        LoginController loginController = new LoginController(loginView,mainPanel,cardLayout,menuController);
        mainPanel.add(loginView,"telaLogin");
        mainPanel.add(menuView, "telaMenu");
        mainPanel.add(simulacaoView, "telaSimulacao");
        mainPanel.add(registroView,"telaRegistro");
        mainPanel.add(estatisticasView,"telaEstatisticas");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

}
