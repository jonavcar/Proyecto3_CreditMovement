package com.banck.creditmovements.domain;

import lombok.Data;

/**
 *
 * @author jonavcar
 */
@Data
public class Movement {

    public String movement;
    public String movementType;
    public String credit;
    public String creditType;
    public String customer;
    public int amount;
    public String concept;
    public String date;
    public String time;
    public boolean correct;
}
