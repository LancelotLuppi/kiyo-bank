package com.luppi.kiyobank.controller;

import com.luppi.kiyobank.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/movimentacao")
@RequiredArgsConstructor
public class MovimentacaoController {
    private final MovimentacaoService movimentacaoService;

    @PostMapping("/saque")
    public void sacar(Integer id, BigDecimal valor){
        movimentacaoService.sacar(id, valor);
    }

    @PostMapping("/deposito")
    public void depositar(Integer id, BigDecimal valor){
        movimentacaoService.depositar(id, valor);
    }

    @PostMapping("/transferencia")
    public void transferir(Integer idPartida, Integer idDestino, BigDecimal valor) {
        movimentacaoService.transferir(idPartida, idDestino, valor);
    }

    @PostMapping("/pagamento")
    public void gerarPagamento(Integer idConta, BigDecimal valor) {
        movimentacaoService.gerarPagamento(idConta, valor);
    }
}
