package com.example.CalculatorFacade;

import org.springframework.data.jpa.repository.JpaRepository;

interface Data extends JpaRepository<ComputationRecord, Long> {}