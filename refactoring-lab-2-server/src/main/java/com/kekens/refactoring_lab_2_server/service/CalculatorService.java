package com.kekens.refactoring_lab_2_server.service;

import com.kekens.refactoring_lab_2_server.exceptions.IncorrectDataException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public double getEvaluationStep(final List<Double> listStep, final int step)
            throws IncorrectDataException
    {
        try {
            return listStep.get(step - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IncorrectDataException(e.getMessage());
        }
    }

    public double addNumbers(double arg1, double arg2) {
        return arg1 + arg2;
    }

    public double subNumbers(double arg1, double arg2) {
        return arg1 - arg2;
    }

    public double mulNumbers(double arg1, double arg2) {
        return arg1 * arg2;
    }

    public double divNumbers(double arg1, double arg2) throws IncorrectDataException {
        if (arg2 == 0) {
            throw new IncorrectDataException("Cannot be divided by zero");
        } else {
            return arg1 / arg2;
        }
    }

    public String getHelpMessage() {
        return  "Usage:\n" +
                "when a first symbol on line is '>' – enter operand (number)\n" +
                "when a first symbol on line is '@' – enter operation\n" +
                "operation is one of '+', '-', '/', '*' or\n" +
                "'#' followed with number of evaluation step\n" +
                "'q' to exit";
    }

}
