package test.entity;

import main.Constantes;
import main.entity.Criatura;
import main.entity.Tipos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CriaturaTest {

    Criatura criatura1;
    Criatura criatura2;
    Criatura criatura3;
    List<Criatura> criaturas;

    @BeforeEach
    void setUp() {
        criatura1 = new Criatura(0, Tipos.minion, 1000000);
        criatura2 = new Criatura(1, Tipos.minion, 1000000);
        criatura3 = new Criatura(2, Tipos.minion, 1000000);
        criaturas = new ArrayList<>();
        criaturas.add(criatura1);
        criaturas.add(criatura2);
        criaturas.add(criatura3);
    }

    @Test
    void testInicializacao() {
        assertEquals(0, criatura1.getId());
        assertEquals(1000000, criatura1.getOuro());
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
    }

    @Test
    void testGetId() {
        criatura1.setId(2);
        assertEquals(2, criatura1.getId());
    }

    @Test
    void testColorNotNull() {
        assertNotNull(criatura1.getColor());
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
        criatura1.setPosX(Constantes.comecoHorizonte - 1);
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);

        criatura1.setPosX(Constantes.finalHorizonte + 1);
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
    void testRouboValido() {
        criatura1.setOuro(1000000);
        criatura2.setOuro(1000000);
        criatura3.setOuro(1000000);

        criatura1.setPosX(11);
        criatura2.setPosX(12);
        criatura3.setPosX(50);

        criatura1.roubar(criaturas);

        assertEquals(1500000, criatura1.getOuro());
        assertEquals(500000, criatura2.getOuro());
        assertEquals(1000000, criatura3.getOuro());
    }
}