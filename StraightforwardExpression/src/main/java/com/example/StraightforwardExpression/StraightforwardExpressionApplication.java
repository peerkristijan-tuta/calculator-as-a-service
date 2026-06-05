package com.example.StraightforwardExpression;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.example.ExpressionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class StraightforwardExpressionApplication extends ExpressionHandler {

	public ResponseEntity<?> processTask (String expression) throws Exception {
		try {
			return super.processTask(expression);
		} catch (Exception exc) {
			return ResponseEntity.ok(StraightforwardExpressionApplication.this.client.getForObject("http://localhost:8105/assumeResponsibility/" + "{expression}", String.class, 
				   expression));
		}
    }

	StraightforwardExpressionApplication() {
		criteria = new char[]{'(', ')'};
		address_of_next_handler = "http://localhost:8103/assumeResponsibility/";
	}

	public String reformatBrackets(String inputted_expression) {
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

	@GetMapping(address)
	ResponseEntity<String> assumeResponsibility(@PathVariable String expression) throws Exception {
		return (ResponseEntity<String>) processTask(reformatBrackets(expression.replace(" ", "").replace('o', '(').replace('c', ')')));
	}



	public Boolean considerDelegation(String expression) {
		int other_operators = 0;
		int special_operators = 0;
		for (int x = 0; x < expression.length(); x++) {
			if (expression.charAt(x) == 'x' || expression.charAt(x) == '+' || expression.charAt(x) == '/' || expression.charAt(x) == '-') {
				if (expression.charAt(x) == 'x'|| expression.charAt(x) == '/') {
					special_operators++;
				} else
					other_operators++;
			} else if (expression.charAt(x) == '(' || expression.charAt(x) == ')')
				return true;
		}

		if ((special_operators > 0 && other_operators > 0))
			return true;
		
		return false;
	}

	public ResponseEntity<?> delegateTask (String expression) throws Exception {
        return ResponseEntity.ok(client.getForObject(address_of_next_handler + "{expression}", String.class, expression));
    }

	public ResponseEntity<String> completeTask(String expression) {	
		String[] input = toArray(expression);
		double num = Double.valueOf(input[0]);

        for (int x = 1; x < input.length; x += 2) {
            switch (input[x]) {
                case "+":
                    num += Double.valueOf(input[x+1]);
                    break;
                case "-":
                    num -= Double.valueOf(input[x+1]);
                    break;
                case "x":
                    num *= Double.valueOf(input[x+1]);
                    break;
                case "/":
                    num /= Double.valueOf(input[x+1]);
                    break;
                case "X":
                    num *= Double.valueOf(input[x+1]);
                    break;
            }
        }

        return ResponseEntity.ok(String.valueOf(num));
	}

	public static void main(String[] args) {
		SpringApplication.run(StraightforwardExpressionApplication.class, args);
	}

}
