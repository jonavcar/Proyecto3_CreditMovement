/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.creditmovements.aplication.impl;

import com.banck.creditmovements.aplication.DebitAccountOperations;
import com.banck.creditmovements.DTO.AnyDto;
import com.banck.creditmovements.DTO.DebitAccount;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

/**
 *
 * @author jonavcar
 */
@Service
@RequiredArgsConstructor
public class DebitAccountOperationsImpl implements DebitAccountOperations {

    @Override
    public Mono<AnyDto> debitCardPayment(String debitCard, double amount) {

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .doOnConnected(connection
                        -> connection.addHandlerLast(new ReadTimeoutHandler(3))
                        .addHandlerLast(new WriteTimeoutHandler(3)));

        WebClient webClient = WebClient.builder()
                .baseUrl("http://mov-account.southcentralus.azurecontainer.io:8083")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                //.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))) // timeout
                .build();
        DebitAccount account = new DebitAccount();
        account.setAmount(amount);
        account.setDebitCard(debitCard);
        return webClient.post()
                .uri("/mov-account/debit-card-payment")
                .bodyValue(account)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    return response.createException();
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    return response.createException();
                })
                .bodyToMono(AnyDto.class).flatMap(o -> {
            return Mono.just(o);
        });
    }

}
