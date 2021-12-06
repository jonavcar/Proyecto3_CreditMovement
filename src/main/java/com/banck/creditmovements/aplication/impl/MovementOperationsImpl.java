package com.banck.creditmovements.aplication.impl;

import com.banck.creditmovements.domain.Movement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.concurrent.atomic.AtomicReference;
import com.banck.creditmovements.aplication.MovementOperations;
import com.banck.creditmovements.aplication.model.MovementRepository;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jonavcar
 */
@Service
@RequiredArgsConstructor
public class MovementOperationsImpl implements MovementOperations {

    private final MovementRepository movementRepository;

    @Override
    public Flux<Movement> list() {
        return movementRepository.list();
    }

    @Override
    public Mono<Movement> get(String movement) {
        return movementRepository.get(movement);
    }

    @Override
    public Mono<Movement> create(Movement movement) {
        return movementRepository.create(movement);
    }

    @Override
    public Mono<Movement> update(String movement, Movement c) {
        return movementRepository.update(movement, c);
    }

    @Override
    public void delete(String movement) {
        movementRepository.delete(movement);
    }

    @Override
    public Flux<Movement> listBySchedule(String customer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Flux<Movement> listLast10CardMovements() {
        return movementRepository.list();
    }

}
