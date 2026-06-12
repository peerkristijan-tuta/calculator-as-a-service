package com.example.BracketedExpression;

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

@Tag(name = "Bracketed Expression Handler")
@RestController
@SpringBootApplication
public class BracketedExpressionApplication extends ExpressionHandler {

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

	BracketedExpressionApplication() {
		criteria = new char[]{};
		address_of_next_handler = "";
	}

	String address_of_next_handler2 = "http://straightforward-expression:8102/assumeResponsibility/";
	String address_of_next_handler3 = "http://bodmas-expression:8103/assumeResponsibility/";

	boolean hasBrackets(String expression) {
        for (int x = 0; x < expression.length(); x++)
            if (expression.charAt(x) == '(') 
                return true;
            
        return false;
    }

	boolean needsBODMAS(String[] expression) { 
		int bodmas_flag = 0;
		int non_bodmas_flag = 0;
        for (int x = 1; x < expression.length; x += 2) 
            if (operatorNecessitatesBODMAS(expression[x]))
                bodmas_flag++;
			else non_bodmas_flag++;
			
        if (non_bodmas_flag > 0 && bodmas_flag > 0)
			return true;
		else        
        	return false;
    }

	String computeExpressionWithBrackets (String expression) { 
        class Helper {
            static int determineBracketDepth (String expression_to_be_analyzed) {
                int current_depth = 0;
                int maximum_depth = 0;

                for (int x = 0; x < expression_to_be_analyzed.length(); x++)
                    if (expression_to_be_analyzed.charAt(x) == '(') {
                        current_depth++;
                        if (current_depth > maximum_depth) 
                            maximum_depth++;
                    } else if (expression_to_be_analyzed.charAt(x) == ')')
                        current_depth--;

                return maximum_depth;
            }

            int findFirstTokenOfInnermostExpression(String expression_to_be_analyzed, int count) {
                for (int x = count; ; x--)
                    if (expression_to_be_analyzed.charAt(x) == '(')
                        return (x+1);
            }

            int findNumberOfInnermostSubExpressions(String expression_to_be_analyzed) {
                int layer = 0;
                int quantity = 0;

                for (int x = 0; x < expression_to_be_analyzed.length(); x++) {
                    if (expression_to_be_analyzed.charAt(x) == '(') 
                        layer++;
                    else if (expression_to_be_analyzed.charAt(x) == ')') {
                        if (layer == determineBracketDepth(expression_to_be_analyzed)) {
                            quantity++;
                            layer--;
                        } else 
                            layer--;
                    }
                }

                return quantity;
            }

            int[][] findInnermostExpressions(String expression_to_be_analyzed) {
                int[][] innermost_expression_locations = new int [findNumberOfInnermostSubExpressions(expression_to_be_analyzed)][2];

                for (int x = 0; x < innermost_expression_locations.length; x++)
                    for (int y = 0; y < innermost_expression_locations[0].length; y++)
                        innermost_expression_locations[x][y] = 0;

                int first_dimensional_increment = 0;
                int second_dimensional_increment = 0;
                int layer = 0;
                int depth = determineBracketDepth(expression_to_be_analyzed);

                for (int x = 0; x < expression_to_be_analyzed.length(); x++) {
                    if (expression_to_be_analyzed.charAt(x) == '(') {
                        layer++;
                    } else if (expression_to_be_analyzed.charAt(x) == ')') {
                        if (layer == depth) {
                            innermost_expression_locations[first_dimensional_increment][second_dimensional_increment++] = 
                                findFirstTokenOfInnermostExpression(expression_to_be_analyzed, x);
                            innermost_expression_locations[first_dimensional_increment++][second_dimensional_increment] = x-1;
                            layer--;
                            second_dimensional_increment = 0;
                        } else 
                            layer--;
                    }
                }

                return innermost_expression_locations;
            }

            boolean isInnermost(int index, int[][] innermost_sub_expression_indexes) {
                for (int x = 0; x < innermost_sub_expression_indexes.length; x++)
                    for (int y = 0; y < innermost_sub_expression_indexes[0].length; y++)
                        if (index == innermost_sub_expression_indexes[x][y]) return true;

                return false;
            }

            static String retrieveSubExpression (int count, String expression_to_be_computed) {
                String sub_expression = "";

                for (int x = count + 1; x < expression_to_be_computed.length(); x++)
                    if (expression_to_be_computed.charAt(x) == ')') 
                        break;
                    else 
                        sub_expression += expression_to_be_computed.charAt(x);
        
                return sub_expression;
            }

            int retrieveNextPoint(int current_index, String expression_to_be_computed) {
                int next_point = 0;
        
                for (int x = current_index; x < expression_to_be_computed.length(); x++)
                    if (expression_to_be_computed.charAt(x) == ')') 
                        return (x+1);

                return 0;
            }

            String solveBrackets (String expression_to_be_computed) {
                if (hasBrackets(expression_to_be_computed)) {
                    int[][] innermost_expression_locations = findInnermostExpressions(expression_to_be_computed);
                    String new_expression = "";
                    int first_dimensional_increment = 0;
                    int second_dimensional_increment = 0;
                    int count = 0;

                    for (int x = 0; x < expression_to_be_computed.length(); x++) {           
                        if (expression_to_be_computed.charAt(x) != '(') {
                            new_expression += expression_to_be_computed.charAt(x);

                            if (expression_to_be_computed.length() - x == 1) {
								System.out.println(new_expression);
                                return solveBrackets(new_expression);
							}
                        } else {
                            if (isInnermost((x+1), innermost_expression_locations)) {
                                if (needsBODMAS(toArray(retrieveSubExpression(x, expression_to_be_computed))))
									new_expression += BracketedExpressionApplication.this.client.getForObject(address_of_next_handler3 + "{expression}", String.class, 
													  retrieveSubExpression(x, expression_to_be_computed).replace("/", "d"));
                                else
                                    new_expression += BracketedExpressionApplication.this.client.getForObject(address_of_next_handler2 + "{expression}", String.class, 
													  retrieveSubExpression(x, expression_to_be_computed).replace("/", "d")+"b");

                                x = retrieveNextPoint(x, expression_to_be_computed);

                                if (x == expression_to_be_computed.length()) {
									System.out.println(new_expression);
                                    return solveBrackets(new_expression);
                                } else if (expression_to_be_computed.length() - x == 1) {
                                    new_expression += expression_to_be_computed.charAt(x);
									System.out.println(new_expression);
                                    return solveBrackets(new_expression);
                                } else
                                    new_expression += expression_to_be_computed.charAt(x);
                            } else {
                                new_expression += expression_to_be_computed.charAt(x);
                                if (expression_to_be_computed.length() - x == 1) {
									System.out.println(new_expression);
                                    return solveBrackets(new_expression);
								}
                            }
                        }
                    }
                } else
					if (needsBODMAS(toArray(expression_to_be_computed)))
                        return BracketedExpressionApplication.this.client.getForObject(address_of_next_handler3 + "{expression}", String.class, 
							   expression_to_be_computed.replace("/", "d"));
                    else
                        return BracketedExpressionApplication.this.client.getForObject(address_of_next_handler2 + "{expression}", String.class, 
							   expression_to_be_computed.replace("/", "d")+"b");
                
                return solveBrackets(expression_to_be_computed);
            }
        }

        Helper h = new Helper();

        return h.solveBrackets(expression);
    }

    @Operation(summary = "Delegate or handle brackets in expression")
	@GetMapping(address)
	ResponseEntity<String> assumeResponsibility(@Parameter(description = "Expression to be processed") @PathVariable String expression) throws Exception {
		return (ResponseEntity<String>) processTask(expression);
	}

	public ResponseEntity<String> completeTask(String expression) {	
		return ResponseEntity.ok(computeExpressionWithBrackets(expression));
	}

	public static void main(String[] args) {
		SpringApplication.run(BracketedExpressionApplication.class, args);
	}

}
