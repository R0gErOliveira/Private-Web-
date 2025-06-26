/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestaPrivate;

import br.com.pod.privatejpa.persistencia.Pedido;
import br.com.pod.privatejpa.persistencia.PedidoRepositorio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ROGER
 */
// Classe fake esta estendendo a classe real pedidorepoditorio apenas para realizar os testes
public class PedidoRepositorioFake extends PedidoRepositorio {

    // usando um mapa para simular o banco em memória no pc 
    private final Map<Integer, Pedido> bancoDeDadosFake = new HashMap<>();
    private int proximoId = 1;

    public PedidoRepositorioFake() {
        super(null);
    }

    //Sobrescrevendo os métodos públicos do repositorio real para interagir com o mapa e não com o banco 
    @Override
    public void salvar(Pedido pedido) {
        if (pedido.getId() == 0) {
            pedido.setId(proximoId++);
        }
        bancoDeDadosFake.put(pedido.getId(), pedido);
        System.out.println("TESTE REALIZADO: Pedido " + pedido.getId() + " salvo no mapa.");
    }

    @Override
    public Pedido buscarPorId(int id) {
        System.out.println("TESTE REALIZADO: Buscando pedido " + id + " no mapa.");
        return bancoDeDadosFake.get(id);
    }

    @Override
    public void excluirPorId(int id) {
        bancoDeDadosFake.remove(id);
        System.out.println("TESTE REALIZADO: Pedido " + id + " removido do mapa.");
    }

    @Override
    public List<Pedido> listarPorPesquisa(String pesquisa) {
        System.out.println("TESTE REALIZADO: Listando todos os pedidos do mapa.");
        return new ArrayList<>(bancoDeDadosFake.values());
    }

    @Override
    public boolean pedidoFinalizadoPorPlaca(String placa) {
        if ("ABC-1234".equals(placa)) {
            return true; // Simula que encontrou um pedido finalizado para esta placa
        }
        return false;
    }

    @Override
    public Pedido buscarPorClienteId(Long clienteId) {
        System.out.println("TESTE REALIZADO: Buscando pedido pelo cliente ID " + clienteId);
        for (Pedido p : bancoDeDadosFake.values()) {
            if (p.getCliente() != null && p.getCliente().getId() == clienteId) {
                return p;
            }
        }
        return null;
    }

}
