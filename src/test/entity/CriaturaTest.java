package test.entity;

import main.Constantes;
import main.entity.Criatura;
import main.entity.Tipos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CriaturaTest {

    Criatura criaturaMinion;
    Criatura criaturaGuardiao;
    Criatura criaturaCluster;

    @BeforeEach
    void setUp() {
        criaturaMinion = new Criatura(1, Tipos.minion, 10);
        criaturaGuardiao = new Criatura(2, Tipos.guardiao, 20);
        criaturaCluster = new Criatura(3, Tipos.cluster, 30);
    }

    // ---------------- Testes de Domínio ----------------

    @Test
    void testConstrutorAtributosBasicos() {
        assertEquals(1, criaturaMinion.getId());
        assertEquals(Tipos.minion, criaturaMinion.getTipo());
        assertEquals(10, criaturaMinion.getOuro());
        assertNotNull(criaturaMinion.getColor());
    }

    @Test
    void testSetGetOuro() {
        criaturaMinion.setOuro(50);
        assertEquals(50, criaturaMinion.getOuro());
    }

    @Test
    void testSetGetId() {
        criaturaMinion.setId(99);
        assertEquals(99, criaturaMinion.getId());
    }

    @Test
    void testSetGetTipo() {
        criaturaMinion.setTipo(Tipos.guardiao);
        assertEquals(Tipos.guardiao, criaturaMinion.getTipo());
    }

    @Test
    void testSetGetColor() {
        Color novaCor = new Color(100, 150, 200);
        criaturaMinion.setColor(novaCor);
        assertEquals(novaCor, criaturaMinion.getColor());
    }

    // ---------------- Testes de Fronteira para posX ----------------

    @Test
    void testSetPosXDentroDosLimites() {
        double meio = (Constantes.comecoHorizonte + Constantes.finalHorizonte) / 2;
        criaturaMinion.setPosX(meio);
        assertEquals(meio, criaturaMinion.getPosX());
    }

    @Test
    void testSetPosXMenorQueInicioReseta() {
        criaturaMinion.setPosX(Constantes.comecoHorizonte - 5);
        assertTrue(criaturaMinion.getPosX() >= Constantes.comecoHorizonte);
    }

    @Test
    void testSetPosXMaiorQueFinalReseta() {
        criaturaMinion.setPosX(Constantes.finalHorizonte + 5);
        assertTrue(criaturaMinion.getPosX() >= Constantes.comecoHorizonte);
    }

    // ---------------- Testes Estruturais e MC/DC ----------------

    @Test
    void testMoveDentroDosLimites() {
        for (int i = 0; i < 100; i++) {  // repete para garantir casos aleatórios
            criaturaMinion.setPosX((Constantes.comecoHorizonte + Constantes.finalHorizonte) / 2);
            criaturaMinion.move();
            assertTrue(criaturaMinion.getPosX() >= Constantes.comecoHorizonte);
            assertTrue(criaturaMinion.getPosX() <= Constantes.finalHorizonte);
        }
    }

    @Test
    void testCriaturaMaisProximaRetornaCorreto() {
        Criatura outra = new Criatura(5, Tipos.minion, 15);
        outra.setPosX(criaturaMinion.getPosX() + 0.5);

        List<Criatura> lista = new ArrayList<>();
        lista.add(criaturaMinion);
        lista.add(outra);

        Criatura prox = criaturaMinion.criaturaMaisProx(lista);
        assertEquals(outra, prox);
    }

    @Test
    void testCriaturaMaisProximaClusterFormadoRemoveMinions() {
        Criatura minion2 = new Criatura(5, Tipos.minion, 15);
        minion2.setPosX(criaturaMinion.getPosX() + 0.5);

        List<Criatura> lista = new ArrayList<>();
        lista.add(criaturaMinion);
        lista.add(minion2);

        Criatura resultado = criaturaMinion.criaturaMaisProx(lista);
        assertNull(resultado);
        assertEquals(1, lista.size());
        assertEquals(Tipos.cluster, lista.get(0).getTipo());
        assertEquals(25, lista.get(0).getOuro());
    }

    @Test
    void testCriaturaMaisProximaGuardiaoEliminaCluster() {
        Criatura cluster = new Criatura(8, Tipos.cluster, 40);
        cluster.setPosX(criaturaGuardiao.getPosX() + 2);

        List<Criatura> lista = new ArrayList<>();
        lista.add(criaturaGuardiao);
        lista.add(cluster);

        Criatura resultado = criaturaGuardiao.criaturaMaisProx(lista);
        assertNull(resultado);
        assertEquals(1, lista.size());
        assertEquals(criaturaGuardiao, lista.get(0));
    }

    // ---------------- Testes do roubo ----------------

    @Test
    void testRoubarDivideOuroCorretamente() {
        Criatura alvo = new Criatura(10, Tipos.minion, 10);
        alvo.setPosX(criaturaMinion.getPosX() + 0.8);

        List<Criatura> lista = new ArrayList<>();
        lista.add(criaturaMinion);
        lista.add(alvo);

        criaturaMinion.roubar(lista);

        assertEquals(10 + 5, criaturaMinion.getOuro());
        assertEquals(5, alvo.getOuro());
    }

    @Test
    void testRoubarDeMinionAumentaEOuroEReduzDoOutro() {
        Criatura c1 = new Criatura(100, Tipos.minion, 40);
        Criatura c2 = new Criatura(200, Tipos.minion, 20);
        c2.setPosX(c1.getPosX() + 2);

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(c1);
        criaturas.add(c2);

        c1.roubar(criaturas);

        assertEquals(40 + 10, c1.getOuro());
        assertEquals(10, c2.getOuro());
    }

    @Test
    void testRoubarDeGuardiaoNaoAlteraOuro() {
        Criatura c1 = new Criatura(101, Tipos.minion, 50);
        Criatura guardiao = new Criatura(102, Tipos.guardiao, 100);
        guardiao.setPosX(c1.getPosX() + 2);

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(c1);
        criaturas.add(guardiao);

        c1.roubar(criaturas);

        assertEquals(50, c1.getOuro());
        assertEquals(100, guardiao.getOuro());
    }

    @Test
    void testRoubarComClusterNaoExecutaRoubo() {
        Criatura c1 = new Criatura(103, Tipos.minion, 30);
        Criatura c2 = new Criatura(104, Tipos.minion, 30);
        c2.setPosX(c1.getPosX() + 0.4);

        List<Criatura> criaturas = new ArrayList<>();
        criaturas.add(c1);
        criaturas.add(c2);

        c1.roubar(criaturas);

        assertEquals(1, criaturas.size());
        Criatura novoCluster = criaturas.get(0);
        assertEquals(Tipos.cluster, novoCluster.getTipo());
        assertEquals(60, novoCluster.getOuro());
    }

    @Test
    void testRoubarNaoFazNadaSeClusterFormado() {
        Criatura minion2 = new Criatura(20, Tipos.minion, 10);
        minion2.setPosX(criaturaMinion.getPosX() + 0.5);

        List<Criatura> lista = new ArrayList<>();
        lista.add(criaturaMinion);
        lista.add(minion2);

        criaturaMinion.roubar(lista);

        // Como o cluster é formado, não ocorre roubo
        assertEquals(1, lista.size());
        assertEquals(Tipos.cluster, lista.get(0).getTipo());
    }

    // ---------------- Testes Baseados em Propriedades ----------------

    @RepeatedTest(20)
    void testCorGeradaEstaDentroDosLimites() {
        Criatura c = new Criatura(30, Tipos.minion, 5);
        Color cor = c.getColor();
        assertTrue(cor.getRed() >= 0 && cor.getRed() <= 255);
        assertTrue(cor.getGreen() >= 0 && cor.getGreen() <= 255);
        assertTrue(cor.getBlue() >= 0 && cor.getBlue() <= 255);
    }
}
