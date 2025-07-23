package main.controller;

import main.entity.Usuario;
import main.util.SessionManager;
import main.view.MenuView;

import javax.swing.*;
import java.awt.*; // Importe CardLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuController {

    private MenuView view;
    private SimulacaoController simulacaoController; // Nova dependência
    private EstatisticasController estatisticasController; // Nova dependência
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MenuController(MenuView view,
                          SimulacaoController simulacaoController,
                          EstatisticasController estatisticasController,
                          JPanel mainPanel,
                          CardLayout cardLayout) {
        this.view = view;
        this.simulacaoController = simulacaoController;
        this.estatisticasController = estatisticasController;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // Adiciona os ActionListeners aos botões da View
        this.view.addIniciarButtonListener(new IniciarButtonListener());
        this.view.addEstatisticasButtonListener(new EstatisticasButtonListener());

        // Atualiza o nome do usuário ao iniciar o controller
        setLoggedInUserName();
    }

    /**
     * Atualiza o rótulo do usuário logado na View.
     * Este método pode ser chamado por outros controllers (ex: LoginController)
     * quando um usuário faz login com sucesso, para atualizar a tela de menu.
     */
    public void setLoggedInUserName() {
        Usuario usuario = SessionManager.getInstance().getLoggedInUser();
        if (usuario != null) {
            view.setUserLabelText("Usuário: " + usuario.getLogin());
        } else {
            view.setUserLabelText("");
        }
    }

    /**
     * Listener para o botão "Iniciar Simulação".
     */
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
                    // Chama o método no SimulacaoController para iniciar a simulação
                    simulacaoController.iniciarSimulacao(numCriaturas);
                    view.clearCriaturasField(); // Limpa o campo na própria MenuView
                    cardLayout.show(mainPanel, "telaSimulacao"); // Navega para a tela de simulação
                }
            } catch (NumberFormatException ex) {
                view.showMessage("Número de criaturas inválido. Digite apenas números inteiros.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener para o botão "Estatísticas".
     */
    class EstatisticasButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Chama o método no EstatisticasController para carregar os dados
            estatisticasController.carregarEstatisticas();
            cardLayout.show(mainPanel, "telaEstatisticas"); // Navega para a tela de estatísticas
        }
    }
}