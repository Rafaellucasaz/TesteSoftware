package main.controller;

import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.util.SessionManager;
import main.view.MenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MenuController {

    private MenuView view;
    private UsuarioDaoImpl usuarioDao;
    private SimulacaoController simulacaoController;
    private EstatisticasController estatisticasController;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MenuController(MenuView view,
                          SimulacaoController simulacaoController,
                          EstatisticasController estatisticasController,
                          JPanel mainPanel,
                          CardLayout cardLayout) {
        this.view = view;
        this.usuarioDao = new UsuarioDaoImpl();
        this.simulacaoController = simulacaoController;
        this.estatisticasController = estatisticasController;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        this.view.addIniciarButtonListener(new IniciarButtonListener());
        this.view.addEstatisticasButtonListener(new EstatisticasButtonListener());
        this.view.addDeletarUsuarioButtonListener(new DeletarUsuarioButtonListener());
        this.view.addLogoutButtonListener(new LogoutButtonListener());
        setLoggedInUserName();
    }


    public void setLoggedInUserName() {
        Usuario usuario = SessionManager.getInstance().getLoggedInUser();
        if (usuario != null) {

            view.setUserInfo(usuario.getLogin(), usuario.getAvatarURL());
        } else {
            view.setUserInfo("", "");
        }
    }

    class IniciarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String criaturasText = view.getNumeroCriaturasText();

            if (criaturasText.isEmpty()) {
                view.showMessage("Por favor, preencha o número de criaturas.", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int numCriaturas = Integer.parseInt(criaturasText);

                if (numCriaturas > 1000) {
                    view.showMessage("Número máximo de criaturas é 1000.", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
                } else if (numCriaturas < 2) {
                    view.showMessage("Número mínimo de criaturas é 2.", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
                } else {
                    simulacaoController.iniciarSimulacao(numCriaturas);
                    view.clearCriaturasField();
                    cardLayout.show(mainPanel, "telaSimulacao");
                }
            } catch (NumberFormatException ex) {
                view.showMessage("Número de criaturas inválido. Digite apenas números inteiros.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class EstatisticasButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            estatisticasController.carregarEstatisticas();
            cardLayout.show(mainPanel, "telaEstatisticas");
        }
    }

    class DeletarUsuarioButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            try {
                usuarioDao.deleteUsuario(SessionManager.getInstance().getLoggedInUser().getId());
                SessionManager.getInstance().setLoggedInUser(null);
                cardLayout.show(mainPanel,"telaLogin");
            } catch (SQLException ex) {
                view.showMessage("Erro ao deletar usuário: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            SessionManager.getInstance().setLoggedInUser(null);
            cardLayout.show(mainPanel,"telaLogin");
        }
    }
}