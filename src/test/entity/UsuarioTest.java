package test.entity;

import main.entity.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class UsuarioTest {

    @Test
    void testConstructorSemId() {
        Usuario usuario = new Usuario("login123", "senha123", "avatar.png", 10, 5);

        assertEquals("login123", usuario.getLogin());
        assertEquals("senha123", usuario.getSenha());
        assertEquals("avatar.png", usuario.getAvatarURL());
        assertEquals(10, usuario.getPontuacao());
        assertEquals(5, usuario.getQtdSimulacoes());
    }

    @Test
    void testConstructorComId() {
        Usuario usuario = new Usuario(1, "user", "pass", "url", 20, 7);

        assertEquals(1, usuario.getId());
        assertEquals("user", usuario.getLogin());
        assertEquals("pass", usuario.getSenha());
        assertEquals("url", usuario.getAvatarURL());
        assertEquals(20, usuario.getPontuacao());
        assertEquals(7, usuario.getQtdSimulacoes());
    }

    @Test
    void testSettersAndGetters() {
        Usuario usuario = new Usuario("u", "s", "a", 0, 0);

        usuario.setId(99);
        usuario.setLogin("rafael");
        usuario.setSenha("novaSenha");
        usuario.setAvatarURL("newAvatar.png");
        usuario.setPontuacao(100);
        usuario.setQtdSimulacoes(42);

        assertEquals(99, usuario.getId());
        assertEquals("rafael", usuario.getLogin());
        assertEquals("novaSenha", usuario.getSenha());
        assertEquals("newAvatar.png", usuario.getAvatarURL());
        assertEquals(100, usuario.getPontuacao());
        assertEquals(42, usuario.getQtdSimulacoes());
    }
}
