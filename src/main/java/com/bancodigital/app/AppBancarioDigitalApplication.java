package com.bancodigital.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplica\u00e7\u00e3o App Banc\u00e1rio Digital.
 *
 * <p>Exp\u00f5e a API REST para contas, transfer\u00eancias e extrato.
 */
@SpringBootApplication
public class AppBancarioDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppBancarioDigitalApplication.class, args);
    }
}
