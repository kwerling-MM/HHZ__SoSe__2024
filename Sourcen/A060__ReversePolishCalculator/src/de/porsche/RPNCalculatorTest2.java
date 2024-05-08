package de.porsche;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RPNCalculatorTest2 {

    RPNCalculator rpnC = null;

    @BeforeEach
    void setUp() {
        rpnC = new RPNCalculator();
    }

    @AfterEach
    void tearDown() {
        rpnC = null;
    }

    @Test
    void processCommandLineParams() {
    }

    @Test
    void doCalculation() {
    }

    @Test
    void isOperand() {
        boolean exp = false;

        assertEquals(exp, rpnC.isOperand("/"));
        assertFalse(rpnC.isOperand("/"));
    }

    @Test
    void performOperation() {
    }
}