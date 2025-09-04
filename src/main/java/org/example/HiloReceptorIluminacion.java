/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Anita
 */
public class HiloReceptorIluminacion extends Thread {
    
    private Socket clienteIluminacion;
    private final BufferedReader br;
    private double iluminacion;
    ConcurrentHashMap<String, Object> estado;

    public double getIlumicacion() {
        return iluminacion;
    }

    public void setIlumicacion(double iluminacion) {
        this.iluminacion = iluminacion;
    }

    public HiloReceptorIluminacion(Socket clienteIluminacion, ConcurrentHashMap<String, Object> estado) {
        this.clienteIluminacion = clienteIluminacion;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteIluminacion.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        while (true){
            try {
                String entrada = br.readLine();
                iluminacion = Double.parseDouble(entrada);
                estado.put("radiacion", iluminacion);
                sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
    
}
