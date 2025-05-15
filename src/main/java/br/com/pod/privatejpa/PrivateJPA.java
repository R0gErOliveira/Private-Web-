package br.com.pod.privatejpa;

import br.com.pod.privatejpa.persistencia.Cliente;
import br.com.pod.privatejpa.persistencia.Pedido;
import br.com.pod.privatejpa.persistencia.Veiculo;
import br.com.pod.privatejpa.servico.RelatorioClienteServico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrivateJPA {

    //Main aplicado teste de funcionamento 
    public static void main(String[] args) {

        // Criando veículos fictícios
        Veiculo veiculo1 = new Veiculo();
        veiculo1.setPlaca("ABC1234");
        veiculo1.setModelo("Gol");
        veiculo1.setCor("Branco");
        veiculo1.setAnofab(2020);

        // Criando clientes fictícios
        Cliente cliente1 = new Cliente();
        cliente1.setNome("Roger Oliveira");
        cliente1.setVeiculo(veiculo1);

        // Associando cliente ao veículo
        veiculo1.setCliente(cliente1);

        // Criando pedidos fictícios
        Pedido pedido1 = new Pedido();
        pedido1.setId(1);
        pedido1.setCliente(cliente1);
        pedido1.setDataSaida(LocalDate.of(2024, 12, 1));
        pedido1.setValorTotal(100.0);

        Pedido pedido2 = new Pedido();
        pedido2.setId(2);
        pedido2.setCliente(cliente1);
        pedido2.setDataSaida(LocalDate.of(2024, 12, 10));
        pedido2.setValorTotal(150.0);

        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido1);
        pedidos.add(pedido2);

        // Usando o serviço para gerar o relatório
        RelatorioClienteServico servico = new RelatorioClienteServico();
        List<ClienteDTO> relatorio = servico.gerarRelatorioClientes(pedidos);

        // Impressão do relatório simulado
        System.out.println("=== RELATÓRIO DE CLIENTES ===");
        for (ClienteDTO cliente : relatorio) {
            System.out.println("Cliente: " + cliente.getNomeCliente());
            System.out.println("Placa: " + cliente.getPlacaVeiculo());
            System.out.println("Total Gasto: R$ " + cliente.getTotalGasto());

            for (PedidoDTO p : cliente.getPedidos()) {
                System.out.println(" - Pedido ID: " + p.getId());
                System.out.println("   Data: " + p.getDataSaida());
                System.out.println("   Valor: R$ " + p.getValorTotal());
            }

            System.out.println();
        }

        double totalGastoSistema = relatorio.stream()
                .mapToDouble(ClienteDTO::getTotalGasto)
                .sum();
        System.out.println("TOTAL GERAL FATURADO: R$ " + totalGastoSistema);
    }

    // DTOs fictícios para o teste funcionar sem acessar banco
    public static class ClienteDTO {

        private String nomeCliente;
        private String placaVeiculo;
        private double totalGasto;
        private List<PedidoDTO> pedidos;

        public ClienteDTO(String nomeCliente, String placaVeiculo, double totalGasto, List<PedidoDTO> pedidos) {
            this.nomeCliente = nomeCliente;
            this.placaVeiculo = placaVeiculo;
            this.totalGasto = totalGasto;
            this.pedidos = pedidos;
        }

        public String getNomeCliente() {
            return nomeCliente;
        }

        public String getPlacaVeiculo() {
            return placaVeiculo;
        }

        public double getTotalGasto() {
            return totalGasto;
        }

        public List<PedidoDTO> getPedidos() {
            return pedidos;
        }
    }

    public static class PedidoDTO {

        private int id;
        private LocalDate dataSaida;
        private double valorTotal;

        public PedidoDTO(int id, LocalDate dataSaida, double valorTotal) {
            this.id = id;
            this.dataSaida = dataSaida;
            this.valorTotal = valorTotal;
        }

        public int getId() {
            return id;
        }

        public LocalDate getDataSaida() {
            return dataSaida;
        }

        public double getValorTotal() {
            return valorTotal;
        }
    }

}
