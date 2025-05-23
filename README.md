# Simulação de criaturas saltitantes



Esse trabalho consiste em uma aplicação que simula n criaturas dentro de um horizonte que se movimentam e roubam ouro de outras criaturas

---
## Descrição


- Cada criatura vai iniciar em uma posição aleatória dentro do horizonte delimitado e vai iniciar com uma quantidade fixa de ouro(1000000).
- Cada criatura vai se movimentar uma vez por rodada, a movimentação é determinada por um número gerado aleatoriamente entre -1 e 1.
- Cada criatura vai roubar metade do ouro da criatura mais próxima dele.
- A simulação encerra quando o número de rodadas definido chegar ao fim.









---
## Testes da classe Criatura

### **Configuração Inicial (`@BeforeEach`)**

Antes de cada teste, são criadas três instâncias da classe `Criatura` com identificadores `0`, `1` e `2`. Além disso, todas as criaturas são armazenadas em um array chamado `criaturas` para facilitar os testes que envolvem múltiplos objetos.

```java
@BeforeEach
void setUp() {
    criatura1 = new Criatura(0);
    criatura2 = new Criatura(1);
    criatura3 = new Criatura(2);
    criaturas = new Criatura[]{criatura1, criatura2, criatura3};
}
```

### 1. `testInicializacao`

**Objetivo:**  
Verificar a correta inicialização de uma `Criatura`.
```java
@Test
    void testInicializacao() {
        assertEquals(0, criatura1.getId());
        assertEquals(1000000, criatura1.getOuro());
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
    }
```
**Validações:**
- O ID inicial é configurado corretamente.
- O ouro inicial é de **1.000.000**.
- A posição X inicial está dentro dos limites definidos por `Constantes`.

---

### 2. `testGetId`

**Objetivo:**  
Verificar se a alteração e recuperação do ID da `Criatura` funcionam conforme o esperado.

```java
 void testGetId(){
        criatura1.setId(2);
        assertEquals(2,criatura1.getId());
    }
```

**Validação:**
- Após `setId(2)`, o `getId()` deve retornar `2`.

---

### 3. `testGetColor`

**Objetivo:**  
Garantir que o atributo `Color` pode ser atribuído e recuperado corretamente.

```java
void testGetColor(){
        criatura1.setColor(new Color(2,2,2));
        assertEquals(new Color(2,2,2),criatura1.getColor());
    }
```

**Validação:**
- Após `setColor(new Color(2,2,2))`, `getColor()` deve retornar a mesma cor.

---

### 4. `testMovimentoDentroDosLimites`

**Objetivo:**  
Verificar se o método `move()` mantém a `Criatura` dentro dos limites horizontais definidos.
```java
 @Test
    void testMovimentoDentroDosLimites() {
        for (int i = 0; i < 1000; i++) {
            criatura1.move();
            assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
        }
    }
```
**Validação:**
- Após **1000 movimentos**, a posição X da `Criatura` deve permanecer dentro de `Constantes.comecoHorizonte` e `Constantes.finalHorizonte`.

---

### 5. `testResetarPosicaoForaDoHorizonte`

**Objetivo:**  
Garantir que, ao definir uma posição fora do horizonte, o sistema corrija automaticamente para manter dentro dos limites.

```java
@Test
    void testPosicaoForaDoHorizonte() {
        criatura1.setPosX(Constantes.comecoHorizonte-1);
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
        criatura1.setPosX(Constantes.finalHorizonte+1);
        assertTrue(criatura1.getPosX() >= Constantes.comecoHorizonte && criatura1.getPosX() <= Constantes.finalHorizonte);
    }
```
**Validações:**
- Ajuste automático se a posição for menor que o início do horizonte.
- Ajuste automático se a posição for maior que o final do horizonte.

---

### 6. `testCriaturaMaisProxima`

**Objetivo:**  
Testar o método `criaturaMaisProx()` que identifica a `Criatura` mais próxima da atual.

```java
@Test
    void testCriaturaMaisProxima() {
        criatura1.setPosX(11);
        criatura2.setPosX(12);
        criatura3.setPosX(50);
        Criatura maisProxima = criatura1.criaturaMaisProx(criaturas);
        assertEquals(criatura2.getId(), maisProxima.getId());
    }
```

**Cenário:**
- `criatura1` na posição `11`.
- `criatura2` na posição `12`.
- `criatura3` na posição `50`.

**Validação:**
- A `Criatura` mais próxima de `criatura1` deve ser `criatura2`.

---

### 7. `testRouboReduzEOuroDaVitimaEPassaParaLadrao`

**Objetivo:**  
Verificar a lógica de roubo entre criaturas.

```java
@Test
    void testRouboValido() {
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
```

**Cenário:**
- Todas as criaturas começam com **1.000.000** de ouro.
- `criatura1` tenta roubar a criatura mais próxima (`criatura2`).

**Validações:**
- A vítima (`criatura2`) perde metade de seu ouro (**500.000**).
- O ladrão (`criatura1`) ganha o valor perdido pela vítima (**500.000**).
- As outras criaturas permanecem inalteradas.

---

## Testes da classe Simulação

A função iniciarSimulacao deve retornar 0 se tudo estiver certo e 1 se alguma coisa tiver errada.

### 1. `testIniciarSimulacaoValoresValidos`

**Objetivo:**  
Verificar a correta inicialização da simulação`.
```java
 @Test
    void testIniciarSimulacaoValoresValidos() {
        int result = simulacao.iniciarSimulacao(5, 10);
        assertEquals(0, result);
        assertEquals(10, simulacao.nCriaturas);
        assertEquals(5, simulacao.rodadas);
        assertNotNull(simulacao.criaturas);
    }
```
**Validações:**
- Confere se a simulação rodou corretamente.
- O número de criaturas está correto.
- O número de rodadas está correto.
- Inicializou o vetor de criaturas.

---

### 2. `testIniciarSimulacaoCriaturasMinimo`

**Objetivo:**  
Verificar se a simulação roda corretamente com o mínimo de criaturas

```java
 @Test
    void testIniciarSimulacaoCriaturasMinimo() {
        int result = simulacao.iniciarSimulacao(5, 2);
        assertEquals(0, result);
    }
```

**Validação:**
- Confere se a simulação rodou corretamente.

---

### 3. `testIniciarSimulacaoCriaturasMaximo`

**Objetivo:**  
Verificar se a simulação roda corretamente com o máximo de criaturas

```java
@Test
    void testIniciarSimulacaoCriaturasMaximo() {
        int result = simulacao.iniciarSimulacao(5, 1000);
        assertEquals(0, result);
    }
```

**Validação:**
- Confere se a simulação rodou corretamente.

---

### 4. `testMenosDe2Criaturas`

**Objetivo:**  
Verificar se a simulação para ao rodar com menos de 2 criaturas

```java

@Test
    void testMenosDe2Criaturas(){
        assertEquals(1,simulacao.iniciarSimulacao(5,1));
    }
```

**Validação:**
- Confere se a simulação para se o mínimo de criaturas não for atingido.

---

### 5. `testMaisDe1000Criaturas`

**Objetivo:**  
Verificar se a simulação para ao rodar com mais de 1000 criaturas

```java
@Test
    void testMaisDe1000Criaturas(){
        assertEquals(1,simulacao.iniciarSimulacao(5,1001));
    }
```

**Validação:**
- Confere se a simulação para se o máximo de criaturas for ultrapassado.

---

### 6. `testMenosDe1Rodada`

**Objetivo:**  
Verificar se a simulação para ao rodar com menos de 1 rodada

```java
 @Test
    void testMenosDe1Rodada(){
        assertEquals(1,simulacao.iniciarSimulacao(0,50));
    }
```

**Validação:**
- Confere se a simulação para se o mínimo de rodadas não for atingido.

---

### 7. `testPausarRetomarSimulaca`

**Objetivo:**  
Verificar se o botão de pausar simulação está funcionando corretamente

```java
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
```

**Validação:**
- Confere se o botão de pausar está funcionando.

---




##  Tecnologias Utilizadas

- **Java 17**
- **JUnit 5**
- **assertJ**
- **Swing**

---

##  Cobertura dos Testes

Esta suíte cobre:

- Inicialização e atributos básicos (`ID`, `ouro`, `posição`, `cor`).
- Regras de movimentação e limites.
- Interação entre objetos (`criaturaMaisProx` e `roubar`).

---





