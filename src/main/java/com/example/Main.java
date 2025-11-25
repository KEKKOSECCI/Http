package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);
        do {
            System.out.println("stiamo cercando un Client");
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println(in.readLine());
            String response;
            do {
                response = in.readLine();
                System.out.println(response);
            } while (!response.equals(""));
            System.out.println("Finito");
        } while (true);
    }
}