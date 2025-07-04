package test.view;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.view.Estatisticas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstatisticasTest {

    private UsuarioDaoImpl mockUsuarioDao;
    private Estatisticas estatisticas;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    @BeforeEach
    void setup() {
        mockUsuarioDao = mock(UsuarioDaoImpl.class);
        mainPanel = new JPanel();
        cardLayout = new CardLayout();

        estatisticas = new Estatisticas(mainPanel, cardLayout) {
            {
                this.usuarioDAO = mockUsuarioDao;
            }
        };
    }

    @Test
    void testCarregarEstatisticasComUsuarios() throws SQLException {
        Usuario u1 = new Usuario(1, "usuario1", "123", "", 200, 4);
        Usuario u2 = new Usuario(2, "usuario2", "123", "", 100, 2);
        List<Usuario> usuarios = Arrays.asList(u1, u2);

        when(mockUsuarioDao.getAllUsuarios()).thenReturn(usuarios);

        estatisticas.carregarEstatisticas();

        JTable tabela = (JTable) getComponentByClass(estatisticas, JTable.class);
        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();

        assertEquals(2, modelo.getRowCount());
        assertEquals("usuario1", modelo.getValueAt(0, 0));
        assertEquals(200, modelo.getValueAt(0, 1));
        assertEquals(4, modelo.getValueAt(0, 2));
        assertEquals("usuario2", modelo.getValueAt(1, 0));

        JLabel totalLabel = findLabelByPrefix(estatisticas, "Total de Simulações:");
        JLabel mediaSims = findLabelByPrefix(estatisticas, "Média de Simulações");
        JLabel mediaPonts = findLabelByPrefix(estatisticas, "Média de Pontuação");

        assertEquals("Total de Simulações: 6", totalLabel.getText());
        assertEquals("Média de Simulações por Usuário: 3,00", mediaSims.getText());
        assertEquals("Média de Pontuação por Usuário: 150,00", mediaPonts.getText());
    }

    @Test
    void testCarregarEstatisticasSemUsuarios() throws SQLException {
        when(mockUsuarioDao.getAllUsuarios()).thenReturn(Collections.emptyList());

        estatisticas.carregarEstatisticas();

        JLabel totalLabel = findLabelByPrefix(estatisticas, "Total de Simulações:");
        JLabel mediaSims = findLabelByPrefix(estatisticas, "Média de Simulações");
        JLabel mediaPonts = findLabelByPrefix(estatisticas, "Média de Pontuação");

        assertEquals("Total de Simulações: 0", totalLabel.getText());
        assertEquals("Média de Simulações por Usuário: 0.0", mediaSims.getText());
        assertEquals("Média de Pontuação por Usuário: 0.0", mediaPonts.getText());
    }

    @Test
    void testCarregarEstatisticasSQLException() throws SQLException {
        when(mockUsuarioDao.getAllUsuarios()).thenThrow(new SQLException("Erro simulado"));

        estatisticas.carregarEstatisticas();

        JTable tabela = (JTable) getComponentByClass(estatisticas, JTable.class);
        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();

        assertEquals(0, modelo.getRowCount(), "Tabela deve estar vazia em caso de erro SQL.");
    }



    private Component getComponentByClass(Container parent, Class<?> clazz) {
        for (Component comp : parent.getComponents()) {
            if (clazz.isInstance(comp)) return comp;
            if (comp instanceof Container) {
                Component child = getComponentByClass((Container) comp, clazz);
                if (child != null) return child;
            }
        }
        return null;
    }

    private JLabel findLabelByPrefix(Container parent, String prefix) {
        for (Component comp : parent.getComponents()) {
            if (comp instanceof JLabel label && label.getText().startsWith(prefix)) {
                return label;
            } else if (comp instanceof Container container) {
                JLabel found = findLabelByPrefix(container, prefix);
                if (found != null) return found;
            }
        }
        return null;
    }
}
