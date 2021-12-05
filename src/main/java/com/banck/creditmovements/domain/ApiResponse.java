/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.creditmovements.domain;

import lombok.Data;
import reactor.core.publisher.Mono;

/**
 *
 * @author jnacarra
 */
@Data
public class ApiResponse {

    String status;
    String code;
    String message;
    Mono<?> mono;
}
