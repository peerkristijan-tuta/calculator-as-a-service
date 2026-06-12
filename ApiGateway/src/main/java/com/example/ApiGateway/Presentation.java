package com.example.ApiGateway;

import java.util.Scanner;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

class Presentation {
    Scanner scanner = new Scanner(System.in);
    ApiGatewayApplication app;
    Presentation (ApiGatewayApplication app) {
        this.app = app;
    }

    void execute() throws Exception {
        System.out.print("Provide identifier for API Gateway: ");
        app.identifier = scanner.nextLine();
        while (true) {
            System.out.print(app.retrieveRecord());
            System.out.print("Provide expression to compute: ");
            String response = scanner.nextLine();
            if (response.equals("exit")) {
                System.exit(0);
            } else {
                System.out.println(app.send(response.replace('/', 'd').replace('(', 'o').replace(')', 'c')));
            }
        }
    }
}