package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println("Server in ascolto sulla porta 3000...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connesso: " + clientSocket.getInetAddress());

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String firstLine = in.readLine();
                    String[] url= firstLine.split(" ");
                    // Leggi e stampa la richiesta HTTP completa
                    String requestLine;
                    while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
                        System.out.println(requestLine);  // Stampa ogni riga della richiesta
                    }
                    System.out.println("Path: "+ url[1]);

                    if(url[1].equals("/ciao.html")){
                        String body = "<html><body><strong>Vongolo</strong></body></html>";
                        int contentLength = body.length();
    
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/html");
                        out.println("Content-Length: " + contentLength);
                        out.println("");  // Riga vuota separa intestazioni dal corpo
                        out.println(body); // Corpo della risposta
                    }else if(url[1].equals("/ciao")){
                        out.println("HTTP/1.1 301 Found");
                        out.println("Location: /nuovapagina");
                        out.println("");  // Riga vuota separa intestazioni dal corpo
                        
                    }else if(url[1].equals("/*")){
                        out.println("HTTP/1.1 404 ERROR");

                    }
                    // Risposta del server

                    System.out.println("Risposta inviata al client.");

                    in.close();
                    out.close();
                } catch (IOException e) {
                    System.err.println("Errore nella gestione della connessione con il client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nell'avvio del server: " + e.getMessage());
        }
    }
}
