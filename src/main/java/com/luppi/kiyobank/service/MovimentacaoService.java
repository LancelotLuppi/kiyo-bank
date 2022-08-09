package com.luppi.kiyobank.service;

import com.luppi.kiyobank.entity.Conta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private Conta conta = new Conta();
    private final ProdutorService produtorService;

    @Value("${kafka.topic-saque}")
    private String saqueTopic;
    @Value("${kafka.topic-deposito}")
    private String depositoTopic;
    @Value("${kafka.topic-transferencia}")
    private String transferenciaTopic;
    @Value("${kafka.topic-pagamento}")
    private String pagamentoTopic;
    @Value("${kafka.topic-status-pagamento}")
    private String statusPagamentoTopic;

    public void sacar(Integer id, BigDecimal valor){
        List<Conta> contas = conta.getContas();

        Conta contaRecuperada = contas.get(id);

        if(contaRecuperada.getSaldo().compareTo(valor) >= 0) {
            contaRecuperada.setSaldo(contaRecuperada.getSaldo().subtract(valor));
            String mensagem = "SAQUE REALIZADO COM SUCESSO, conta -> " + contaRecuperada.getNumero() + " | " +
                    "titular -> " + contaRecuperada.getTitular() + " | valor -> " + valor + " R$";

            produtorService.enviarMensagem(mensagem, saqueTopic);
        }
    }

    public void depositar(Integer id, BigDecimal valor){
        List<Conta> contas = conta.getContas();

        Conta contaRecuperada = contas.get(id);

        if(valor.compareTo(BigDecimal.ZERO) > 0) {
            contaRecuperada.setSaldo(contaRecuperada.getSaldo().add(valor));
            String mensagem = "DEPOSITO REALIZADO COM SUCESSO, conta -> " + contaRecuperada.getNumero() + " | " +
                    "titular -> " + contaRecuperada.getTitular() + " | valor -> " + valor + " R$";

            produtorService.enviarMensagem(mensagem, saqueTopic);
        }
    }

    public void transferir(Integer idPartida, Integer idDestino, BigDecimal valor) {
        List<Conta> contas = conta.getContas();
        Conta contaPartida = contas.get(idPartida);
        Conta contaDestino = contas.get(idDestino);

        if(contaPartida.getSaldo().compareTo(valor) >= 0 && valor.compareTo(BigDecimal.ZERO) > 0) {
            contaPartida.setSaldo(contaPartida.getSaldo().subtract(valor));
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

            String mensagem = "TRANSFERENCIA REALIZADA COM SUCESSO, de -> " + contaPartida.getTitular() +
                    " para " + contaDestino.getTitular() + ", no valor de " + valor + " R$";
            produtorService.enviarMensagem(mensagem, transferenciaTopic);
        }
    }

    public String gerarPagamento(Integer idConta, BigDecimal valor) {
        String mensagem = "Pagamento gerado com sucesso: @" + idConta + "@" + valor;
        produtorService.enviarMensagem(mensagem, pagamentoTopic);
        return "Pagamento gerado com sucesso";
    }

    @KafkaListener(
            topics = "${kafka.topic-pagamento}",
            groupId = "group1",
            containerFactory = "listenerContainerFactory",
            clientIdPrefix = "topicoPagamento"
    )
    public boolean verificarDadosPagamento(@Payload String mensagem,
                                           @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                           @Header(KafkaHeaders.OFFSET) Long offset) {

        if(mensagem != null) {
            String[] array;
            array = mensagem.split("@");
            int idCliente = Integer.parseInt(array[1]);
            int valorPagamento = Integer.parseInt(array[2]);
            BigDecimal valor = new BigDecimal(valorPagamento);

            List<Conta> contasList = conta.getContas();
            Conta contaRecuperada = contasList.get(idCliente);

            if(contaRecuperada.getSaldo().compareTo(valor) >= 0 && valor.compareTo(BigDecimal.ZERO) >= 0) {
                String statusMessage = "PAGAMENTO APROVADO";
                produtorService.enviarMensagem(statusMessage, statusPagamentoTopic);
                return true;
            }
            String statusMessage = "PAGAMENTO CANCELADO";
            produtorService.enviarMensagem(statusMessage, statusPagamentoTopic);
            return false;
        }
        return false;
    }
}
