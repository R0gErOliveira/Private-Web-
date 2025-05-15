package br.com.pod.privatejpa.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    //constante para centralizar o nome da unidade de persistência
    // se o nome mudar, precisaremos alterar em um só lugar
    private static final String PERSISTENCE_UNIT = "PrivatePU";

    //Gerenciador de entidades para a comunicação com o banco de dados
    private static EntityManager em;

    //  Fábrica de gerenciadores de entidade para a criação de EntityManager
    private static EntityManagerFactory fabrica;

    // Cria ou retorna o gerenciador de entidades, caso não exista
    public static EntityManager getEntityManager() {
        if (fabrica == null || !fabrica.isOpen()) {
            fabrica = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }

        if (em == null || !em.isOpen()) //cria se em nulo ou se o entity manager foi fechado
        {
            em = fabrica.createEntityManager();
        }

        return em;
    }

    //Fecha o gerenciador de entidades e a fábrica de gerenciadores
    public static void closeEntityManager() {
        if (em.isOpen() && em != null) {
            em.close();
            fabrica.close();
        }

    }

}
