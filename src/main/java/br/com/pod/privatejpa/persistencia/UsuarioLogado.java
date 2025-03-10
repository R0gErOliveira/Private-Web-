package br.com.pod.privatejpa.persistencia;

public class UsuarioLogado {

    //Armazena o usuário logado na sessão
    private static Usuario usuario;

    // Define o usuário logado na sessão
    public static void setUsuario(Usuario user) {
        usuario = user;
    }

    //Obtém o usuário logado na sessão
    public static Usuario getUsuario() {
        return usuario;
    }

    //Obtém o tipo de usuário logado na sessão
    public static String getTipoUsuario() {
        return (usuario != null) ? usuario.getTipo() : null;
    }

    // Obtém o nome do usuário logado na sessão
    public static String getNomeUsuario() {
        return (usuario != null) ? usuario.getNome() : "Desconhecido";
    }

}
