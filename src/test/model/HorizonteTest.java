package test.model;

import main.model.Horizonte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HorizonteTest {

    @BeforeEach
    void setUp() {

        Horizonte.comecoHorizonte = 10;
        Horizonte.finalHorizonte = 80;
    }

    @Test
    @DisplayName("Deve redefinir os valores do horizonte para os padrões")
    void deveRedefinirValoresDoHorizonteParaPadroes() {

        Horizonte.reset();


        assertEquals(10, Horizonte.comecoHorizonte);
        assertEquals(80, Horizonte.finalHorizonte);
    }

    @Test
    @DisplayName("Deve ter os valores padrão corretos no início")
    void deveTerValoresPadraoCorretosNoInicio() {

        assertEquals(10, Horizonte.comecoHorizonte);
        assertEquals(80, Horizonte.finalHorizonte);
    }
}