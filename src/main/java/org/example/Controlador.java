package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Controlador {
    public static void main(String[] args) {
    // Crear el ConcurrentHashMap que tiene todos los datos del estado
    ConcurrentHashMap<String, Object> estado = new ConcurrentHashMap<>();

        try {
            ServerSocket server = new ServerSocket(20000);
            HiloControlador controladorPrint = new HiloControlador(estado);
            controladorPrint.start();
            while (true) {
                Socket s = server.accept();
                HiloConexion handler = new HiloConexion(s, estado);
                handler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}