package test.view;

import main.Constantes;
import main.dao.UsuarioDao;
import main.entity.Criatura;
import main.entity.Usuario;
import main.util.SessionManager;
import main.view.Simulacao;
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
        usuarioFake = new Usuario("teste", "teste123");
        usuarioFake.setPontuacao(0);
        usuarioFake.setQtdSimulacoes(0);

        SessionManager.getInstance().setLoggedInUser(usuarioFake);
        simulacao = new Simulacao(new JPanel(), new CardLayout());
    }

    @Test
    void testIniciarSimulacaoValoresValidos() {
        int result = simulacao.iniciarSimulacao(5, 10);
        assertEquals(0, result);
        assertEquals(10, simulacao.nCriaturas);
        assertEquals(5, simulacao.rodadas);
        assertNotNull(simulacao.criaturas);
        assertEquals(11, simulacao.criaturas.size()); // 10 minions + 1 guardião
    }

    @Test
    void testIniciarSimulacaoCriaturasMinimo() {
        assertEquals(0, simulacao.iniciarSimulacao(5, 2));
    }

    @Test
    void testIniciarSimulacaoCriaturasMaximo() {
        assertEquals(0, simulacao.iniciarSimulacao(5, 1000));
    }

    @Test
    void testMenosDe2Criaturas() {
        assertEquals(1, simulacao.iniciarSimulacao(5, 1));
    }

    @Test
    void testMaisDe1000Criaturas() {
        assertEquals(1, simulacao.iniciarSimulacao(5, 1001));
    }

    @Test
    void testMenosDe1Rodada() {
        assertEquals(1, simulacao.iniciarSimulacao(0, 50));
    }

    @Test
    void testPausarRetomarSimulacao() {
        simulacao.iniciarSimulacao(5, 10);

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
    void testLabelsSaoAtualizados() {
        simulacao.iniciarSimulacao(5, 10);

        for (int i = 0; i < 10; i++) {
            Criatura c = simulacao.criaturas.get(i);
            String textoEsperado = "ID: " + i + " | posX: " + String.format("%.2f", c.getPosX()) + " | ouro: " + c.getOuro();
            assertEquals(textoEsperado, simulacao.scoreLabels[i].getText());
        }
    }

    @Test
    void testUsuarioAtualizaQtdSimulacoes() {
        assertEquals(0, usuarioFake.getQtdSimulacoes());
        simulacao.iniciarSimulacao(5, 10);
        assertEquals(1, usuarioFake.getQtdSimulacoes());
    }

    @Test
    void testCriaturasAdicionadasCorretamente() {
        simulacao.iniciarSimulacao(5, 5);
        assertEquals(6, simulacao.criaturas.size()); // 5 minions + 1 guardião
        assertEquals(Tipos.guardiao, simulacao.criaturas.get(5).getTipo());
    }
}
