/*
 * Comentários do template
 */
package TestaPrivate; // Idealmente, este pacote deveria ser br.com.pod.privatejpa.servico ou similar

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import br.com.pod.privatejpa.persistencia.Cliente;
import br.com.pod.privatejpa.persistencia.Pedido;
import br.com.pod.privatejpa.servico.PedidoServico;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author ROGER
 */
public class TestJUnit {

    private PedidoServico pedidoServico;
    private PedidoRepositorioFake repositorioFalso;

    @BeforeEach
    void setUp() {
        // 1. Cria uma nova instância do nosso falso.
        repositorioFalso = new PedidoRepositorioFake();

        // 2.inicia o repositório falso.
        pedidoServico = new PedidoServico(repositorioFalso);
    }
// teste para testar um novo pedido quando o cliente finaliza a estadia no estacionemtno 

    @Test
    @DisplayName("Deve registrar um novo pedido com sucesso")
    void deveRegistrarUmNovoPedido() {
        Pedido novoPedido = new Pedido();
        novoPedido.setValorTotal(150.0);

        // Chama o método do serviço
        pedidoServico.registrarPedido(novoPedido);
        // confirma que o pedido foi salvo no repositorio falso 
        Pedido pedidoSalvo = repositorioFalso.buscarPorId(1);

        assertNotNull(pedidoSalvo, "O pedido não foi encontrado no repositório fake após salvar.");
        assertEquals(150.0, pedidoSalvo.getValorTotal(), "O valor total do pedido salvo está incorreto.");
    }
// teste para buscar um pedido específico, será usado nos relatório futuramente 

    @Test
    @DisplayName("Deve buscar um pedido existente pelo seu ID")
    void deveBuscarPedidoPorId() {

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(42); //informa ID
        repositorioFalso.salvar(pedidoExistente);

        // tenta buscar o pedido 
        Pedido pedidoEncontrado = pedidoServico.buscarPedido(42);

        // garante q o pedido encontrado não é nulo 
        assertNotNull(pedidoEncontrado);
        assertEquals(42, pedidoEncontrado.getId());
    }

    // teste ao buscar pedido q não existe 
    @Test
    @DisplayName("Deve retornar nulo ao buscar um pedido com ID inexistente")
    void deveRetornarNuloParaIdInexistente() {
// informando ID q não existe 
        Pedido pedidoEncontrado = pedidoServico.buscarPedido(999);

        // confirmando q o id não existe o pedido é nulo 
        assertNull(pedidoEncontrado, "Deveria retornar nulo para um ID que não existe.");
    }
    // teste para exclusão de um pedido 

    @Test
    @DisplayName("Deve excluir um pedido existente")
    void deveExcluirPedido() {

        Pedido pedidoParaExcluir = new Pedido();
        repositorioFalso.salvar(pedidoParaExcluir);
        int idDoPedido = pedidoParaExcluir.getId();

        assertNotNull(repositorioFalso.buscarPorId(idDoPedido), "O pedido deveria existir antes da exclusão.");

        pedidoServico.excluirPedido(idDoPedido);

        assertNull(repositorioFalso.buscarPorId(idDoPedido), "O pedido não deveria mais ser encontrado após a exclusão.");
    }
// teste de pesquisa de pedido 

    @Test
    @DisplayName("Deve listar todos os pedidos quando a pesquisa for vazia")
    void deveListarTodosPedidos() {
        // inseridno 2 pedidos ao repositório falso 
        repositorioFalso.salvar(new Pedido());
        repositorioFalso.salvar(new Pedido());

        // simulando a pesquisa com o usuário clicando somnte em buscar sem preencher nenhum cmapo 
        List<Pedido> listaDePedidos = pedidoServico.pesquisarPedidos("");

        //garante q a lista não é nula e retorna os 2 pedidos cadastrados 
        assertNotNull(listaDePedidos);
        assertEquals(2, listaDePedidos.size(), "A lista deveria conter 2 pedidos.");
    }
// teste para placa encontrada e não encontrada 

    @Test
    @DisplayName("Deve identificar corretamente um pedido finalizado pela placa")
    void deveIdentificarPedidoFinalizado() {
        // definindo um pedido finalizado 
        String placaFinalizada = "ABC-1234";
        // definindo um pedido não finalizado 
        String placaNaoFinalizada = "XYZ-9876";

        // verifica se retorna verdadeiro a pesquisa é em uma placa existente 
        assertTrue(pedidoServico.isPedidoFinalizado(placaFinalizada), "Deveria retornar true para a placa finalizada.");
        //verifica se retorna falso, a placa não existe 
        assertFalse(pedidoServico.isPedidoFinalizado(placaNaoFinalizada), "Deveria retornar false para a placa não finalizada.");
    }
// teste para pedido 

    @Test
    @DisplayName("Deve calcular corretamente o valor do pedido com base nos dias estacionados")
    void deveCalcularValorDoPedidoCorretamente() {
        //difinindo cliente, dias e valor da diaria 
        Cliente cliente = new Cliente();
        cliente.setDataEntrada(LocalDate.of(2023, 10, 20)); // Entrou dia 20
        LocalDate dataSaida = LocalDate.of(2023, 10, 24);   // Saiu dia 24
        double valorDiaria = 50.0;

        // 5 diarias, valo total a pagar 
        double valorEsperado = 250.0;

        // 1. Calcula o número de dias entre a data de entrada e a de saída.
        //   usando o  ChronoUnit.DAYS.between é exclusivo, então adicionamos 1 para incluir o dia de saída.
        long numeroDeDias = ChronoUnit.DAYS.between(cliente.getDataEntrada(), dataSaida) + 1;

        // 2. Calcula o valor total.
        double valorCalculado = numeroDeDias * valorDiaria;
        // ====================================================================

        // verifica se o valor calculado esta correto 
        assertEquals(valorEsperado, valorCalculado, "O cálculo do valor final do estacionamento está incorreto.");
    }
}
