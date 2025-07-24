package test.controller;

import main.controller.SimulacaoController;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Criatura;
import main.model.Horizonte; // Importe Horizonte
import main.model.Tipos;
import main.model.Usuario;
import main.util.SessionManager;
import main.view.SimulacaoView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SimulacaoControllerTest {

    @Mock
    private SimulacaoView mockView;
    @Mock
    private JPanel mockMainPanel;
    @Mock
    private CardLayout mockCardLayout;
    @Mock
    private UsuarioDaoImpl mockUsuarioDao;

    @Captor
    private ArgumentCaptor<ActionListener> pauseButtonListenerCaptor;
    @Captor
    private ArgumentCaptor<ActionListener> backToMenuButtonListenerCaptor;

    private SimulacaoController simulacaoController;

    private ActionListener pauseButtonListener;
    private ActionListener backToMenuButtonListener;

    @Mock
    private Timer mockTimer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        doNothing().when(mockView).addPauseButtonListener(pauseButtonListenerCaptor.capture());
        doNothing().when(mockView).addBackToMenuButtonListener(backToMenuButtonListenerCaptor.capture());

        simulacaoController = new SimulacaoController(mockView, mockMainPanel, mockCardLayout);

        pauseButtonListener = pauseButtonListenerCaptor.getValue();
        backToMenuButtonListener = backToMenuButtonListenerCaptor.getValue();

        Field usuarioDaoField = SimulacaoController.class.getDeclaredField("usuarioDao");
        usuarioDaoField.setAccessible(true);
        usuarioDaoField.set(simulacaoController, mockUsuarioDao);

        Field timerField = SimulacaoController.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        timerField.set(simulacaoController, mockTimer);

        SessionManager.getInstance().setLoggedInUser(null);
        Horizonte.reset();
    }

    @Test
    @DisplayName("Deve iniciar a simulação com minions e um guardião, e atualizar o usuário")
    void deveIniciarSimulacaoComMinionsEUmGuardiaoEAtualizarUsuario() throws SQLException {
        int nCriaturas = 3;
        Usuario usuario = new Usuario(1, "testUser", "pass", "avatar.png", 0, 0);
        SessionManager.getInstance().setLoggedInUser(usuario);

        simulacaoController.iniciarSimulacao(nCriaturas);


        verify(mockUsuarioDao).updateUsuario(usuario);
        assertEquals(1, usuario.getQtdSimulacoes());


        List<Criatura> criaturasDoController = getCriaturasViaReflection(simulacaoController);
        assertNotNull(criaturasDoController);
        assertEquals(nCriaturas + 1, criaturasDoController.size());

        long minionCount = criaturasDoController.stream().filter(c -> c.getTipo() == Tipos.minion).count();
        long guardiaoCount = criaturasDoController.stream().filter(c -> c.getTipo() == Tipos.guardiao).count();

        assertEquals(nCriaturas, minionCount);
        assertEquals(1, guardiaoCount);


        verify(mockView).setRodadaLabel(1);
        verify(mockView).setPauseButtonText("Pausar simulação");
        verify(mockView).updateSimulationDisplay(anyList());
        verify(mockView).updateScoreboard(anyList(), eq((int) minionCount), eq(0), eq((int) guardiaoCount));

        verify(mockTimer).isRunning();
        verify(mockTimer, never()).stop();
    }

    @Test
    @DisplayName("Deve pausar e retomar a simulação")
    void devePausarERetomarSimulacao() {
        when(mockTimer.isRunning()).thenReturn(true);


        pauseButtonListener.actionPerformed(new ActionEvent(mockView, 0, "pause"));
        verify(mockTimer).stop();
        verify(mockView).setPauseButtonText("Retomar simulação");
        assertTrue(getIsPausedViaReflection(simulacaoController));


        pauseButtonListener.actionPerformed(new ActionEvent(mockView, 0, "pause"));
        verify(mockTimer).start();
        verify(mockView).setPauseButtonText("Pausar simulação");
        assertFalse(getIsPausedViaReflection(simulacaoController));
    }

    @Test
    @DisplayName("Deve parar o timer e navegar para o menu ao clicar em voltar")
    void devePararTimerENavegarParaMenuAoClicarEmVoltar() {
        when(mockTimer.isRunning()).thenReturn(true);

        backToMenuButtonListener.actionPerformed(new ActionEvent(mockView, 0, "back"));

        verify(mockTimer).stop();
        verify(mockCardLayout).show(mockMainPanel, "telaMenu");
    }



    @Test
    @DisplayName("O horizonte deve começar a fechar após a rodada 20 e os limites serem ajustados")
    void oHorizonteDeveComecarAFacharAposRodada20ELimitesSereM_ajustados() {


        simulacaoController.iniciarSimulacao(1);

        ActionListener simulationTickListener = getSimulationTickListenerViaReflection(simulacaoController);


        assertEquals(10, Horizonte.comecoHorizonte);
        assertEquals(80, Horizonte.finalHorizonte);


        setRodadaAtualViaReflection(simulacaoController, 19);

        // Tick para rodada 20
        simulationTickListener.actionPerformed(new ActionEvent(mockTimer, 0, "tick"));
        assertEquals(20, getRodadaAtualViaReflection(simulacaoController));
        assertFalse(getFecharHorizonteViaReflection(simulacaoController));
        assertEquals(10, Horizonte.comecoHorizonte);
        assertEquals(80, Horizonte.finalHorizonte);

        // Tick para rodada 21
        simulationTickListener.actionPerformed(new ActionEvent(mockTimer, 0, "tick"));
        assertEquals(21, getRodadaAtualViaReflection(simulacaoController));
        assertTrue(getFecharHorizonteViaReflection(simulacaoController));
        assertEquals(11, Horizonte.comecoHorizonte);
        assertEquals(79, Horizonte.finalHorizonte);

        // Tick para rodada 22
        simulationTickListener.actionPerformed(new ActionEvent(mockTimer, 0, "tick"));
        assertEquals(22, getRodadaAtualViaReflection(simulacaoController));
        assertTrue(getFecharHorizonteViaReflection(simulacaoController));
        assertEquals(12, Horizonte.comecoHorizonte);
        assertEquals(78, Horizonte.finalHorizonte);
    }

    @Test
    @DisplayName("A simulação deve finalizar quando não há minions")
    void aSimulacaoDeveFinalizarQuandoNaoHaMinions() {
        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.guardiao, 0),
                new Criatura(2, Tipos.cluster, 100)
        ));

        ActionListener simulationTickListener = getSimulationTickListenerViaReflection(simulacaoController);
        simulationTickListener.actionPerformed(new ActionEvent(mockTimer, 0, "tick"));

        verify(mockTimer).stop();
        verify(mockView).showMessage(anyString(), anyString(), eq(JOptionPane.INFORMATION_MESSAGE));
    }

    @Test
    @DisplayName("A simulação deve finalizar quando há 0 clusters e 1 minion")
    void aSimulacaoDeveFinalizarQuandoHaZeroClustersEUmMinion() {
        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.minion, 100),
                new Criatura(2, Tipos.guardiao, 0)
        ));

        ActionListener simulationTickListener = getSimulationTickListenerViaReflection(simulacaoController);
        simulationTickListener.actionPerformed(new ActionEvent(mockTimer, 0, "tick"));

        verify(mockTimer).stop();
        verify(mockView).showMessage(anyString(), anyString(), eq(JOptionPane.INFORMATION_MESSAGE));
    }

    @Test
    @DisplayName("Deve premiar guardião com pontos extras e exibir mensagem de vitória do guardião")
    void devePremiarGuardiaoComPontosExtrasEExibirVitoriaGuardiao() throws SQLException {
        Usuario usuario = new Usuario(1, "testUser", "pass", "avatar.png", 10, 5);
        SessionManager.getInstance().setLoggedInUser(usuario);

        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.guardiao, 0)
        ));

        invokeHandleSimulationEnd(simulacaoController);

        assertEquals(14, usuario.getPontuacao());
        verify(mockUsuarioDao).updateUsuario(usuario);
        verify(mockView).showMessage(
                "Simulação concluída! O guardião venceu!",
                "Fim da Simulação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Test
    @DisplayName("Deve exibir mensagem de vitória dos clusters e atualizar pontuação base")
    void deveExibirVitoriaClustersEAtualizarPontuacaoBase() throws SQLException {
        Usuario usuario = new Usuario(1, "testUser", "pass", "avatar.png", 10, 5);
        SessionManager.getInstance().setLoggedInUser(usuario);


        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.guardiao, 0),
                new Criatura(2, Tipos.cluster, 200)
        ));

        invokeHandleSimulationEnd(simulacaoController);

        assertEquals(11, usuario.getPontuacao());
        verify(mockUsuarioDao).updateUsuario(usuario);
        verify(mockView).showMessage(
                "Simulação concluída! Os Clusters venceram!",
                "Fim da Simulação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Test
    @DisplayName("Não deve atualizar pontuação se não houver usuário logado no fim da simulação")
    void naoDeveAtualizarPontuacaoSeNaoHouverUsuarioLogadoNoFimDaSimulacao() throws SQLException {
        SessionManager.getInstance().setLoggedInUser(null);

        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.guardiao, 0)
        ));

        invokeHandleSimulationEnd(simulacaoController);

        verify(mockUsuarioDao, never()).updateUsuario(any(Usuario.class));
        verify(mockView).showMessage(
                "Simulação concluída! O guardião venceu!",
                "Fim da Simulação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Test

    void deveLidarComSQLExceptionAoAtualizarUsuarioNaInicializacao() throws SQLException {
        Usuario usuario = new Usuario(1, "testUser", "pass", "avatar.png", 0, 0);
        SessionManager.getInstance().setLoggedInUser(usuario);

        doThrow(new SQLException("Erro de DB na inicialização")).when(mockUsuarioDao).updateUsuario(usuario);

        simulacaoController.iniciarSimulacao(5);

        verify(mockUsuarioDao).updateUsuario(usuario);
        verify(mockView, never()).showMessage(anyString(), anyString(), anyInt());
        verify(mockTimer).isRunning();
    }

    @Test
    @DisplayName("Deve lidar com SQLException ao atualizar usuário no fim da simulação")
    void deveLidarComSQLExceptionAoAtualizarUsuarioNoFimDaSimulacao() throws SQLException {
        Usuario usuario = new Usuario(1, "testUser", "pass", "avatar.png", 10, 5);
        SessionManager.getInstance().setLoggedInUser(usuario);

        setCriaturasViaReflection(simulacaoController, Arrays.asList(
                new Criatura(1, Tipos.guardiao, 0)
        ));

        doThrow(new SQLException("Erro de DB no fim da simulação")).when(mockUsuarioDao).updateUsuario(usuario);

        invokeHandleSimulationEnd(simulacaoController);

        verify(mockUsuarioDao).updateUsuario(usuario);
        verify(mockView).showMessage(
                "Simulação concluída! O guardião venceu!",
                "Fim da Simulação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private List<Criatura> getCriaturasViaReflection(SimulacaoController controller) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("criaturas");
            field.setAccessible(true);
            return (List<Criatura>) field.get(controller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao acessar o campo 'criaturas' via reflection: " + e.getMessage());
            return null;
        }
    }

    private void setCriaturasViaReflection(SimulacaoController controller, List<Criatura> criaturas) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("criaturas");
            field.setAccessible(true);
            field.set(controller, criaturas);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao definir o campo 'criaturas' via reflection: " + e.getMessage());
        }
    }

    private boolean getIsPausedViaReflection(SimulacaoController controller) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("isPaused");
            field.setAccessible(true);
            return (boolean) field.get(controller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao acessar o campo 'isPaused' via reflection: " + e.getMessage());
            return false;
        }
    }

    private ActionListener getSimulationTickListenerViaReflection(SimulacaoController controller) {
        try {
            Class<?> listenerClass = Class.forName("main.controller.SimulacaoController$SimulationTickListener");
            return (ActionListener) listenerClass.getDeclaredConstructor(SimulacaoController.class).newInstance(controller);
        } catch (Exception e) {
            fail("Falha ao obter SimulationTickListener via reflection: " + e.getMessage());
            return null;
        }
    }

    private int getRodadaAtualViaReflection(SimulacaoController controller) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("rodadaAtual");
            field.setAccessible(true);
            return (int) field.get(controller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao acessar o campo 'rodadaAtual' via reflection: " + e.getMessage());
            return -1;
        }
    }

    private void setRodadaAtualViaReflection(SimulacaoController controller, int rodada) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("rodadaAtual");
            field.setAccessible(true);
            field.set(controller, rodada);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao definir o campo 'rodadaAtual' via reflection: " + e.getMessage());
        }
    }

    private boolean getFecharHorizonteViaReflection(SimulacaoController controller) {
        try {
            Field field = SimulacaoController.class.getDeclaredField("fecharHorizonte");
            field.setAccessible(true);
            return (boolean) field.get(controller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao acessar o campo 'fecharHorizonte' via reflection: " + e.getMessage());
            return false;
        }
    }

    private void invokeHandleSimulationEnd(SimulacaoController controller) {
        try {
            java.lang.reflect.Method method = SimulacaoController.class.getDeclaredMethod("handleSimulationEnd");
            method.setAccessible(true);
            method.invoke(controller);
        } catch (Exception e) {
            fail("Falha ao invocar handleSimulationEnd via reflection: " + e.getMessage());
        }
    }
}