package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HiloReceptorHumedad extends Thread {
    private Socket clienteHumedad;
    private final BufferedReader br;
    private double humedad;
    ConcurrentHashMap<String, Object> estado;
    private int id;
    ConcurrentHashMap<String, Double> humedades;

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    public HiloReceptorHumedad(Socket clienteHumedad, ConcurrentHashMap<String, Object> estado, int id) {
        this.clienteHumedad = clienteHumedad;
        this.id = id;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteHumedad.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        while (true) {
            try {
                String entrada = br.readLine();
                humedad = Double.parseDouble(entrada);
                humedades = (ConcurrentHashMap<String, Double>) this.estado.get("humedades");

                humedades.put(String.valueOf(id), humedad);

                sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}