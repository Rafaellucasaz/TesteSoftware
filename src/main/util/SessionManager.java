package main.util;
import main.model.Usuario;
public class SessionManager {
    private static SessionManager instance;
    private Usuario loggedInUser;

    private SessionManager() {

    }


    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(Usuario user) {
        this.loggedInUser = user;

    }

    public Usuario getLoggedInUser() {
        return loggedInUser;
    }


    public void logout() {
        this.loggedInUser = null;
        System.out.println("Usuário deslogado.");
    }


    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
