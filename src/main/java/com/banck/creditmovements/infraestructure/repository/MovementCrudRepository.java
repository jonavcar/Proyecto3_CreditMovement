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
        m.setSchedule(md.getSchedule());
        m.setProduct(md.getProduct());
        m.setModality(md.getModality());
        m.setConcept(md.getConcept());
        m.setCustomer(md.getCustomer());
        m.setObservations(md.getObservations());
        m.setAmount(md.getAmount());
        m.setDate(md.getDate());
        m.setTime(md.getTime());
        m.setStatus(md.getStatus());
        return m;
    }

    public MovementDao MovementToMovementDao(Movement m) {
        MovementDao md = new MovementDao();
        md.setMovement(m.getMovement());
        md.setMovementType(m.getMovementType());
        md.setSchedule(m.getSchedule());
        md.setProduct(m.getProduct());
        md.setModality(m.getModality());
        md.setConcept(m.getConcept());
        md.setCustomer(m.getCustomer());
        md.setObservations(m.getObservations());
        md.setAmount(m.getAmount());
        md.setDate(m.getDate());
        md.setTime(m.getTime());
        md.setStatus(m.getStatus());
        return md;
    }

    @Override
    public Flux<Movement> listBySchedule(String customer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 

}
