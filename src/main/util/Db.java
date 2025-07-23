package main.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Db {
    // A URL para o H2 em modo embutido. O banco de dados será um arquivo chamado 'simulacaocriaturas'
    // no diretório onde a aplicação está sendo executada.
    // DB_CLOSE_DELAY=-1 mantém o banco de dados aberto enquanto a JVM estiver ativa,
    // o que é útil para evitar que ele seja fechado acidentalmente entre operações.
    private static final String URL = "jdbc:h2:./simulacaocriaturas;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa"; // Usuário padrão para H2
    private static final String PASSWORD = ""; // Senha padrão (geralmente vazia) para H2
    private static final String DB_NAME = "simulacaocriaturas"; // Nome lógico do banco de dados (usado na URL)

    /**
     * Cria (ou conecta a) o banco de dados H2 e a tabela 'usuarios'.
     */
    public static void createDatabase() {
        Connection connection = null;
        Statement statement = null;
        try {
            // Não é necessário criar o banco de dados explicitamente com H2 embutido,
            // ele é criado automaticamente se não existir quando a conexão é estabelecida.
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado ao H2 com sucesso! (Banco de dados: '" + DB_NAME + "')");

            statement = connection.createStatement();

            // SQL para criar a tabela 'usuarios'
            // H2 usa AUTO_INCREMENT para chaves primárias auto-incrementais
            String createTableUsuariosSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," + // H2 usa INT e AUTO_INCREMENT
                    "login VARCHAR(100) NOT NULL," +
                    "senha VARCHAR(100) NOT NULL," +
                    "avatarURL VARCHAR NOT NULL," +
                    "pontuacao INT," + // H2 usa INT para inteiros
                    "qtdSimulacoes INT" + // H2 usa INT para inteiros
                    ");";
            statement.executeUpdate(createTableUsuariosSQL);
            System.out.println("Tabela 'usuarios' criada (ou já existe) com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou executar operação no banco de dados H2:");
            e.printStackTrace();
        } finally {
            // Garante que o Statement e a Connection sejam fechados, mesmo se ocorrer um erro.
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                    System.out.println("Conexão H2 e Statement fechados.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos do banco de dados H2:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Retorna uma nova conexão com o banco de dados H2.
     * @return Uma conexão JDBC com o banco de dados H2.
     * @throws SQLException Se ocorrer um erro ao obter a conexão.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carregar o driver H2 (geralmente não é estritamente necessário em versões mais recentes do JDBC,
            // mas é uma boa prática para garantir).
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do H2 não encontrado. Verifique sua dependência Maven.");
            throw new SQLException("Driver JDBC não encontrado", e);
        }
        // Retorna uma nova conexão.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}