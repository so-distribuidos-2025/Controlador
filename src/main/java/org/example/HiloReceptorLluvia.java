package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HiloReceptorLluvia extends Thread{
    private Socket clientelluvia;
    private final BufferedReader br;
    private boolean lluvia;
    ConcurrentHashMap<String, Object> estado;

    public boolean getlluvia() {
        return lluvia;
    }

    public void setlluvia(boolean lluvia) {
        this.lluvia = lluvia;
    }

    public HiloReceptorLluvia(Socket clientelluvia, ConcurrentHashMap<String, Object> estado) {
        this.clientelluvia = clientelluvia;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clientelluvia.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        while (true){
            try {
                String entrada = br.readLine();
                lluvia = Double.parseDouble(entrada) == 1.0;
                this.estado.put("lluvia", lluvia);
                sleep(1000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}