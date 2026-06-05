package com.example.CalculatorFacade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.springframework.web.util.UriUtils;

@RestController
@SpringBootApplication
public class CalculatorFacadeApplication {

	@Autowired
	Data data;

	String processResult(String expression, String result, String identifier) {
		List<ComputationRecord> records = data.findAll();
		int count = 0;
		for (int x = 0; x < records.size(); x++)
			if (records.get(x).identifier.equals(identifier))
				count++;

		int index = 0;
		Date earliest = null;

		if (count == 5) {
			for (int x = 0; x < records.size(); x++)
				if (records.get(x).identifier.equals(identifier))
					if (earliest == null || records.get(x).time.before(earliest)) {
						earliest = records.get(x).time;
						index = x;
					}
			records.remove(index);
		}
	
		data.deleteAll();
		data.saveAll(records);
		data.save(new ComputationRecord(expression, result, new Date(), identifier));
		return result;
	}

	String delegateToHandler(String expression) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8102/assumeResponsibility/" + UriUtils.encodePath(expression, "UTF-8"))).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
	}

	@GetMapping("/retrieveRecord/{identifier}")
	public ResponseEntity<ArrayList<String[]>> retrieveRecord(@PathVariable String identifier) {
		List<ComputationRecord> full_record = data.findAll();
		ArrayList<String[]> retrieved = new ArrayList<String[]>();
		ComputationRecord[] temporary_retrieved;

		int quantity = 0;
		for (int x = 0; x < full_record.size(); x++)
			if (full_record.get(x).identifier.equals(identifier))
				quantity++;
		
		if (quantity == 0)
			return ResponseEntity.ok(null);

		Date[] dates = new Date[quantity];
		int increment = 0;
		for (int x = 0; x < full_record.size(); x++)
			if (full_record.get(x).identifier.equals(identifier))
				dates[increment++] = full_record.get(x).time;
		
		while (true) {
			boolean changed = false;
			for (int x = 0; x < quantity; x++) {
				if (quantity - x != 1)
					for (int y =  x + 1; y < quantity; y++)
						if (x != y)
							if (dates[y].before(dates[x])) {
								Date[] temporary_dates = {dates[x], dates[y]};
								dates[x] = temporary_dates[1];
								dates[y] = temporary_dates[0];
								changed = true;
							}
			}
			if (!changed)
				break;
		}

		for (int x = 0; x < quantity; x++) {
    		for (int z = 0; z < full_record.size(); z++) {
        		if (full_record.get(z).identifier.equals(identifier) && full_record.get(z).time.equals(dates[x])) {
            		retrieved.add(new String[]{full_record.get(z).expression.replace('d', '/').replace('o', '(').replace('c', ')'), full_record.get(z).result, full_record.get(z).time.toString()});
            		break;
        		}
    		}
		}
		
		return ResponseEntity.ok(retrieved);
	}

	@GetMapping("/receiveExpression/{expression}/{identifier}")	
	public ResponseEntity<String> receiveExpression(@PathVariable String expression, @PathVariable String identifier) throws Exception {
		return ResponseEntity.ok(processResult(expression, delegateToHandler(expression), identifier));
	}

	public static void main(String[] args) {
		SpringApplication.run(CalculatorFacadeApplication.class, args);
	}

}
