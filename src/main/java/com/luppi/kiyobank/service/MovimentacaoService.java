package com.luppi.kiyobank.service;

import com.luppi.kiyobank.entity.Conta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void sacar(Integer id, BigDecimal valor){
        List<Conta> contas = conta.getContas();

        Conta contaRecuperada = contas.get(id);

        if(contaRecuperada.getSaldo().compareTo(valor) >= 0) {
            contaRecuperada.setSaldo(contaRecuperada.getSaldo().subtract(valor));
            contas.remove(id);
            contas.add(contaRecuperada);
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
            contas.remove(id);
            contas.add(contaRecuperada);
            String mensagem = "DEPOSITO REALIZADO COM SUCESSO, conta -> " + contaRecuperada.getNumero() + " | " +
                    "titular -> " + contaRecuperada.getTitular() + " | valor -> " + valor + " R$";

            produtorService.enviarMensagem(mensagem, saqueTopic);
        }
    }
}
