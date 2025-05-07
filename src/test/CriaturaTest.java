package test;

import main.entity.Criatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CriaturaTest {

    Criatura criatura1;
    Criatura criatura2;
    Criatura[] criaturas;

    @BeforeEach
    void setUp() {
        criatura1 = new Criatura(0);
        criatura2 = new Criatura(1);
        criaturas = new Criatura[]{criatura1, criatura2};
    }

    @Test
    void testInicializacao() {
        assertEquals(0, criatura1.getId());
        assertEquals(1000000, criatura1.getOuro());
        assertTrue(criatura1.getPosX() >= 0 && criatura1.getPosX() <= 20);
    }

    @Test
    void testMovimentoDentroDosLimites() {
        for (int i = 0; i < 1000; i++) {
            criatura1.setPosX(10);
            criatura1.move();
            assertTrue(criatura1.getPosX() >= 0 && criatura1.getPosX() <= 20);
        }
    }

    @Test
    void testResetarPosicaoForaDoHorizonte() {
        criatura1.setPosX(-1); // força o reset
        criatura1.move();
        assertTrue(criatura1.getPosX() >= 0 && criatura1.getPosX() <= 20);

        criatura1.setPosX(21);
        criatura1.move();
        assertTrue(criatura1.getPosX() >= 0 && criatura1.getPosX() <= 20);
    }

    @Test
    void testCriaturaMaisProxima() {
        criatura1.setPosX(5);
        criatura2.setPosX(7);
        Criatura maisProxima = criatura1.criaturaMaisProx(criaturas);
        assertEquals(criatura2, maisProxima);
    }

    @Test
    void testRouboReduzEOuroDaVitimaEPassaParaLadrao() {
        criatura1.setOuro(1000000);
        criatura2.setOuro(1000000);
        criatura1.setPosX(5);
        criatura2.setPosX(6);

        criatura1.roubar(criaturas);

        // Após o roubo:
        // criatura2 -> perde metade: 1000000 / 2 = 500000
        // criatura1 -> ganha 500000
        assertEquals(1500000, criatura1.getOuro());
        assertEquals(500000, criatura2.getOuro());
    }
}
