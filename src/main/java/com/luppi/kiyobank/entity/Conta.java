package com.luppi.kiyobank.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityScan
public class Conta {
    private Integer id;
    private String titular;
    private String numero;
    private BigDecimal saldo;

    private static List<Conta> contas = generateContas();

    public static List<Conta> generateContas() {
        Conta conta1 = new Conta(0, "Gabriel Luppi", "123456", new BigDecimal("1000"));
        Conta conta2 = new Conta(1, "Maicon Gerardi", "654321", new BigDecimal("32000"));
        Conta conta3 = new Conta(2, "Rafael Lazzari", "213465", new BigDecimal("34000"));
        Conta conta4 = new Conta(3, "Eula Lawrance", "563412", new BigDecimal("5209.20"));

        List<Conta> listaContas = new ArrayList<>();
        listaContas.add(conta1);
        listaContas.add(conta2);
        listaContas.add(conta3);
        listaContas.add(conta4);

        return listaContas;
    }

    public List<Conta> getContas() {
        return contas;
    }
}
