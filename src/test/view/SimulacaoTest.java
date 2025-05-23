package test.view;
import main.Constantes;
import main.entity.Criatura;
import main.view.Simulacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;

public class SimulacaoTest {

    Simulacao simulacao;

    @BeforeEach
    void setUp() {
        simulacao = new Simulacao(new JPanel(), new CardLayout());
    }
    @Test
    void testIniciarSimulacaoValoresValidos() {
        int result = simulacao.iniciarSimulacao(5, 10);
        assertEquals(0, result);
        assertEquals(10, simulacao.nCriaturas);
        assertEquals(5, simulacao.rodadas);
        assertNotNull(simulacao.criaturas);
    }

    @Test
    void testIniciarSimulacaoCriaturasMinimo() {
        int result = simulacao.iniciarSimulacao(5, 2);
        assertEquals(0, result);
    }

    @Test
    void testIniciarSimulacaoCriaturasMaximo() {
        int result = simulacao.iniciarSimulacao(5, 1000);
        assertEquals(0, result);
    }

    @Test
    void testMenosDe2Criaturas(){
        assertEquals(1,simulacao.iniciarSimulacao(5,1));
    }

    @Test
    void testMaisDe1000Criaturas(){
        assertEquals(1,simulacao.iniciarSimulacao(5,1001));
    }
    @Test
    void testMenosDe1Rodada(){
        assertEquals(1,simulacao.iniciarSimulacao(0,50));
    }

    @Test
    void testPausarRetomarSimulacao() {
        simulacao.iniciarSimulacao(5, 10);
        simulacao.pauseButton.doClick();
        assertTrue(simulacao.pause);
        assertEquals("Retomar simulação", simulacao.pauseButton.getText());

        simulacao.pauseButton.doClick();
        assertFalse(simulacao.pause);
        assertEquals("Pausar simulação", simulacao.pauseButton.getText());
    }


}
