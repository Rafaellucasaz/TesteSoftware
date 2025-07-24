package main.model;

public class Usuario {
    private int id;
    private String login;
    private String senha;
    private String avatarURL;
    private int pontuacao;
    private int qtdSimulacoes;


    public Usuario(String login, String senha, String avatarURL, int pontuacao, int qtdSimulacoes) {
        this.login = login;
        this.senha = senha;
        this.avatarURL = avatarURL;
        this.pontuacao = pontuacao;
        this.qtdSimulacoes = qtdSimulacoes;
    }

    public Usuario(int id, String login, String senha, String avatarURL, int pontuacao, int qtdSimulacoes) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.avatarURL = avatarURL;
        this.pontuacao = pontuacao;
        this.qtdSimulacoes = qtdSimulacoes;
    }

    public Usuario() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarUrl) {
        this.avatarURL = avatarUrl;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getQtdSimulacoes() {
        return qtdSimulacoes;
    }

    public void setQtdSimulacoes(int qtdSimulacoes) {
        this.qtdSimulacoes = qtdSimulacoes;
    }
}
