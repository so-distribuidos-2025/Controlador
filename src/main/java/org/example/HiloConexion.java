package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HiloConexion extends Thread{
    private Socket s;
    String tipoDispositivo = "";
    ConcurrentHashMap<String, Object> estado;

    public HiloConexion(Socket s, ConcurrentHashMap<String, Object> estado) {
        this.s = s;
        this.estado = estado;
    }

    public void run(){
        try {
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        tipoDispositivo = br.readLine();
        switch (tipoDispositivo){
            case "humedad":
                int id = Integer.parseInt(br.readLine());
                System.out.println("---Conectado sensor humedad---");
                HiloReceptorHumedad receptor = new HiloReceptorHumedad(s, estado, id);
                receptor.start();
                break;
            case "temperatura":
                System.out.println("---Conectado sensor temperatura---");
                HiloReceptorTemperatura receptorT = new HiloReceptorTemperatura(s, estado);
                receptorT.start();
                break;
            case "lluvia":
                System.out.println("---Conectado sensor lluvia---");
                HiloReceptorLluvia receptorL = new HiloReceptorLluvia(s, estado);
                receptorL.start();
                break;
            case "temporizador":
                System.out.println("---Conectado al temporizador!---");
                HiloReceptorTiempo receptorTiempo = new HiloReceptorTiempo(s);
                receptorTiempo.run();
            case "iluminacion":
                System.out.println("---Conectado sensor iluminacion---");
                HiloReceptorIluminacion receptorIluminacion = new HiloReceptorIluminacion(s, estado);
                receptorIluminacion.run();
            default:
                System.out.println("Disposivo no reconocido");
                break;
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
