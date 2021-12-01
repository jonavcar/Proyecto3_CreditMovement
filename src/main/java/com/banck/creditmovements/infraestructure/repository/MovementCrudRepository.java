package com.banck.creditmovements.infraestructure.repository;

import com.banck.creditmovements.domain.Movement;
import com.banck.creditmovements.infraestructure.model.dao.MovementDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.banck.creditmovements.aplication.model.MovementRepository;

/**
 *
 * @author jonavcar
 */
@Component
public class MovementCrudRepository implements MovementRepository {

    @Autowired
    IMovementCrudRepository movementRepository;

    @Override
    public Mono<Movement> get(String movement) {
        return movementRepository.findById(movement).map(this::MovementDaoToMovement);
    }

    @Override
    public Flux<Movement> list() {
        return movementRepository.findAll().map(this::MovementDaoToMovement);
    }

    @Override
    public Mono<Movement> create(Movement movement) {
        return movementRepository.save(MovementToMovementDao(movement)).map(this::MovementDaoToMovement);
    }

    @Override
    public Mono<Movement> update(String movement, Movement c) {
        c.setMovement(movement);
        return movementRepository.save(MovementToMovementDao(c)).map(this::MovementDaoToMovement);
    }

    @Override
    public void delete(String movement) {
        movementRepository.deleteById(movement).subscribe();
    }

    public Movement MovementDaoToMovement(MovementDao md) {
        Movement m = new Movement();
        m.setMovement(md.getMovement());
        m.setMovementType(md.getMovementType());
        m.setCredit(md.getCredit());
        m.setCreditType(md.getCreditType());
        m.setCustomer(md.getCustomer());
        m.setAmount(md.getAmount());
        m.setConcept(md.getConcept());
        m.setDate(md.getDate());
        m.setTime(md.getTime());
        m.setCorrect(md.isCorrect());
        return m;
    }

    public MovementDao MovementToMovementDao(Movement m) {
        MovementDao md = new MovementDao();
        md.setMovement(m.getMovement());
        md.setMovementType(m.getMovementType());
        md.setCredit(m.getCredit());
        md.setCreditType(m.getCreditType());
        md.setCustomer(m.getCustomer());
        md.setAmount(m.getAmount());
        md.setConcept(m.getConcept());
        md.setDate(m.getDate());
        md.setTime(m.getTime());
        md.setCorrect(m.isCorrect());
        return md;
    }

    @Override
    public Flux<Movement> listByCustomer(String customer) {
        return movementRepository.findAllByCustomer(customer).map(this::MovementDaoToMovement);
    }

    @Override
    public Flux<Movement> listByCustomerAndCredit(String customer, String credit) {
        return movementRepository.findAllByCustomerAndAccount(customer, credit).map(this::MovementDaoToMovement);
    }

    @Override
    public Flux<Movement> listByCustomerAndCreditAndCreditType(String customer, String credit, String creditType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Flux<Movement> listByAccount(String account) {
        return movementRepository.findAllByAccount(account).map(this::MovementDaoToMovement);
    }

}
