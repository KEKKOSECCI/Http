package com.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server avviato sulla porta 3000...");

        // ciclo infinito: accettiamo i client uno alla volta
        while (true) {
            Socket socket = server.accept();
            handleRequest(socket);
        }
    }

    private static void handleRequest(Socket socket) throws IOException {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)
        );

        OutputStream out = socket.getOutputStream();

        // ----- LETTURA RIGA DI RICHIESTA -----
        String requestLine = in.readLine();
        if (requestLine == null) {
            socket.close();
            return;
        }

        // ignoriamo gli header
        while (true) {
            String header = in.readLine();
            if (header == null || header.isEmpty()) break;
        }

        // ----- PARSING -----
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path   = parts[1];

        // ----- CONTROLLI -----
        if (!method.equals("GET")) {
            sendTextResponse(out, 405, "Method Not Allowed", "Metodo non supportato");
            socket.close();
            return;
        }
        
        System.out.println(path);
        if (path.endsWith("/")) {
            path += "index.html";
        }
        File root = new File("sito").getCanonicalFile();
        File file = new File(root, path.substring(1)).getCanonicalFile();
        if(file.isDirectory() ){
            String header =
            "HTTP/1.1 " + "302" + " " + "Found" + "\r\n" +
            "Location: " + path+"/"+ "\r\n" +
            "\r\n";
            out.write(header.getBytes(StandardCharsets.US_ASCII)); 
            return;
        }

        if (!file.exists()|| !file.getPath().startsWith(root.getPath())) {
            sendTextResponse(out, 404, "Not Found", "File non trovato");
            socket.close();
            return;
        }

        // ----- INVIO FILE -----
        sendFile(out, file);

        socket.close();
    }

    // ---------------------------------------------------------
    // Risposta testuale semplice
    // ---------------------------------------------------------
    private static void sendTextResponse(OutputStream out, int code, String status, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);

        String header =
                "HTTP/1.1 " + code + " " + status + "\r\n" +
                "Content-Length: " + data.length + "\r\n" +
                "Content-Type: text/plain; charset=utf-8\r\n" +
                "\r\n";

        out.write(header.getBytes(StandardCharsets.US_ASCII));
        out.write(data);
    }

    // ---------------------------------------------------------
    // Risposta file
    // ---------------------------------------------------------
    private static void sendFile(OutputStream out, File file) throws IOException {

        String header =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Content-Type: " + getContentType(file) + "\r\n" +
                "\r\n";

        out.write(header.getBytes(StandardCharsets.US_ASCII));

        try (InputStream fis = new FileInputStream(file)) {
            fis.transferTo(out); // Java semplificato
        }
    }

    // ---------------------------------------------------------
    // Content-Type
    // ---------------------------------------------------------
    private static String getContentType(File f) {
        String name = f.getName().toLowerCase();

        if (name.endsWith(".html")) return "text/html; charset=utf-8";
        if (name.endsWith(".css"))  return "text/css";
        if (name.endsWith(".js"))   return "application/javascript";
        if (name.endsWith(".png"))  return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }
}
