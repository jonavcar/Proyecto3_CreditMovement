package com.banck.creditmovements.infraestructure.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author jonavcar
 */
@Data
@Document("movement")
public class MovementDao {

    @Id
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
