package br.com.pod.privatejpa.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class PedidoDAO {

    private EntityManager entityManager;

    public PedidoDAO() {
        // Criação manual do EntityManagerFactory e EntityManager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PrivatePU");
        this.entityManager = emf.createEntityManager();
    }

    public <T> void cadastrar(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.flush(); // Força a sincronização com o banco de dados
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();  // Imprime o stack trace para diagnóstico
            throw new RuntimeException("Erro ao cadastrar: " + e.getMessage(), e); // Lança uma RuntimeException
        }
        // REMOVER O FINALLY POIS NÃO FAZ SENTIDO FECHAR O ENTITYMANAGER SEMPRE.
    }

    // Método para verificar se um pedido já foi finalizado
    public boolean verificarPedidoFinalizado(String placaVeiculo) {
        try {
            String textoQuery = "SELECT p FROM Pedido p "
                    + "JOIN p.cliente c " // Join com o cliente
                    + "JOIN c.veiculo v " // Join com o veículo do cliente
                    + "WHERE v.placa = :placa AND p.dataSaida IS NOT NULL";

            TypedQuery<Pedido> consulta = entityManager.createQuery(textoQuery, Pedido.class);
            consulta.setParameter("placa", placaVeiculo);

            Pedido pedidoExistente = consulta.getResultList().stream().findFirst().orElse(null);

            return pedidoExistente != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Método responsável por excluir um Pedido baseado no ID
    public void excluirPedidoPorId(int id) {
        Pedido pedido = entityManager.find(Pedido.class,
                id);

        if (pedido != null) {
            entityManager.remove(pedido);
        } else {
            System.out.println("Pedido não encontrado com o id: " + id);
        }
    }

    // Método responsável por buscar um Pedido baseado no ID
    public Pedido
            buscarPedidoPorId(int id) {
        try {
            return entityManager.find(Pedido.class,
                    id);
        } catch (NoResultException e) {
            return null; // Retorna null se não encontrar nenhum pedido
        }
    }

    // Método responsável por listar todos os pedidos que atendem à pesquisa (nome do cliente ou placa do veículo)
    public List<Pedido> listarPedidos(String pesquisa) {
        List<Pedido> pedidos = null;

        try {
            String textoQuery = "SELECT p FROM Pedido p "
                    + "JOIN FETCH p.cliente c "
                    + "JOIN FETCH p.veiculo v "
                    + "WHERE (:pesquisa IS NULL OR c.nome LIKE :pesquisa OR v.placa LIKE :pesquisa)";

            TypedQuery<Pedido> consulta = entityManager.createQuery(textoQuery, Pedido.class
            );
            consulta.setParameter("pesquisa", pesquisa.isEmpty() ? null : "%" + pesquisa + "%");

            pedidos = consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pedidos;
    }
}
