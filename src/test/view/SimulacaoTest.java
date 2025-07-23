package test.view;

import main.entity.Criatura;
import main.entity.Tipos;
import main.entity.Usuario;
import main.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimulacaoTest {

    Simulacao simulacao;
    Usuario usuarioFake;

    @BeforeEach
    void setUp() {
        // Mock da sessão do usuário para evitar banco real
        usuarioFake = new Usuario("usu1","senha123","avatar1",0,0);
        usuarioFake.setPontuacao(0);
        usuarioFake.setQtdSimulacoes(0);

        SessionManager.getInstance().setLoggedInUser(usuarioFake);
        simulacao = new Simulacao(new JPanel(), new CardLayout());
    }

    @Test
    void testIniciarSimulacaoValoresValidos() {
        int result = simulacao.iniciarSimulacao( 10);
        assertEquals(0, result);
        assertEquals(10, simulacao.nCriaturas);
        assertNotNull(simulacao.criaturas);
        assertEquals(11, simulacao.criaturas.size()); // 10 minions + 1 guardião
    }

    @Test
    void testIniciarSimulacaoCriaturasMinimo() {
        assertEquals(0, simulacao.iniciarSimulacao( 2));
    }

    @Test
    void testIniciarSimulacaoCriaturasMaximo() {
        assertEquals(0, simulacao.iniciarSimulacao( 1000));
    }

    @Test
    void testMenosDe2Criaturas() {
        assertEquals(1, simulacao.iniciarSimulacao( 1));
    }

    @Test
    void testMaisDe1000Criaturas() {
        assertEquals(1, simulacao.iniciarSimulacao( 1001));
    }

    @Test
    void testPausarRetomarSimulacao() {
        simulacao.iniciarSimulacao( 10);

        // Pausar
        simulacao.pauseButton.doClick();
        assertTrue(simulacao.pause);
        assertEquals("Retomar simulação", simulacao.pauseButton.getText());

        // Retomar
        simulacao.pauseButton.doClick();
        assertFalse(simulacao.pause);
        assertEquals("Pausar simulação", simulacao.pauseButton.getText());
    }



    @Test
    void testUsuarioAtualizaQtdSimulacoes() {
        assertEquals(0, usuarioFake.getQtdSimulacoes());
        simulacao.iniciarSimulacao(10);
        assertEquals(1, usuarioFake.getQtdSimulacoes());
    }

    @Test
    void testCriaturasAdicionadasCorretamente() {
        simulacao.iniciarSimulacao( 5);
        assertEquals(6, simulacao.criaturas.size()); // 5 minions + 1 guardião
        assertEquals(Tipos.guardiao, simulacao.criaturas.get(5).getTipo());
    }

    @Test
    void testUpdateScoreboardAposSimulacao() {
        simulacao.iniciarSimulacao(3);

        // transformar um minion em cluster
        Criatura criaturaCluster = simulacao.criaturas.get(0);
        criaturaCluster.setTipo(Tipos.cluster);

        simulacao.updateScoreboard(); // forçar atualização

        assertEquals(2, simulacao.minionCount);
        assertEquals(1, simulacao.clusterCount);
        assertEquals(1, simulacao.guardiaoCount);
    }

    @Test
    void testSimulacaoFinalizadaComZeroMinions() {
        simulacao.iniciarSimulacao(3);
        for (Criatura c : simulacao.criaturas) {
            if (c.getTipo() == Tipos.minion) {
                c.setTipo(Tipos.cluster);
            }
        }
        simulacao.updateScoreboard();
        assertTrue(simulacao.simulacaoFinalizada());
    }

    @Test
    void testSimulacaoFinalizadaComZeroClusters() {
        simulacao.iniciarSimulacao(3);
        for (Criatura c : simulacao.criaturas) {
            if (c.getTipo() == Tipos.cluster) {
                c.setTipo(Tipos.minion);
            }
        }
        simulacao.updateScoreboard();
        assertTrue(simulacao.simulacaoFinalizada());
    }

    @Test
    void testSimulacaoSucedidaComApenasMinionsEGuardiao() {
        simulacao.iniciarSimulacao(4);
        for (Criatura c : simulacao.criaturas) {
            if (c.getTipo() == Tipos.minion) {
                c.setTipo(Tipos.minion);
            }
        }
        assertTrue(simulacao.simulacaoSucedida());
    }

    @Test
    void testSimulacaoSucedidaFalsaComClusterPresente() {
        simulacao.iniciarSimulacao(5);
        Criatura c = simulacao.criaturas.get(0);
        c.setTipo(Tipos.cluster);
        assertFalse(simulacao.simulacaoSucedida());
    }


}
