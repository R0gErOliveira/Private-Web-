package br.com.pod.privatejpa.servico;

import br.com.pod.privatejpa.PrivateJPA;
import br.com.pod.privatejpa.persistencia.Cliente;
import br.com.pod.privatejpa.persistencia.JPAUtil;
import br.com.pod.privatejpa.persistencia.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;

import java.util.List;

public class RelatorioClienteServico {

    public List<Cliente> listarPorNomeOuPlaca(String pesquisa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String queryStr = "SELECT c FROM Cliente c "
                    + "JOIN FETCH c.veiculo v "
                    + "WHERE (:pesquisa IS NULL OR c.nome LIKE :pesquisa OR v.placa LIKE :pesquisa)";

            TypedQuery<Cliente> query = em.createQuery(queryStr, Cliente.class);
            query.setParameter("pesquisa", pesquisa.isEmpty() ? null : "%" + pesquisa + "%");

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> listarPorMesDeSaida(int mes) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String queryStr = "SELECT DISTINCT c FROM Cliente c "
                    + "LEFT JOIN FETCH c.veiculo v "
                    + "JOIN FETCH c.pedidos p "
                    + "WHERE EXTRACT(MONTH FROM p.dataSaida) = :mes";

            TypedQuery<Cliente> query = em.createQuery(queryStr, Cliente.class);
            query.setParameter("mes", mes);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
 // Criado apenas para teste 
public List<PrivateJPA.ClienteDTO> gerarRelatorioClientes(List<Pedido> pedidos) {
    List<PrivateJPA.ClienteDTO> relatorio = new ArrayList<>();

    if (pedidos == null || pedidos.isEmpty()) {
        return relatorio;
    }

    // Agrupa pedidos por cliente
    Cliente cliente = pedidos.get(0).getCliente();
    List<PrivateJPA.PedidoDTO> pedidoDTOs = new ArrayList<>();
    double total = 0.0;

    for (Pedido p : pedidos) {
        pedidoDTOs.add(new PrivateJPA.PedidoDTO(p.getId(), p.getDataSaida(), p.getValorTotal()));
        total += p.getValorTotal();
    }

    PrivateJPA.ClienteDTO dto = new PrivateJPA.ClienteDTO(
            cliente.getNome(),
            cliente.getVeiculo().getPlaca(),
            total,
            pedidoDTOs
    );
    relatorio.add(dto);

    return relatorio;
}
}
