package com.kekens.refactoring_lab_2_server.controller;

import com.kekens.refactoring_lab_2_server.exceptions.IncorrectDataException;
import com.kekens.refactoring_lab_2_server.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @Autowired
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/help")
    public ResponseEntity<String> getHelpMessage() {
        return ResponseEntity.status(HttpStatus.OK).body(calculatorService.getHelpMessage());
    }

    @GetMapping("/step")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Double> getEvaluationStep(@RequestParam("step") int step, HttpSession httpSession) throws IncorrectDataException {
        List<Double> listStep = (List<Double>) httpSession.getAttribute("listStep");

        if (listStep != null) {
            double num = calculatorService.getEvaluationStep(listStep, step);
            saveDataInSession(httpSession, 0, num);
            return ResponseEntity.status(HttpStatus.OK).body(num);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/add")
    public ResponseEntity<Double> addNumbers(@RequestParam("arg1") @NumberFormat(pattern = "#,####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#,####") double arg2,
                                             HttpSession httpSession) throws IncorrectDataException
    {
        double result = calculatorService.addNumbers(arg1, arg2);
        saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/sub")
    public ResponseEntity<Double> subNumbers(@RequestParam("arg1") @NumberFormat(pattern = "#,####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#,####") double arg2,
                                             HttpSession httpSession) throws IncorrectDataException
    {
        double result = calculatorService.subNumbers(arg1, arg2);
        saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/mul")
    public ResponseEntity<Double> mulNumbers(@RequestParam("arg1") @NumberFormat(pattern = "#,####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#,####") double arg2,
                                             HttpSession httpSession) throws IncorrectDataException
    {
        double result = calculatorService.mulNumbers(arg1, arg2);
        saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/div")
    public ResponseEntity<Double> divNumbers(@RequestParam("arg1") @NumberFormat(pattern = "#,####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#,####") double arg2,
                                             HttpSession httpSession) throws IncorrectDataException
    {
        double result = calculatorService.divNumbers(arg1, arg2);
        saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/q")
    public void clearSession(HttpSession httpSession) {
        httpSession.removeAttribute("listStep");
    }

    @SuppressWarnings("unchecked")
    private void saveDataInSession(HttpSession httpSession, double arg1, double result) {
        List<Double> listStep = (List<Double>) httpSession.getAttribute("listStep");

        if (listStep == null) {
            listStep = new ArrayList<>(Arrays.asList(arg1, result));
        } else {
            listStep.add(result);
        }

        httpSession.setAttribute("listStep", listStep);
    }

    @ExceptionHandler(IncorrectDataException.class)
    public ResponseEntity<String> handleException(IncorrectDataException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
