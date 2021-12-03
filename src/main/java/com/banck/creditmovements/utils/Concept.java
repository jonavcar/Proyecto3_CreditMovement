/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.creditmovements.utils;

/**
 *
 * @author jonavcar
 */
public enum Concept {
    //PAGO-CUOTA
    FEE_PAYMENT("PAGO-CUOTA") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    //PLAZO-CUOTA
    DEADLINE_FEE("PLAZO-CUOTA") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    };

    public final String value;

    public boolean equals(String customerType) {
        return value.equals(customerType);
    }

    private Concept(String value) {
        this.value = value;
    }
}
