package test;

import main.Constantes;
import main.entity.Criatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CriaturaTest {

    Criatura criatura1;
    Criatura criatura2;
    Criatura criatura3;
    Criatura[] criaturas;

    @BeforeEach
    void setUp() {
        criatura1 = new Criatura(0);
        criatura2 = new Criatura(1);
        criatura3 = new Criatura(2);
        criaturas = new Criatura[]{criatura1, criatura2,criatura3};
    }

    @Test
    void testInicializacao() {
        assertEquals(0, criatura1.getId());
        assertEquals(1000000, criatura1.getOuro());
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
    }

    @Test
    void testMovimentoDentroDosLimites() {
        for (int i = 0; i < 1000; i++) {
            criatura1.move();
            assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
        }
    }

    @Test
    void testResetarPosicaoForaDoHorizonte() {
        criatura1.setPosX(15);
        assertEquals(15,criatura1.getPosX());
        criatura1.setPosX(Constantes.comecoHorizonte-1);
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
        criatura1.setPosX(Constantes.finalHorizonte+1);
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
    }

    @Test
    void testCriaturaMaisProxima() {
        criatura1.setPosX(11);
        criatura2.setPosX(12);
        criatura3.setPosX(50);
        Criatura maisProxima = criatura1.criaturaMaisProx(criaturas);
        assertEquals(criatura2.getId(), maisProxima.getId());
    }

    @Test
    void testRouboReduzEOuroDaVitimaEPassaParaLadrao() {
        criatura1.setOuro(1000000);
        criatura2.setOuro(1000000);
        criatura3.setOuro(1000000);
        criatura1.setPosX(11);
        criatura2.setPosX(12);
        criatura3.setPosX(50);

        criatura1.roubar(criaturas);



        // Após o roubo:
        // criatura2 -> perde metade: 1000000 / 2 = 500000
        // criatura1 -> ganha 500000
        assertEquals(1500000, criatura1.getOuro());
        assertEquals(500000, criatura2.getOuro());
        assertEquals(1000000,criatura3.getOuro());
    }
}
