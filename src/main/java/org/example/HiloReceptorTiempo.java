package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HiloReceptorTiempo implements Runnable{

    private Socket clienteTiempo;
    private final BufferedReader br;
    private int totalSegundos;
    private RuntimeException runtimeException;


    public int getTotalSegundos() {
        return totalSegundos;
    }

    public void setTotalSegundos(int totalSegundos) {
        this.totalSegundos = totalSegundos;
    }

    public HiloReceptorTiempo(Socket clienteTemporizador) {
        this.clienteTiempo = clienteTemporizador;
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
                String entrada = br.readLine();
                totalSegundos = Integer.parseInt(entrada);
                System.out.println("Segundos restantes: " + totalSegundos);

            } catch (IOException e) {
                throw runtimeException;
        }
    }
}
}
