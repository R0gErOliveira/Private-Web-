package br.com.pod.privatejpa.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ClienteRepositorio {

    // cadastra um novo Cliente no banco de dados 
    public void cadastrar(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    // exclui um Cliente no banco com base no id 
    public void excluirCliente(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                em.remove(cliente); // Cascade já remove Veiculo e Pedidos
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao excluir cliente", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    // Busca um cliente pela placa do veículo associado 
    public Cliente buscarPorPlaca(String placa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT v.cliente FROM Veiculo v WHERE LOWER(TRIM(v.placa)) = LOWER(TRIM(:placa))", Cliente.class);
            query.setParameter("placa", placa);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // Busca um Cliente por nome ignorando letras maísculas ou minusculas 
    public Cliente buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE LOWER(c.nome) = LOWER(:nome)", Cliente.class);
            query.setParameter("nome", nome);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // Criado apenas para teste 
    public List<Cliente> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c", Cliente.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Criado apenas para teste 
    public double getTotalGasto(Cliente cliente) {
        return cliente.getPedidos().stream()
                .mapToDouble(Pedido::getValorTotal)
                .sum();
    }

}
