package br.com.pod.privatejpa.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PedidoRepositorio {

    private final EntityManager entityManager;

    public PedidoRepositorio(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void salvar(Pedido pedido) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(pedido);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar o pedido", e);
        }
    }

    // busca um pedido no banco de dados pelo id 
    public Pedido buscarPorId(int id) {
        return entityManager.find(Pedido.class, id);
    }

    // exclui um pedido no banco dados pelo id se ele existir 
    public void excluirPorId(int id) {
        Pedido pedido = buscarPorId(id);
        if (pedido != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(pedido);
            entityManager.getTransaction().commit();
        }
    }

    // verifica se existe um pedido finalizado com data de saída para determinada placa 
    public boolean pedidoFinalizadoPorPlaca(String placa) {
        try {
            String jpql = "SELECT COUNT(p) FROM Pedido p "
                    + "JOIN p.cliente c "
                    + "JOIN c.veiculo v "
                    + "WHERE v.placa = :placa AND p.dataSaida IS NOT NULL";
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("placa", placa)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // lista pedido com base me pesquisa por nome cliente ou placa do veículo 
    public List<Pedido> listarPorPesquisa(String pesquisa) {
        String jpql = "SELECT p FROM Pedido p "
                + "JOIN FETCH p.cliente c "
                + "JOIN FETCH c.veiculo v "
                + "WHERE (:pesquisa IS NULL OR c.nome LIKE :pesquisa OR v.placa LIKE :pesquisa)";
        TypedQuery<Pedido> query = entityManager.createQuery(jpql, Pedido.class);
        query.setParameter("pesquisa", pesquisa == null || pesquisa.isEmpty() ? null : "%" + pesquisa + "%");
        return query.getResultList();
    }

    // busca um pedido associado a um cliente específico, com base no id do cliente 
    public Pedido buscarPorClienteId(Long clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId", Pedido.class);
            query.setParameter("clienteId", clienteId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

}
