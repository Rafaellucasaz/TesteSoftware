package main.controller; // Crie um pacote 'controller'

import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.view.EstatisticasView; // Importe a View

import javax.swing.*;
import java.awt.*; // Importe CardLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class EstatisticasController {

    private EstatisticasView view;
    private UsuarioDaoImpl usuarioDAO;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public EstatisticasController(EstatisticasView view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl(); // Instancia o DAO
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.view.addBackButtonListener(new BackButtonListener());

        carregarEstatisticas();
    }


    public void carregarEstatisticas() {
        view.clearTable(); // Limpa a tabela antes de preencher

        long totalSimulations = 0;
        long totalScore = 0;
        int userCount = 0;

        try {
            List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
            userCount = usuarios.size();

            for (Usuario usuario : usuarios) {
                // Adiciona a linha na tabela da View
                view.addTableRow(new Object[]{
                        usuario.getLogin(),
                        usuario.getPontuacao(),
                        usuario.getQtdSimulacoes()
                });
                totalSimulations += usuario.getQtdSimulacoes();
                totalScore += usuario.getPontuacao();
            }


            view.setTotalSimulationsLabel(totalSimulations);

            if (userCount > 0) {
                double avgSims = (double) totalSimulations / userCount;
                double avgScore = (double) totalScore / userCount;
                view.setAvgSimulationsLabel(avgSims);
                view.setAvgScoreLabel(avgScore);
            } else {
                view.setAvgSimulationsLabel(0.0);
                view.setAvgScoreLabel(0.0);
            }

        } catch (SQLException ex) {
            view.showMessage("Erro ao carregar estatísticas do banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            cardLayout.show(mainPanel, "telaMenu");

        }
    }
}