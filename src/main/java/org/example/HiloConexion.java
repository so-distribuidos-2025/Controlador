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
            int id;
        switch (tipoDispositivo){
            case "humedad":
                id = Integer.parseInt(br.readLine()); //Leer id
                System.out.printf("---Conectado sensor humedad %d---\n", id);
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
                id = Integer.parseInt(br.readLine()); //Leer id
                System.out.printf("---Conectado temporizador %d---\n", id);
                HiloReceptorTiempo receptorTiempo = new HiloReceptorTiempo(s, estado);
                receptorTiempo.run();
            case "iluminacion":
                System.out.println("---Conectado sensor iluminacion---");
                HiloReceptorIluminacion receptorIluminacion = new HiloReceptorIluminacion(s, estado);
                receptorIluminacion.run();
            case "electrovalvula":
                id = Integer.parseInt(br.readLine()); //Leer id
                System.out.printf("---Conectado electrovalvula %d---\n", id);
            default:
                System.out.println("Disposivo no reconocido");
                break;
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
