package com.banck.creditmovements.spring.config;

import com.banck.creditmovements.infraestructure.repository.MovementCrudRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.banck.creditmovements.aplication.model.MovementRepository;

/**
 *
 * @author jonavcar
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public MovementRepository movementRepository() {
        return new MovementCrudRepository();
    }
 
}
