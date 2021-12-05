package com.banck.creditmovements.aplication;

import com.banck.creditmovements.domain.AnyDto;
import reactor.core.publisher.Mono;

/**
 *
 * @author jonavcar
 */
public interface DebitAccountOperations {

    public Mono<AnyDto> debitCardPayment(String debitCard, double amount);

}
