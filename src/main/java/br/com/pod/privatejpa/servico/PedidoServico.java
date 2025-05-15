package br.com.pod.privatejpa.servico;

import br.com.pod.privatejpa.persistencia.Pedido;
import br.com.pod.privatejpa.persistencia.PedidoRepositorio;

import java.util.List;

public class PedidoServico {

    private final PedidoRepositorio pedidoRepositorio;

    public PedidoServico(PedidoRepositorio pedidoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
    }

    public void registrarPedido(Pedido pedido) {
        pedidoRepositorio.salvar(pedido);
    }

    public Pedido buscarPedido(int id) {
        return pedidoRepositorio.buscarPorId(id);
    }

    public void excluirPedido(int id) {
        pedidoRepositorio.excluirPorId(id);
    }

    public boolean isPedidoFinalizado(String placa) {
        return pedidoRepositorio.pedidoFinalizadoPorPlaca(placa);
    }

    public List<Pedido> pesquisarPedidos(String pesquisa) {
        return pedidoRepositorio.listarPorPesquisa(pesquisa);
    }

}
