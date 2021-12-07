package com.banck.creditmovements.aplication;

import com.banck.creditmovements.domain.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author jonavcar
 */
public interface MovementOperations {

    public Flux<Movement> list();

    public Flux<Movement> listBySchedule(String customer);

    public Flux<Movement> listLast10CardMovements();

    public Flux<Movement> listProductMovementBetweenDatesAndCustomer(String customer, String dateI, String dateF);

    public Mono<Movement> get(String movement);

    public Mono<Movement> create(Movement movement);

    public Mono<Movement> update(String id, Movement movement);

    public void delete(String id);

}
