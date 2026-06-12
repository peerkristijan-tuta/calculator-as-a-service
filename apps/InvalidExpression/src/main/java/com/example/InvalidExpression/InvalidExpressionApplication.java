package com.example.InvalidExpression;

import com.example.ExpressionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Invalid Expression Handler")
@RestController
@SpringBootApplication
public class InvalidExpressionApplication extends ExpressionHandler {

	InvalidExpressionApplication() {
		address_of_next_handler = "";
	}

	public ResponseEntity<String> completeTask(String expression) throws Exception {

		StringBuilder ErrorMessage = new StringBuilder();
		ErrorMessage.append("\n\n");
		ArrayList<Integer> problematic_operators = new ArrayList<Integer>();
        HashMap<Integer, Integer> problematic_opening_brackets = new HashMap<Integer, Integer>();
        ArrayList<Integer> problematic_closing_brackets = new ArrayList<Integer>();
        ArrayList<Integer> alphabets = new ArrayList<Integer>();

        boolean bracket_opened = false;

        Integer bracket_depth = 0;
        final Integer last_token = expression.length() - 1;

        for (Integer x = 0; x < expression.length(); x++)
            if (((64 < (int) expression.charAt(x) && expression.charAt(x) < 91) || (96 < (int) expression.charAt(x) && (int) expression.charAt(x) < 123))
                && ((int) expression.charAt(x) != 120 && (int) expression.charAt(x) != 88))
                alphabets.add(x);
            else if (47 < (int) expression.charAt(x) && expression.charAt(x) < 58)
                problematic_operators = new ArrayList<Integer>();
            else
                switch ((int) expression.charAt(x)) {
                    case 120, 45, 47, 43:
                        problematic_operators.add(x);

                        break;
                    case 40:
                        problematic_opening_brackets.put(++bracket_depth, x);
                        bracket_opened = true;

                        break;
                    case 41:
                        if (!bracket_opened)
                            problematic_closing_brackets.add(x);
                        else
                            problematic_opening_brackets.remove(bracket_depth--);

                        if (bracket_depth == 0)
                            bracket_opened = false;    

                        break;
                    }

        interface dashPrinter {
                void method(String expression);
        }

        dashPrinter dash_printer = (n) -> {
            ErrorMessage.append("\n");

            for (int x = 0; x < ("  issue(s) | ".length() + n.length()); x++)
                ErrorMessage.append("-");

            ErrorMessage.append("\n");
        };

        dash_printer.method(expression);
 
        ErrorMessage.append("expression | " + expression);

        dash_printer.method(expression);

        ErrorMessage.append("  issue(s) | ");


        HashMap<String, ArrayList<Integer>> problematic_tokens = new HashMap<String, ArrayList<Integer>>();
        problematic_tokens.put("operators", problematic_operators);
        problematic_tokens.put("opening brackets", new ArrayList<Integer>());
        problematic_tokens.put("closing brackets", problematic_closing_brackets);

        for (Integer problematic_opening_bracket_locations : problematic_opening_brackets.values())
            problematic_tokens.get("opening brackets").add(problematic_opening_bracket_locations);

        problematic_tokens.put("alphabets", alphabets);
            
        String[] token_types = {"operators", "opening brackets", "closing brackets", "alphabets"};
        Integer[][] all_problematic_tokens = new Integer[4][];
        int count = 0;

        for (String token_type : token_types)
            all_problematic_tokens[count++] = problematic_tokens.get(token_type).toArray(new Integer[0]);

        HashMap<Integer, String> problematic_token_classification = new HashMap<Integer, String>();
        count = 0;
        boolean problem_alerted = false;
        boolean problematic_token_found = false;

        for (Integer x = 0; x < expression.length(); x++)
            for (Integer y = 0; y < all_problematic_tokens.length; y++)
                for (Integer z = 0; z < all_problematic_tokens[y].length; z++)
                    if (x == all_problematic_tokens[y][z])
                        problematic_token_found = true;

        if (problematic_token_found) {

        for (Integer x = 0; x < expression.length(); x++) {
            for (Integer y = 0; y < all_problematic_tokens.length; y++)
                for (Integer z = 0; z < all_problematic_tokens[y].length; z++)
                    if (x == all_problematic_tokens[y][z]) {
                        ErrorMessage.append(++count);

                        switch (y) {
                        	case 0:
                                problematic_token_classification.put(x, "this operator does not have an operand to operate on");
                                break;
                            case 1:
                                problematic_token_classification.put(x, "this opening bracket is not followed by a closing bracket later in the expression");
                                break;
                            case 2:
                                problematic_token_classification.put(x, "this closing bracket is not closing any opening bracket");
                                break;
                            case 3:
                                problematic_token_classification.put(x, "alphabets are not supported by this calculator");
                                break;
                        }

                    problem_alerted = true;
                }
                
            if (problem_alerted)
                problem_alerted = false;
            else
                ErrorMessage.append(" ");
        }

        } else {
            boolean end_loop = false;
            for (int x = 0; x < expression.length(); x++) {
                if (x == 0 && (expression.charAt(x) == 'x' || expression.charAt(x) == '/')) {
                    end_loop = true;
                    ErrorMessage.append(++count); 
                    for (int y = 0; y < (expression.length()-1); y++)
                        ErrorMessage.append(" ");
                    problematic_token_classification.put(x, "This operator does not have the required operand on the left to operate on.");
                    break;
                } else if (expression.length() - x > 1) {
                    switch ((int) expression.charAt(x)) {
                        case 120, 47:
                            switch ((int) expression.charAt(x+1)) {
                                case 120, 47:
                                    problematic_token_classification.put(x, "This operator cannot operate on the one that comes after it.");
                                    ErrorMessage.append(++count);
                                    for (int y = x+1; y < expression.length(); y++)
                                        ErrorMessage.append(" ");
                                    end_loop = true;
                                    break;
                                default:
                                    ErrorMessage.append(" ");
                            }
                            break;
                        case 45, 43:
                            switch ((int) expression.charAt(x+1)) {
                                case 120, 47:
                                    problematic_token_classification.put(x, "This operator cannot operate on the one that comes after it.");
                                    ErrorMessage.append(++count);
                                    for (int y = x+1; y < expression.length(); y++)
                                        ErrorMessage.append(" ");
                                    end_loop = true;
                                    break;
                                case 45, 43:
                                    if ((int) expression.charAt(x-1) == 45 || (int) expression.charAt(x-1) == 43) {
                                        ErrorMessage.append(++count); 
                                        for (int y = x+1; y < (expression.length()); y++)
                                            ErrorMessage.append(" ");
                                        problematic_token_classification.put(x, "This operator does not have the required operand on the left to operate on.");
                                        end_loop = true;
                                    } else {
                                        ErrorMessage.append(" ");
                                    }
                                    break;
                                default:
                                    ErrorMessage.append(" ");
                            }
                            break;
                        default:
                            ErrorMessage.append(" ");
                    }
                } else 
                    ErrorMessage.append(" ");

                if (end_loop)
                    break;
            }
        }
            
        dash_printer.method(expression);

        ErrorMessage.append("Explaination of Issue(s)");
        count = 1;

        for (Integer problematic_token : problematic_token_classification.keySet()) {
            ErrorMessage.append("\n");
            ErrorMessage.append((count++) + ". " + "Token no. " + (problematic_token+1) + ", '" + 
                            expression.charAt(problematic_token) + "'," + " is invalid because ");
            ErrorMessage.append(problematic_token_classification.get(problematic_token));
        }

        ErrorMessage.append("\n\n");
		return ResponseEntity.ok(ErrorMessage.toString());
    }

    @Operation(summary = "Identify and explain invalid tokens")
	@GetMapping(address)
	ResponseEntity<String> assumeResponsibility(@Parameter(description = "Expression to be processed") @PathVariable String expression) throws Exception {
		return (ResponseEntity<String>) completeTask(expression.replace(" ", "").replace('o', '(').replace('c', ')').replace('d', '/'));
	}		

	public static void main(String[] args) {
		SpringApplication.run(InvalidExpressionApplication.class, args);
	}
}