/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.creditmovements.DTO;

import com.banck.creditmovements.domain.Movement;
import java.util.List;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author jnacarra
 */
@Data
public class CardMovementDto {

    String card;
    List<Movement> movements;
}
