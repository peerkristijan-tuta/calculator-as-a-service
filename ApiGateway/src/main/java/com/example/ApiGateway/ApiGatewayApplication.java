package com.example.ApiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;

@SpringBootApplication
@RestController
public class ApiGatewayApplication implements CommandLineRunner {

	String identifier;
	RestTemplate client = new RestTemplate();
	public void run(String... args) {
		try {
			new Presentation(this).execute();
		} catch (Exception exc) {
			System.out.print(exc.getMessage());
		}
	}

	String send(String expression) throws Exception {
		return client.getForObject("http://localhost:8101/receiveExpression/{expression}/{identifier}", String.class, expression, identifier);
	}

	String retrieveRecord() throws Exception {
		String record = "\n";
		String[][] list =  client.getForObject("http://localhost:8101/retrieveRecord/" + identifier, String[][].class);

		if (list == null)
			return "\nNo past computation records found.\n\n";
		else 
			for (int x = 0; x < list.length; x++) {
				if (x == 0)
					record += list.length + " past computations found for instance with code " + identifier + "\n";
				
				record += (x+1) + ". " + list[x][0] + " = " + list[x][1] + " | " + list[x][2] + "\n";
						
				if (list.length - x == 1) {
					record += "\n";
					return record;
				}
			}

		return "\nError: retrieveRecord\n\n";
	}

	@GetMapping("/exit")
	public void terminate() {
		System.exit(0);
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
}
