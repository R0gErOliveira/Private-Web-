package br.com.pod.privatejpa.persistencia;


import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ClienteDAO {

    // Método responsável por cadastrar os dados que forem inseridos na tela no
    // banco de dados
    // Método genérico responsável por cadastrar qualquer entidade no banco de dados podendo usar tipos diferentes de clases 
    public <T> void cadastrar(T entity) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    public void excluirClienteEVeiculoPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);

            if (cliente != null) {
                // A exclusão do Cliente vai automaticamente excluir o Veículo devido ao CascadeType.ALL
                em.remove(cliente);
            } else {
                System.out.println("Cliente não encontrado com o id: " + id);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir: " + e.getMessage(), e); // Lança uma RuntimeException
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    public Cliente buscarClientePorPlaca(String placa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT v.cliente FROM Veiculo v WHERE LOWER(TRIM(v.placa)) = LOWER(TRIM(:placa))", Cliente.class);
            query.setParameter("placa", placa);

            return query.getSingleResult(); // Retorna o cliente encontrado
        } catch (NoResultException e) {
            return null; // Retorna null se não encontrar nenhum cliente
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Cliente> listarRelatorio(String pesquisa) {

        EntityManager em = JPAUtil.getEntityManager();
        List<Cliente> clientes = null;

        try {
            // Ajustando a consulta para retornar a entidade Cliente
            String textoQuery = "SELECT c FROM Cliente c "
                    + "JOIN FETCH c.veiculo v " // JOIN FETCH carrega os dados do Veículo junto
                    + "WHERE (:pesquisa IS NULL OR c.nome LIKE :pesquisa OR v.placa LIKE :pesquisa)";

            TypedQuery<Cliente> consulta = em.createQuery(textoQuery, Cliente.class);

            // Definindo o parâmetro da pesquisa
            consulta.setParameter("pesquisa", pesquisa.isEmpty() ? null : "%" + pesquisa + "%");

            clientes = consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return clientes;

    }

    public Pedido buscarPedidoPorClienteId(Long clienteId) {
        EntityManager em = JPAUtil.getEntityManager();  // Adicionando o EntityManager corretamente
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId", Pedido.class);
            query.setParameter("clienteId", clienteId);
            return query.getSingleResult(); // Retorna o pedido encontrado
        } catch (NoResultException e) {
            return null; // Retorna null se não encontrar nenhum pedido
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Lidar com outras possíveis exceções
        } finally {
            em.close(); // Fechar o EntityManager ao final
        }
    }

    public Cliente buscarClientePorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE LOWER(c.nome) = LOWER(:nome)", Cliente.class);
            query.setParameter("nome", nome);

            return query.getSingleResult(); // Retorna o cliente encontrado
        } catch (NoResultException e) {
            return null; // Retorna null se não encontrar nenhum cliente
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Cliente> listarRelatorioPorMes(int mesPesquisa) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Cliente> clientes = null;

        try {
            // Consulta JPQL para filtrar apenas pelo mês da data de saída
            String textoQuery = "SELECT DISTINCT c FROM Cliente c "
                    + "LEFT JOIN FETCH c.veiculo v "
                    + "JOIN FETCH c.pedidos p " // JOIN para garantir apenas clientes com pedidos
                    + "WHERE EXTRACT(MONTH FROM p.dataSaida) = :mesPesquisa";

            TypedQuery<Cliente> consulta = em.createQuery(textoQuery, Cliente.class);
            consulta.setParameter("mesPesquisa", mesPesquisa);

            clientes = consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return clientes;
    }
}
