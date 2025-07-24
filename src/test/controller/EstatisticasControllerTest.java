package test.controller;

import main.controller.EstatisticasController;
import main.controller.RegistroController;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.view.EstatisticasView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class EstatisticasControllerTest {


    @Mock
    private EstatisticasView mockView;
    @Mock
    private UsuarioDaoImpl mockUsuarioDAO;
    @Mock
    private JPanel mockMainPanel;
    @Mock
    private CardLayout mockCardLayout;


    @InjectMocks
    private EstatisticasController estatisticasController;

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.openMocks(this);
        estatisticasController = new EstatisticasController(mockView, mockMainPanel, mockCardLayout);

        Field daoField = EstatisticasController.class.getDeclaredField("usuarioDAO");
        daoField.setAccessible(true);
        daoField.set(estatisticasController, mockUsuarioDAO);
    }

    @Test
    void deveCarregarEstatisticasComMultiplosUsuarios() throws SQLException {
        List<Usuario> usuarios = Arrays.asList(
                new Usuario("usuario1", "senha1", "assets/avatars/Isabelle.jpg", 100, 5),
                new Usuario("usuario2", "senha2","assets/avatars/Isabelle.jpg", 200, 10),
                new Usuario("usuario3", "senha3","assets/avatars/Isabelle.jpg", 300, 15)
        );


        when(mockUsuarioDAO.getAllUsuarios()).thenReturn(usuarios);

        estatisticasController.carregarEstatisticas();
        verify(mockView).clearTable();
        verify(mockView, times(3)).addTableRow(any(Object[].class));


        verify(mockView).setTotalSimulationsLabel(30L); // 5 + 10 + 15 = 30
        verify(mockView).setAvgSimulationsLabel(10.0); // 30 / 3 = 10.0
        verify(mockView).setAvgScoreLabel(200.0); // (100 + 200 + 300) / 3 = 200.0

        verify(mockView, never()).showMessage(anyString(), anyString(), anyInt());
    }

    @Test
    void deveCarregarEstatisticasComNenhumUsuario() throws SQLException {

        when(mockUsuarioDAO.getAllUsuarios()).thenReturn(Collections.emptyList());

        estatisticasController.carregarEstatisticas();

        verify(mockView).clearTable();
        verify(mockView, never()).addTableRow(any(Object[].class));
        verify(mockView).setTotalSimulationsLabel(0L);
        verify(mockView).setAvgSimulationsLabel(0.0);
        verify(mockView).setAvgScoreLabel(0.0);
        verify(mockView, never()).showMessage(anyString(), anyString(), anyInt());
    }

    @Test

    void deveLidarComExcecaoSQLException() throws SQLException {
        String errorMessage = "Erro de conexão com o banco de dados";
        when(mockUsuarioDAO.getAllUsuarios()).thenThrow(new SQLException(errorMessage));

        estatisticasController.carregarEstatisticas();


        verify(mockView).clearTable();
        verify(mockView).showMessage(
                eq("Erro ao carregar estatísticas do banco de dados: " + errorMessage),
                eq("Erro de Banco de Dados"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(mockView, never()).setTotalSimulationsLabel(anyLong());
        verify(mockView, never()).setAvgSimulationsLabel(anyDouble());
        verify(mockView, never()).setAvgScoreLabel(anyDouble());
    }

    @Test

    void deveRetornarParaTelaDeMenu() {

        estatisticasController.new BackButtonListener().actionPerformed(mock(java.awt.event.ActionEvent.class));

        verify(mockCardLayout).show(mockMainPanel, "telaMenu");
    }
}