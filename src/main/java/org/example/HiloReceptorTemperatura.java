package org.example;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HiloReceptorTemperatura extends Thread{
    private Socket clienteTemperatura;
    private final BufferedReader br;
    private double temperatura;
    ConcurrentHashMap<String, Object> estado;

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public HiloReceptorTemperatura(Socket clienteTemperatura, ConcurrentHashMap<String, Object> estado) {
        this.clienteTemperatura = clienteTemperatura;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteTemperatura.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        while (true){
            try {
                String entrada = br.readLine();
                temperatura = Double.parseDouble(entrada);
                estado.put("temperatura", temperatura);
                sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}