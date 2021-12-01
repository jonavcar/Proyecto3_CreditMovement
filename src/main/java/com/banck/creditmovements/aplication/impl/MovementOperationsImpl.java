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
        AtomicReference<Mono<Movement>> atMovement = new AtomicReference<>();
        Flux<Movement> lstMovements = movementRepository.listByAccount(movement.getCredit());

        /*
        if (CreditType.BUSINESS_CREDIT.equals(movement.getCreditType())) {

            lstMovements.filter(act -> act.getMovementType().equals(movement.getMovementType()))
                    .count()
                    .subscribe(count -> {
                        if (count == 0) {
                            atAccount.set(movementRepository.create(movement));
                        } else {
                            atAccount.set(Mono.empty());
                        }
                    });
        } else if (CreditType.PERSONAL_CREDIT.equals(movement.getCreditType())) {

            lstMovements.filter(act -> act.getCreditType().equals(movement.getCreditType()))
                    .count()
                    .subscribe(count -> {
                        if (count == 0) {
                            atAccount.set(movementRepository.create(movement));
                        } else {
                            atAccount.set(Mono.empty());
                        }
                    });
        } else {
            atAccount.set(movementRepository.create(movement));
        }
         */
        atMovement.set(movementRepository.create(movement));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MovementOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return atMovement.get();
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
    public Flux<Movement> listByCustomer(String customer) {
        return movementRepository.listByCustomer(customer);
    }

    @Override
    public Flux<Movement> listByCustomerAndCredit(String customer, String credit) {
        return movementRepository.listByCustomerAndCredit(customer, credit);
    }

    @Override
    public Flux<Movement> listByCustomerAndCreditAndCreditType(String customer, String credit, String creditType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Flux<Movement> listByAccount(String movement) {
        return movementRepository.listByAccount(movement);
    }

}
