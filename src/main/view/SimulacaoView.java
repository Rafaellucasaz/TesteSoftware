package main.view;

import main.Constantes;
import main.model.Criatura;
import main.model.Horizonte;
import main.model.Tipos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulacaoView extends JPanel {


    private JPanel simulacaoPanel;
    private JPanel scorePanel;
    private JLabel rodadaLabel;
    private JButton pauseButton;
    private JButton backToMenuButton;


    private List<Criatura> criaturasParaDesenhar;
    private int minionCount;
    private int clusterCount;
    private int guardiaoCount;

    public SimulacaoView() {
        setLayout(new BorderLayout());


        simulacaoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (criaturasParaDesenhar != null) {
                    for (Criatura c : criaturasParaDesenhar) {
                        int x = (int) (c.getPosX() * 10);
                        int y = 150;
                        int raio = 5;

                        g.setColor(c.getColor());
                        if (c.getTipo() == Tipos.minion) {
                            g.fillOval(x, y, raio, raio);
                        } else if (c.getTipo() == Tipos.cluster) {
                            g.fillOval(x, y - 10, raio * 3, raio * 3);
                        } else if (c.getTipo() == Tipos.guardiao) {
                            g.fillRect(x, y - 25, 30, 30);
                            g.setColor(Color.BLACK);
                            g.setFont(new Font("Arial", Font.BOLD, 20));
                            FontMetrics fm = g.getFontMetrics();
                            int textX = x + (30 - fm.stringWidth("g")) / 2;
                            int textY = (y - 25) + ((30 - fm.getHeight()) / 2) + fm.getAscent();
                            g.drawString("g", textX, textY);
                        }

                        g.setColor(Color.BLACK);
                        g.drawLine((Horizonte.comecoHorizonte * 10) - raio, 0, (Horizonte.comecoHorizonte * 10) - raio, 400);
                        g.drawLine((Horizonte.finalHorizonte * 10) + raio, 0, (Horizonte.finalHorizonte * 10) + raio, 400);
                        g.drawLine((Horizonte.comecoHorizonte * 10) - raio, 155, (Horizonte.finalHorizonte * 10) + raio, 155);

                        g.setColor(Color.WHITE);
                    }
                }
            }
        };
        simulacaoPanel.setPreferredSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));
        simulacaoPanel.setBackground(Color.LIGHT_GRAY);
        add(simulacaoPanel, BorderLayout.NORTH);


        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setBackground(new Color(230, 230, 230));

        rodadaLabel = new JLabel("Rodada Atual: 1");
        rodadaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(rodadaLabel);

        pauseButton = new JButton("Pausar simulação");
        pauseButton.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(pauseButton);

        backToMenuButton = new JButton("Voltar ao Menu");
        backToMenuButton.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(backToMenuButton);

        add(controlPanel, BorderLayout.SOUTH);


        scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());
        scorePanel.setSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));

        JScrollPane scrollPane = new JScrollPane(scorePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }



    public void addPauseButtonListener(ActionListener listener) {
        pauseButton.addActionListener(listener);
    }

    public void addBackToMenuButtonListener(ActionListener listener) {
        backToMenuButton.addActionListener(listener);
    }

    public void setRodadaLabel(int rodada) {
        rodadaLabel.setText("Rodada Atual: " + rodada);
    }

    public void setPauseButtonText(String text) {
        pauseButton.setText(text);
    }

    public void updateSimulationDisplay(List<Criatura> criaturas) {
        this.criaturasParaDesenhar = criaturas;
        simulacaoPanel.repaint();
    }

    public void updateScoreboard(List<Criatura> criaturas, int minionCount, int clusterCount, int guardiaoCount) {
        scorePanel.removeAll();


        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        summaryPanel.setBackground(new Color(255, 255, 240));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel minionLabel = new JLabel("Minions: " + minionCount);
        minionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(minionLabel);

        JLabel clusterLabel = new JLabel("Clusters: " + clusterCount);
        clusterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(clusterLabel);

        JLabel guardiaoLabel = new JLabel("Guardiões: " + guardiaoCount);
        guardiaoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(guardiaoLabel);

        scorePanel.add(summaryPanel, BorderLayout.NORTH);


        JPanel creatureGridPanel = new JPanel();
        creatureGridPanel.setBackground(new Color(255, 255, 240));

        int numCreatures = criaturas.size();
        int columns = Math.min(numCreatures, 4);
        int rows = (int) Math.ceil((double) numCreatures / columns);
        creatureGridPanel.setLayout(new GridLayout(rows, columns, 5, 5));

        for (Criatura c : criaturas) {
            JLabel scoreLabel = new JLabel("<html>ID: " + c.getId()
                    + "<br>Tipo: " + c.getTipo()
                    + "<br>PosX: " + String.format("%.2f", c.getPosX())
                    + "<br>Ouro: " + c.getOuro() + "</html>");
            scoreLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabel.setVerticalAlignment(SwingConstants.TOP);
            scoreLabel.setOpaque(true);
            scoreLabel.setBackground(c.getColor().brighter());
            scoreLabel.setForeground(Color.BLACK);
            creatureGridPanel.add(scoreLabel);
        }
        scorePanel.add(creatureGridPanel, BorderLayout.CENTER);

        scorePanel.revalidate();
        scorePanel.repaint();
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}