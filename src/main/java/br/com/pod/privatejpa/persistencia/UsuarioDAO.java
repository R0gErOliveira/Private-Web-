package br.com.pod.privatejpa.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Método responsável por verificar se um usuário existe no banco de dados
    //com base no login e senha informados, seos dados estiverem corretos ele
    //retorna os dados do usuário
    
    
    public static Usuario validarUsuario(Usuario usuario) throws SQLException {
        String sql = "SELECT login, senha, tipo, nome FROM usuario WHERE login = ? AND senha = ?";

        try (Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbprivate", "root", "ROGERReM436285"); PreparedStatement statement = conexao.prepareStatement(sql)) {

            statement.setString(1, usuario.getLogin());
            statement.setString(2, usuario.getSenha());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Criar e retornar o usuário encontrado com o campo nome e tipo
                Usuario usuarioEncontrado = new Usuario(rs.getString("login"), rs.getString("senha"), rs.getString("tipo"));
                usuarioEncontrado.setLogin(rs.getString("login"));
                usuarioEncontrado.setSenha(rs.getString("senha"));
                usuarioEncontrado.setTipo(rs.getString("tipo"));
                usuarioEncontrado.setNome(rs.getString("nome")); // Atribuindo o nome ao objeto

                return usuarioEncontrado;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao validar usuário: " + ex.getMessage());
        }

        return null; // Retorna null caso o usuário não seja encontrado
    }
}
