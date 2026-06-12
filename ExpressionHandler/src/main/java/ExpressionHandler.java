package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

abstract public class ExpressionHandler {
    public static ExpressionHandler[] instantiated_handlers = {null, null, null, null};
    public static final String address = "/assumeResponsibility/{expression}";
    public RestTemplate client = new RestTemplate();

    public ExpressionHandler() {}

    public char[] criteria;
    public String address_of_next_handler;

    public Boolean considerDelegation(String expression) {
        for (int x = 0; x < expression.length(); x++)
            for (int y = 0; y < criteria.length; y++)
                if (expression.charAt(x) == criteria[y])
                    return true;
        return false;
    }

    public abstract ResponseEntity<String> completeTask (String expression) throws Exception;

    public ResponseEntity<?> delegateTask (String expression) throws Exception {
        return ResponseEntity.ok(client.getForObject(address_of_next_handler + "{expression}", String.class, expression));
    }

    public ResponseEntity<?> processTask (String expression) throws Exception {
        expression = expression.replace('d', '/').replace('o', '(').replace('c', ')');
        if (!considerDelegation(expression))
            return completeTask(expression);
        else
            return delegateTask(expression.replace('/', 'd').replace('(','o').replace(')','c'));
    }

    public String toString(String[] input) {
        String inputted_expression = "";

        for (int x = 0; x < input.length; x++)
            inputted_expression += input[x];

        String outputted_expression = "";

        for (int x = 0; x < inputted_expression.length(); x++) {
            if (inputted_expression.charAt(x) == '(') {
                if (x == 0) 
                    outputted_expression += inputted_expression.charAt(x);
                else if (inputted_expression.charAt(x-1) == '+' || inputted_expression.charAt(x-1) == '-' || 
                         inputted_expression.charAt(x-1) == 'x' || inputted_expression.charAt(x-1) == '/' || 
                         inputted_expression.charAt(x-1) == '(') 
                    outputted_expression += inputted_expression.charAt(x);
                else if (inputted_expression.charAt(x-1) == ')') 
                    outputted_expression += inputted_expression.charAt(x);
                else 
                    outputted_expression += String.valueOf('x') + String.valueOf(inputted_expression.charAt(x));
            } else if (inputted_expression.charAt(x) == ')') {
                if (inputted_expression.length() - x == 1) 
                    outputted_expression += inputted_expression.charAt(x);
                else if (inputted_expression.charAt(x+1) == '+' || inputted_expression.charAt(x+1) == '-' || inputted_expression.charAt(x+1) == 'x' || 
                         inputted_expression.charAt(x+1) == '/' || inputted_expression.charAt(x+1) == ')') 
                    outputted_expression += inputted_expression.charAt(x);
                else 
                    outputted_expression += String.valueOf(inputted_expression.charAt(x)) + String.valueOf('x');
            } else
                outputted_expression += inputted_expression.charAt(x);
        }

        return outputted_expression;
    }

    public String[] toArray(String input) {
        int size = 0;

        for (int x = 0; x < input.length(); x++)
            if (input.charAt(x) == '+' || input.charAt(x) == '-' || input.charAt(x) == 'x' || input.charAt(x) == '/')
                if (x != 0)
                    if (input.charAt(x-1) != '+' && input.charAt(x-1) != '-' && input.charAt(x-1) != 'x' && input.charAt(x-1) != '/')
                        size++;

        String[] elements = new String[(size * 2) + 1];
        int increment = 0;

        for (int x = 0; x < elements.length; x++) 
            elements[x] = "";

        for (int x = 0; x < input.length(); x++) {
            if (input.charAt(x) == '+' || input.charAt(x) == '-' || input.charAt(x) == 'x' || input.charAt(x) == '/') {
                if (x == 0)
                    elements[increment] += input.charAt(x);
                else if (input.charAt(x-1) == '+' || input.charAt(x-1) == '-' || input.charAt(x-1) == 'x' || input.charAt(x-1) == '/')
                    elements[increment] += input.charAt(x);
                else {
                    elements[++increment] += input.charAt(x);
                    increment++;
                }
            } else 
                elements[increment] += input.charAt(x);
        }

        return elements;
    }
}