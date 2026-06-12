package com.example.CalculatorFacade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Random;
import java.util.Date;
import jakarta.persistence.Column;

@Entity
class ComputationRecord {
    ComputationRecord(String expression, String result, Date time, String identifier) {
        this.expression = expression;
        this.result = result;
        this.time = time;
        this.identifier = identifier;
        this.id = new Random().nextLong(1000000);
    }

    ComputationRecord() {}

    @Id
    Long id;
    Date time;
    String expression, identifier;
    @Column(columnDefinition = "TEXT")
    String result;
}
