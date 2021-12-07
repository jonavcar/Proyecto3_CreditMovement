package com.banck.creditmovements.infraestructure.rest;

import com.banck.creditmovements.aplication.DebitAccountOperations;
import com.banck.creditmovements.DTO.CardMovementDto;
import com.banck.creditmovements.DTO.DateIDateF;
import com.banck.creditmovements.domain.Movement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.Collectors;

import com.banck.creditmovements.aplication.MovementOperations;
import com.banck.creditmovements.DTO.ProductMovementDto;
import com.banck.creditmovements.utils.Concept;
import com.banck.creditmovements.utils.DateValidator;
import com.banck.creditmovements.utils.DateValidatorUsingLocalDate;
import com.banck.creditmovements.utils.Modality;
import com.banck.creditmovements.utils.MovementType;
import java.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jonavcar
 */
@RestController
@RequestMapping("/mov-credit")
@RequiredArgsConstructor
public class MovementController {

    Logger logger = LoggerFactory.getLogger(MovementController.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("America/Bogota"));
    private final MovementOperations operations;
    private final DebitAccountOperations accountOperations;

    @GetMapping
    public Flux<Movement> listAll() {
        return operations.list();
    }

    @GetMapping("/{id}")
    public Mono<Movement> get(@PathVariable("id") String id) {
        return operations.get(id);
    }

    @GetMapping("/customer/{id}/list")
    public Flux<Movement> listByCustomer(@PathVariable("id") String id) {
        return operations.listBySchedule(id);
    }

    @GetMapping("/account/{id}/list")
    public Flux<Movement> listByAccount(@PathVariable("id") String id) {
        return operations.listBySchedule(id);
    }

    @GetMapping("/customer-credit/{customer}/{credit}/list")
    public Flux<Movement> listByCustomerAndCredit(@PathVariable("customer") String customer,
            @PathVariable("credit") String credit) {
        return operations.listBySchedule(customer);
    }

    @GetMapping("/card/last-10-movements")
    public Flux<CardMovementDto> reportLast10CardMovements() {
        return operations.listLast10CardMovements()
                .filter(fm -> Optional.ofNullable(fm.getProduct()).isPresent()
                && Optional.ofNullable(fm.getModality()).isPresent())
                .filter(f2m -> f2m.getModality().equals(Modality.CREDIT_CARD.value)
                || f2m.getModality().equals(Modality.DEBIT_CARD.value))
                .groupBy(gb -> gb.getProduct())
                .flatMap(gm -> {
                    return gm.takeLast(10).collectList().map(lm -> {
                        CardMovementDto cm = new CardMovementDto();
                        cm.setCard(gm.key());
                        cm.setMovements(lm);
                        return cm;
                    });
                });
    }

    @PostMapping("/product/movement/{customer}/list")
    public Flux<ProductMovementDto> ProductMovementByCustomerAndDate(@PathVariable("customer") String customer, @RequestBody DateIDateF didf) {

        DateValidator validator = new DateValidatorUsingLocalDate(formatDate);

        if (!validator.isValid(didf.getDateI())) {
            Throwable t = new Throwable();
            return Flux.error(t, true);
        }

        if (!validator.isValid(didf.getDateF())) {
            Throwable t = new Throwable();
            return Flux.error(t, false);
        }

        return operations.listProductMovementBetweenDatesAndCustomer(customer, didf.getDateI(), didf.getDateF())
                .filter(fm -> Optional.ofNullable(fm.getProduct()).isPresent()
                && Optional.ofNullable(fm.getDate()).isPresent()
                && !Optional.ofNullable(fm.getDate()).isEmpty())
                .filter(fm -> isDateRange(didf.getDateI(), didf.getDateF(), fm.getDate()))
                .groupBy(gb -> gb.getProduct())
                .flatMap(gm -> {
                    return gm.collectList().map(lm -> {
                        ProductMovementDto cm = new ProductMovementDto();
                        cm.setProduct(gm.key());
                        cm.setMovements(lm);
                        return cm;
                    });
                });
    }

    @PostMapping
    public Mono<ResponseEntity> create(@RequestBody Movement c) {
        c.setMovement("MV-" + getRandomNumberString());
        c.setDate(dateTime.format(formatDate));
        c.setTime(dateTime.format(formatTime));
        return Mono.just(c).flatMap(schedule -> {

            String msgTipoMovimiento = "Credito Personal = { \"productType\": \"CP\" }\n"
                    + "Credito Empresarial = { \"productType\": \"CE\" }\n"
                    + "Targeta Debito = { \"productType\": \"TD\" }\n"
                    + "Targeta Credito = { \"productType\": \"TC\" }";

            String msgModalidad = "Ventanilla = { \"modality\": \"VT\" }\n"
                    + "Targeta Debito = { \"modality\": \"TD\" }\n"
                    + "Targeta Credito = { \"modality\": \"TC\" }\n"
                    + "Cajero Automatico = { \"modality\": \"CA\" }";

            String msgConcepto = "Pago Cuota = { \"concept\": \"PAGO-CUOTA\" }";

            if (Optional.ofNullable(schedule.getMovementType()).isEmpty()) {
                return Mono.just(
                        new ResponseEntity("Debe ingresar Tipo Movimiento, Ejemplo { \"movementType\": \"ABONO\" }",
                                HttpStatus.BAD_REQUEST));
            }

            if (Optional.ofNullable(schedule.getModality()).isEmpty()) {
                return Mono.just(new ResponseEntity("Debe ingresar la Modalidad, Ejemplo { \"modality\": \"TA\" }",
                        HttpStatus.BAD_REQUEST));
            }

            if (Optional.ofNullable(schedule.getConcept()).isEmpty()) {
                return Mono.just(new ResponseEntity(
                        "Debe ingresar la Concepto, Ejemplo { \"concept\": \"PAGO-CUOTA\" }", HttpStatus.BAD_REQUEST));
            }

            boolean isMovementType = false;
            for (MovementType tc : MovementType.values()) {
                if (schedule.getMovementType().toUpperCase().equals(tc.value)) {
                    isMovementType = true;
                }
            }

            boolean isModalidadType = false;
            for (Modality tc : Modality.values()) {
                if (schedule.getModality().toUpperCase().equals(tc.value)) {
                    isModalidadType = true;
                }
            }

            boolean isConceptType = false;
            for (Concept tc : Concept.values()) {
                if (schedule.getConcept().equals(tc.value)) {
                    isConceptType = true;
                }
            }

            if (!isMovementType || Optional.ofNullable(schedule.getMovementType()).isEmpty()) {
                return Mono.just(new ResponseEntity(""
                        + "Solo existen estos Codigos de Tipo Movimiento: \n"
                        + msgTipoMovimiento, HttpStatus.BAD_REQUEST));
            }
            if (!isConceptType || Optional.ofNullable(schedule.getConcept()).isEmpty()) {
                return Mono.just(new ResponseEntity(""
                        + "Solo existen estos Codigos de Conceptos: \n"
                        + msgConcepto, HttpStatus.BAD_REQUEST));
            }
            if (!isModalidadType || Optional.ofNullable(schedule.getModality()).isEmpty()) {
                return Mono.just(new ResponseEntity(""
                        + "Solo existen estos Codigos Modalidades: \n"
                        + msgModalidad, HttpStatus.BAD_REQUEST));
            }

            if (Optional.ofNullable(schedule.getAmount()).isEmpty() || schedule.getAmount() <= 0) {
                return Mono.just(new ResponseEntity("Debe ingresar el monto, Ejemplo { \"amount\": 240 }",
                        HttpStatus.BAD_REQUEST));
            }

            if (Concept.FEE_PAYMENT.equals(schedule.getConcept())) {
                if (Optional.ofNullable(schedule.getSchedule()).isEmpty()) {
                    return Mono.just(new ResponseEntity(
                            "Debe ingresar el cronograma a pagar, Ejemplo { \"schedule\": \"TC-00001\" }",
                            HttpStatus.BAD_REQUEST));
                }
            }

            schedule.setMovement(getRandomNumberString());
            schedule.setObservations("Amortizacion de cuota");
            schedule.setStatus("1");

            return operations.create(schedule).flatMap(scheduleRes -> {
                return Mono.just(new ResponseEntity(scheduleRes, HttpStatus.OK));
            });
        });
    }

    @PostMapping("/debit-card-payment")
    public Mono<ResponseEntity> debitCardPayment(@RequestBody Movement movementReq) {
        movementReq.setMovement("MV-" + getRandomNumberString());
        movementReq.setDate(dateTime.format(formatDate));
        movementReq.setTime(dateTime.format(formatTime));

        return Mono.just(movementReq).flatMap(movement -> {

            if (Optional.ofNullable(movement.getProduct()).isEmpty()) {
                return Mono.just(
                        new ResponseEntity("Debe ingresar la targeta de debito, Ejemplo { \"product\": \"TD-00000\" }",
                                HttpStatus.BAD_REQUEST));
            }

            if (Optional.ofNullable(movement.getAmount()).isEmpty() || movement.getAmount() <= 0) {
                return Mono.just(new ResponseEntity("Debe ingresar el monto, Ejemplo { \"amount\": 240 }",
                        HttpStatus.BAD_REQUEST));
            }

            if (Optional.ofNullable(movement.getSchedule()).isEmpty()) {
                return Mono.just(
                        new ResponseEntity("Debe ingresar el cronograma a pagar, Ejemplo { \"schedule\": \"0000-1\" }",
                                HttpStatus.BAD_REQUEST));
            }

            movement.setMovement(getRandomNumberString());
            movement.setObservations("Amortizacion de la cuota " + movementReq.getSchedule() + " con un abono de "
                    + movementReq.getAmount());
            movement.setStatus("1");
            movement.setModality(Modality.DEBIT_CARD.value);
            movement.setMovementType(MovementType.PAYMENT.value);
            movement.setConcept(Concept.FEE_PAYMENT.value);
            movement.setProduct(movementReq.getProduct());
            movement.setCustomer("");
            return accountOperations.debitCardPayment(movement.getProduct(), movement.getAmount()).flatMap(tr -> {

                if (tr.getCode().equals("1")) {
                    return operations.create(movement).flatMap(scheduleRes -> {
                        return Mono.just(new ResponseEntity(scheduleRes, HttpStatus.OK));
                    });
                } else {
                    return Mono.just(new ResponseEntity(tr.getMessage(), HttpStatus.BAD_REQUEST));
                }
            }).onErrorReturn(new ResponseEntity("Ocurrio un error en el servidor de Movimientos en Cuentas",
                    HttpStatus.BAD_REQUEST));
        });
    }

    @PutMapping("/{id}")
    public Mono<Movement> update(@PathVariable("id") String id, @RequestBody Movement movement) {
        return operations.update(id, movement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        operations.delete(id);
    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public boolean isDateRange(String strDateI, String strDateF, String strDateC) {
        LocalDate dateI = LocalDate.parse(strDateI, formatDate);
        LocalDate dateF = LocalDate.parse(strDateF, formatDate);
        LocalDate dateC = LocalDate.parse(strDateC, formatDate);
        return ((dateC.isAfter(dateI) || dateC.isEqual(dateI)) && (dateC.isBefore(dateF) || dateC.isEqual(dateF)));
    }
}
