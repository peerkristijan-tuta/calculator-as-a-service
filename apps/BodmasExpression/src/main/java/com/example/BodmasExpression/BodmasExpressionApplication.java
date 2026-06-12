package com.example.BodmasExpression;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;
import com.example.ExpressionHandler;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Bodmas Expression Handler")
@RestController
@SpringBootApplication
public class BodmasExpressionApplication extends ExpressionHandler {

	BodmasExpressionApplication() {
		criteria = new char[]{'(', ')'};
		address_of_next_handler = "http://bracketed-expression:8104/assumeResponsibility/";
	}

	String address_of_next_handler2 = "http://straightforward-expression:8102/assumeResponsibility/";

	boolean operatorNecessitatesBODMAS(String operator) { 
            switch (operator) {
                case "x":
                    return true;
                case "/":
                    return true;
                default:
                    return false;
        }
    }

	ResponseEntity<String> computeExpressionWithBODMAS(String[] expression) { 
        class Helper {
            int[] findAffectedOperators() {
                int size = 0;

                for (int x = 1; x < expression.length; x += 2)
                    if (operatorNecessitatesBODMAS(expression[x])) 
                        size++;

                int[] affected_operators = new int[size];
                int increment = 0;

                for (int x = 1; x < expression.length; x +=2)
                    if (operatorNecessitatesBODMAS(expression[x])) 
                        affected_operators[increment++] = x;

                return affected_operators;
            }

            String[] computeSubExpressionsThatRequireBODMAS(int[] affected_operators) { 
                int size = 0;

                if (affected_operators.length == 0) 
                    return expression;
        
                for (int x = 0; x < affected_operators.length; x++)
                    if (expression.length - affected_operators[x] == 2)
                        size++;
                    else if (!operatorNecessitatesBODMAS(expression[affected_operators[x]+2]))
                        size++;

                String[] computed_sub_expressions = new String[size];
                
                for (int x = 0; x < computed_sub_expressions.length; x++) 
                    computed_sub_expressions[x] = "";

                int increment = 0;

                for (int x = 0; x < affected_operators.length; x++) {
                    if (affected_operators.length == 1) {           
                        computed_sub_expressions[increment] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        break;
                    } else if (x == 0) {            
                        if (affected_operators[x+1] - affected_operators[x] == 2) 
                            computed_sub_expressions[increment] = expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        else 
                            computed_sub_expressions[increment++] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                    } else if (expression.length - affected_operators[x] == 2) {          
                        if (affected_operators[x] - affected_operators[x-1] == 2)
                            computed_sub_expressions[increment] += expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        else           
                            computed_sub_expressions[increment] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                    } else if (affected_operators.length - x == 1) {            
                        if (affected_operators[x] - affected_operators[x-1] == 2) 
                            computed_sub_expressions[increment] += expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        else 
                            computed_sub_expressions[increment] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                    } else if (affected_operators[x+1] - affected_operators[x] == 2) {       
                        if (affected_operators[x] - affected_operators[x-1] == 2)      
                            computed_sub_expressions[increment] += expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        else       
                            computed_sub_expressions[increment] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                    } else if (affected_operators[x+1] - affected_operators[x] != 2 && affected_operators[x] - affected_operators[x-1] == 2)
                        computed_sub_expressions[increment++] += expression[affected_operators[x]] + expression[affected_operators[x]+1];
                    else { 
                        if (affected_operators[x] - affected_operators[x-1] == 2)
                            computed_sub_expressions[increment] += expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        else {
                            if (x == 0) 
                                computed_sub_expressions[increment++] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                            else 
                                computed_sub_expressions[increment++] += expression[affected_operators[x]-1] + expression[affected_operators[x]] + expression[affected_operators[x]+1];
                        }
                    }
                }

                for (int x = 0; x < computed_sub_expressions.length; x++) 
					computed_sub_expressions[x] = BodmasExpressionApplication.this.client.getForObject(
						address_of_next_handler2 + "{expression}", String.class, computed_sub_expressions[x].replace("/", "d")+"b");

                return computed_sub_expressions;
            }

            String[] integrateComputedSubExpressionsThatRequireBODMAS (String[] computed_sub_expressions) {
                String new_expression = "";
                int increment = 0;
                boolean bodmas_recently_inserted = false;

                if (expression.length == 3) {
                    for (String string : expression) 
                        new_expression += string;

                    return toArray(new_expression);
                }

                for (int x = 1; x < expression.length; x += 2) {
                    if ((!expression[x].equals("x")) && (!expression[x].equals("/"))) {
                        if (bodmas_recently_inserted == false) {
                            new_expression += expression[x-1] + expression[x];

                            if (expression.length - x == 2)
                                new_expression += expression[x+1];
                        } else {
                            new_expression += expression[x];
                            bodmas_recently_inserted = false;

                            if (expression.length - x == 2) 
                                new_expression += expression[x+1];
                        }
                    } else {
                        new_expression += computed_sub_expressions[increment++];
                        bodmas_recently_inserted = true;

                        for (int y = x; y < expression.length; y += 2) 
                            if ((!expression[y].equals("x")) && (!expression[y].equals("/"))) {
                                x = y-2;
                                break;
                            } else if (expression.length - y == 2) {
                                x = expression.length;
                                break;
                            }
                    }
                }

                return toArray(new_expression);
            }
        }



        Helper h = new Helper();

		return ResponseEntity.ok(client.getForObject(address_of_next_handler2 + "{expression}", String.class,
			toString(h.integrateComputedSubExpressionsThatRequireBODMAS(h.computeSubExpressionsThatRequireBODMAS(h.findAffectedOperators())))+"b"
			));
    }

    @Operation(summary = "Delegate or handle BODMAS expression (not including brackets)")
	@GetMapping(address)
	ResponseEntity<String> assumeResponsibility(@Parameter(description = "Expression to be processed") @PathVariable String expression) throws Exception {
		return (ResponseEntity<String>) processTask(expression);
	}

	public ResponseEntity<String> completeTask(String expression) {	
		return computeExpressionWithBODMAS(toArray(expression));
	}

	public static void main(String[] args) {
		SpringApplication.run(BodmasExpressionApplication.class, args);
	}
}
