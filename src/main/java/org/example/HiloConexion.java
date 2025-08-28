package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HiloConexion extends Thread{
    private Socket s;
    String tipoDispositivo = "";

    public HiloConexion(Socket s) {
        this.s = s;
    }

    public void run(){
        try {
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        tipoDispositivo = br.readLine();
        switch (tipoDispositivo){
            case "humedad":
                System.out.println("---Conectado sensor humedad---");
                HiloReceptorHumedad receptor = new HiloReceptorHumedad(s);
                receptor.start();
                break;
            case "temperatura":
                System.out.println("---Conectado sensor temperatura---");
                HiloReceptorTemperatura receptorT = new HiloReceptorTemperatura(s);
                receptorT.start();
                break;
            case "lluvia":
                System.out.println("---Conectado sensor lluvia---");
                HiloReceptorLluvia receptorL = new HiloReceptorLluvia(s);
                receptorL.start();
                break;
            case "temporizador":
                System.out.println("---Conectado al temporizador!---");
                HiloReceptorTiempo receptorTiempo = new HiloReceptorTiempo(s);
                receptorTiempo.run();
            case "iluminacion":
                System.out.println("---Conectado sensor de Iluminación ---");
                HiloReceptorIluminacion receptorI = new HiloReceptorIluminacion(s);
                receptorI.start();
            default:
                System.out.println("Disposivo no reconocido");
                break;
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
