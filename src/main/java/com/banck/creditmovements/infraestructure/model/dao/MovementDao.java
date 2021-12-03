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
    public String schedule;
    public String product;
    public String modality;
    public String concept;
    public String customer;
    public int amount;
    public String observations;
    public String date;
    public String time;
    public String status;
}
