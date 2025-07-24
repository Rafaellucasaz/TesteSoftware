package main.controller;

import main.dao.UsuarioDao;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Criatura;
import main.model.Horizonte;
import main.model.Tipos;
import main.model.Usuario;
import main.util.SessionManager;
import main.view.SimulacaoView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimulacaoController {

    private SimulacaoView view;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private List<Criatura> criaturas;
    private Timer timer;
    private int rodadaAtual = 1;
    private boolean isPaused = false;
    private boolean fecharHorizonte = false;
    private UsuarioDao usuarioDao;

    public SimulacaoController(SimulacaoView view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.usuarioDao = new UsuarioDaoImpl();

        this.view.addPauseButtonListener(new PauseButtonListener());
        this.view.addBackToMenuButtonListener(new BackToMenuButtonListener());
    }

    public void iniciarSimulacao(int nCriaturas)  {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        this.rodadaAtual = 1;
        this.criaturas = new ArrayList<>();
        this.isPaused = false;
        this.fecharHorizonte = false;
        Horizonte.reset();

        Usuario usuarioLogado = SessionManager.getInstance().getLoggedInUser();
        if (usuarioLogado != null) {
            usuarioLogado.setQtdSimulacoes(usuarioLogado.getQtdSimulacoes() + 1);
            try {
                usuarioDao.updateUsuario(usuarioLogado);
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar a quantidade de simulações do usuário: " + e.getMessage());
            }
        }

        for (int i = 0; i < nCriaturas; i++) {
            Criatura criatura = new Criatura(i, Tipos.minion, 1000000);
            criaturas.add(criatura);
        }
        criaturas.add(new Criatura(nCriaturas + 1, Tipos.guardiao, 0));

        updateCountsAndScoreboard();
        view.setRodadaLabel(rodadaAtual);
        view.setPauseButtonText("Pausar simulação");
        view.updateSimulationDisplay(criaturas);

        timer = new Timer(200, new SimulationTickListener());
        timer.start();
    }

    private void updateCountsAndScoreboard() {
        int minionCount = 0;
        int clusterCount = 0;
        int guardiaoCount = 0;

        List<Criatura> criaturasCopia = new ArrayList<>(criaturas);
        for (Criatura c : criaturasCopia) {
            if (c.getTipo() == Tipos.minion) {
                minionCount++;
            } else if (c.getTipo() == Tipos.cluster) {
                clusterCount++;
            } else if (c.getTipo() == Tipos.guardiao) {
                guardiaoCount++;
            }
        }
        view.updateScoreboard(criaturas, minionCount, clusterCount, guardiaoCount);
    }

    class SimulationTickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            rodadaAtual++;
            view.setRodadaLabel(rodadaAtual);

            if (rodadaAtual > 20) {
                fecharHorizonte = true;
            }
            if (fecharHorizonte) {
                Horizonte.comecoHorizonte += 1;
                Horizonte.finalHorizonte -= 1;
            }

            List<Criatura> criaturasAtivas = new ArrayList<>(criaturas);
            List<Criatura> criaturasParaRemover = new ArrayList<>();

            for (Criatura c : criaturasAtivas) {
                c.move();
                c.roubar(criaturas);
                if (c.getOuro() <= 0 && c.getTipo() == Tipos.minion) {
                    criaturasParaRemover.add(c);
                }
            }
            criaturas.removeAll(criaturasParaRemover);

            updateCountsAndScoreboard();
            view.updateSimulationDisplay(criaturas);

            if (simulacaoFinalizada()) {
                timer.stop();
                handleSimulationEnd();
            }
        }
    }

    class PauseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer == null) return;

            if (isPaused) {
                isPaused = false;
                timer.start();
                view.setPauseButtonText("Pausar simulação");
            } else {
                isPaused = true;
                timer.stop();
                view.setPauseButtonText("Retomar simulação");
            }
        }
    }

    class BackToMenuButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }

            cardLayout.show(mainPanel, "telaMenu");
        }
    }

    private boolean simulacaoFinalizada() {
        int minionCount = 0;
        int clusterCount = 0;
        for (Criatura c : criaturas) {
            if (c.getTipo() == Tipos.minion) {
                minionCount++;
            } else if (c.getTipo() == Tipos.cluster) {
                clusterCount++;
            }
        }
        return minionCount < 1 || (clusterCount < 1 && minionCount == 1);
    }

    private boolean simulacaoSucedida() {
        for (Criatura c : criaturas) {
            if (c.getTipo() == Tipos.cluster) {
                return false;
            }
        }
        return true;
    }

    private void handleSimulationEnd() {
        Usuario usuarioLogado = SessionManager.getInstance().getLoggedInUser();
        if (usuarioLogado != null) {
            usuarioLogado.setPontuacao(usuarioLogado.getPontuacao() + 1);

            if (simulacaoSucedida()) {
                view.showMessage("Simulação concluída! O guardião venceu!", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
                usuarioLogado.setPontuacao(usuarioLogado.getPontuacao() + 3);
            } else {
                view.showMessage("Simulação concluída! Os Clusters venceram!", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
            }

            try {
                usuarioDao.updateUsuario(usuarioLogado);
            } catch (SQLException ex) {
                System.err.println("Erro ao atualizar a pontuação do usuário: " + ex.getMessage());
            }
        } else {
            if (simulacaoSucedida()) {
                view.showMessage("Simulação concluída! O guardião venceu!", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
            } else {
                view.showMessage("Simulação concluída! Os Clusters venceram!", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}