package main.view;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import main.Constantes;
import main.dao.UsuarioDao;
import main.dao.impl.UsuarioDaoImpl;
import main.entity.Criatura;
import main.entity.Tipos;
import main.entity.Usuario;
import main.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulacao extends JPanel {

    public int minionCount = 0;
    public int clusterCount = 0;
    public int guardiaoCount = 0;
    private UsuarioDao usuario;
    public int nCriaturas;
    public List<Criatura> criaturas;
    public Timer timer;
    public int rodadaAtual = 1;
    public boolean pause = false;

    public JPanel score;
    public JPanel simulacaoPanel;
    public JLabel rodadaLabel;
    public JButton pauseButton;
    public JButton backToMenuButton;

    public Simulacao(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());

        simulacaoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (criaturas != null) {

                    for (Criatura c : criaturas) {
                        int x = (int) (c.getPosX() * 10);
                        int y = 150;
                        int raio = 5;

                        g.setColor(c.getColor());
                        if(c.getTipo() == Tipos.minion){
                            g.fillOval(x, y, raio, raio);
                        }
                        else if(c.getTipo() == Tipos.cluster){
                            g.fillOval(x, y-10, raio*3, raio*3);
                        }
                        else if(c.getTipo() == Tipos.guardiao){
                            g.fillRect(x,y-25,30,30);
                            g.setColor(Color.BLACK);
                            g.setFont(new Font("Arial", Font.BOLD, 20));
                            FontMetrics fm = g.getFontMetrics();
                            int textX = x + (30 - fm.stringWidth("g")) / 2;
                            int textY = (y - 25) + ((30 - fm.getHeight()) / 2) + fm.getAscent();
                            g.drawString("g", textX, textY);
                        }
                        g.setColor(Color.BLACK);

                        g.drawLine((Constantes.comecoHorizonte * 10) - raio, 0, (Constantes.comecoHorizonte * 10) - raio, 400);
                        g.drawLine((Constantes.finalHorizonte * 10) + raio, 0, (Constantes.finalHorizonte * 10) + raio, 400);
                        g.drawLine((Constantes.comecoHorizonte * 10) - raio, 155, (Constantes.finalHorizonte * 10) + raio, 155);

                        g.setColor(Color.WHITE);
                    }
                }
            }
        };

        simulacaoPanel.setPreferredSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setBackground(new Color(230, 230, 230));

        rodadaLabel = new JLabel("Rodada Atual: " + rodadaAtual);
        rodadaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(rodadaLabel);

        pauseButton = new JButton("Pausar simulação");
        pauseButton.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(pauseButton);

        backToMenuButton = new JButton("Voltar ao Menu");
        backToMenuButton.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(backToMenuButton);


        add(simulacaoPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);



        score = new JPanel();
        score.setSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));
        score.setLayout(new GridLayout(1, 1));

        JScrollPane scrollPane = new JScrollPane(score);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pause) {
                    pause = false;
                    timer.start();
                    pauseButton.setText("Pausar simulação");
                } else {
                    pause = true;
                    timer.stop();
                    pauseButton.setText("Retomar simulação");
                }
            }
        });
        backToMenuButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            rodadaAtual = 1;
            cardLayout.show(mainPanel, "telaInicial");
        });
    }

    public int iniciarSimulacao(int nCriaturas) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        this.rodadaAtual = 1;
        this.nCriaturas = nCriaturas;
        this.criaturas = new ArrayList<>();

        Usuario usuario = SessionManager.getInstance().getLoggedInUser();
        usuario.setQtdSimulacoes(usuario.getQtdSimulacoes()+1);
        UsuarioDao usuarioDao = new UsuarioDaoImpl();

        try {
            usuarioDao.updateUsuario(usuario);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if ( nCriaturas < 2 || nCriaturas > 1000) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            return 1;
        }


        for (int i = 0; i < nCriaturas; i++) {
            Criatura criatura = new Criatura(i, Tipos.minion, 1000000);
            criaturas.add(criatura);
        }
        criaturas.add(new Criatura(nCriaturas+1,Tipos.guardiao,0));
        updateScoreboard();
        score.revalidate();
        score.repaint();

        timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rodadaLabel.setText("Rodada Atual: " + rodadaAtual);


                for (int i = 0; i < criaturas.size(); i++) {
                    Criatura c = criaturas.get(i);
                    c.move();
                    c.roubar(criaturas);
                }
                updateScoreboard();
                rodadaAtual++;
                simulacaoPanel.repaint();

                if (simulacaoFinalizada()) {

                    if(simulacaoSucedida()){
                        JOptionPane.showMessageDialog(Simulacao.this, "Simulação concluída! O guardião venceu!", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
                        //usuario ganha 1 ponto por concluir a simulação
                        usuario.setPontuacao(usuario.getPontuacao()+1);
                        try {
                            usuarioDao.updateUsuario(usuario);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(Simulacao.this, "Simulação concluída! Os Clusters venceram", "Fim da Simulação", JOptionPane.INFORMATION_MESSAGE);
                    }

                    timer.stop();
                }
            }
        });

        timer.start();
        return 0;
    }
    public void updateScoreboard() {
        score.removeAll();
        minionCount = 0;
        clusterCount = 0;
        guardiaoCount = 0;
        for (Criatura c : criaturas) {
            if (c.getTipo() == Tipos.minion) {
                minionCount++;
            } else if (c.getTipo() == Tipos.cluster) {
                clusterCount++;
            } else if (c.getTipo() == Tipos.guardiao) {
                guardiaoCount++;
            }
        }
        score.setLayout(new BorderLayout());
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

        score.add(summaryPanel, BorderLayout.NORTH);

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
        score.add(creatureGridPanel, BorderLayout.CENTER);
        score.revalidate();
        score.repaint();
    }

    public boolean simulacaoFinalizada(){
        return minionCount < 1 || clusterCount < 1;
    }
    public boolean simulacaoSucedida(){
        for(Criatura c : criaturas){
            if(c.getTipo() == Tipos.cluster){
                return false;
            }
        }
        return true;
    }
}


