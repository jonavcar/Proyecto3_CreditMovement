/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.creditmovements.DTO;

import lombok.Data;

/**
 *
 * @author jnacarra
 */
@Data
public class DebitAccount {

    String status;
    String debitCard;
    double amount;
    String message;
}
