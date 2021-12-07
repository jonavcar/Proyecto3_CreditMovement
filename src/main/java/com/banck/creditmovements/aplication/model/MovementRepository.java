package com.banck.creditmovements.aplication.model;

import com.banck.creditmovements.domain.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author jonavcar
 */
public interface MovementRepository {

    public Flux<Movement> list();

    public Flux<Movement> listBySchedule(String customer);
    
    public Flux<Movement> listByCustomer(String customer);

    public Mono<Movement> get(String movement);

    public Mono<Movement> create(Movement movement);

    public Mono<Movement> update(String id, Movement movement);

    public void delete(String id);
}
