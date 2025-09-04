package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HiloReceptorTiempo implements Runnable{

    private Socket clienteTiempo;
    private final BufferedReader br;
    private int totalSegundos;
    private RuntimeException runtimeException;
    ConcurrentHashMap<String, Object> estado;
    ConcurrentHashMap<String, Integer> temporizadores;
    private int id;


    public int getTotalSegundos() {
        return totalSegundos;
    }

    public void setTotalSegundos(int totalSegundos) {
        this.totalSegundos = totalSegundos;
    }

    public HiloReceptorTiempo(Socket clienteTemporizador, ConcurrentHashMap<String, Object> estado) {
        this.clienteTiempo = clienteTemporizador;
        this.estado = estado;
        this.temporizadores = (ConcurrentHashMap<String, Integer>) this.estado.get("temporizadores");
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteTemporizador.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                totalSegundos = (Integer) this.temporizadores.get(String.valueOf(this.id));
                if (totalSegundos != 0) {
                    String entrada = br.readLine();
                    totalSegundos = Integer.parseInt(entrada);
                    System.out.printf("temporizador %d parado\n", id);
                }
            } catch (IOException e) {
                throw runtimeException;
        }
    }
}
}
