package main.view;

import main.Constantes;
import main.entity.Criatura;
import main.entity.Horizonte;
import main.entity.Tipos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Importe ActionListener
import java.util.List;

public class SimulacaoView extends JPanel {

    // Componentes que o Controller precisará interagir ou que a View atualizará
    private JPanel simulacaoPanel;
    private JPanel scorePanel; // Renomeado para evitar conflito com método
    private JLabel rodadaLabel;
    private JButton pauseButton;
    private JButton backToMenuButton;

    // Dados que a View renderizará (serão definidos pelo Controller)
    private List<Criatura> criaturasParaDesenhar; // Lista para o paintComponent
    private int minionCount;
    private int clusterCount;
    private int guardiaoCount;

    public SimulacaoView() {
        setLayout(new BorderLayout());

        // Painel onde as criaturas serão desenhadas
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
                        // Desenha as linhas do horizonte (H2 não é mais o nome do banco, mas a classe Horizonte)
                        g.setColor(Color.BLACK);
                        g.drawLine((Horizonte.comecoHorizonte * 10) - raio, 0, (Horizonte.comecoHorizonte * 10) - raio, 400);
                        g.drawLine((Horizonte.finalHorizonte * 10) + raio, 0, (Horizonte.finalHorizonte * 10) + raio, 400);
                        g.drawLine((Horizonte.comecoHorizonte * 10) - raio, 155, (Horizonte.finalHorizonte * 10) + raio, 155);

                        g.setColor(Color.WHITE); // Volta a cor para branco para não afetar o fundo
                    }
                }
            }
        };
        simulacaoPanel.setPreferredSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));
        simulacaoPanel.setBackground(Color.LIGHT_GRAY); // Cor de fundo para o painel da simulação
        add(simulacaoPanel, BorderLayout.NORTH);

        // Painel de controle (botões e rótulo de rodada)
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

        // Painel para o placar (score)
        scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout()); // Inicialmente BorderLayout para o resumo e grid
        scorePanel.setSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));

        JScrollPane scrollPane = new JScrollPane(scorePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- Métodos para o Controller Definir Eventos e Atualizar a View ---

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
        simulacaoPanel.repaint(); // Solicita a repintura do painel da simulação
    }

    public void updateScoreboard(List<Criatura> criaturas, int minionCount, int clusterCount, int guardiaoCount) {
        scorePanel.removeAll(); // Limpa o painel antes de adicionar novos componentes

        // Painel de resumo (minions, clusters, guardiões)
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

        // Painel para o grid das criaturas detalhadas
        JPanel creatureGridPanel = new JPanel();
        creatureGridPanel.setBackground(new Color(255, 255, 240));

        int numCreatures = criaturas.size();
        int columns = Math.min(numCreatures, 4); // Máximo de 4 colunas
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
        scorePanel.add(creatureGridPanel, BorderLayout.CENTER); // Adiciona o grid ao painel principal do score

        scorePanel.revalidate(); // Revalida o layout do painel do score
        scorePanel.repaint();   // Solicita a repintura do painel do score
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}