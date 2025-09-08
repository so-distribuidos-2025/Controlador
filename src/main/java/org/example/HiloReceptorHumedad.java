package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hilo encargado de recibir datos de un sensor de humedad específico.
 * 
 * <p>Este hilo se conecta a un cliente que envía valores de humedad en formato texto.
 * Cada valor recibido se convierte a {@code double} y se almacena en el
 * {@link ConcurrentHashMap} de estado compartido bajo la clave correspondiente
 * al ID del sensor.</p>
 */
public class HiloReceptorHumedad extends Thread {
    private Socket clienteHumedad;
    private final BufferedReader br;
    private double humedad;
    private int id;
    private ConcurrentHashMap<String, Object> estado;
    private ConcurrentHashMap<String, Double> humedades;

    /**
     * Devuelve el último valor de humedad recibido.
     * 
     * @return valor de humedad en porcentaje
     */
    public double getHumedad() {
        return humedad;
    }

    /**
     * Establece manualmente el valor de humedad.
     * 
     * @param humedad nuevo valor de humedad
     */
    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    /**
     * Constructor de la clase.
     *
     * @param clienteHumedad socket del cliente que envía los datos de humedad
     * @param estado estructura compartida con el estado global del sistema
     * @param id identificador único del sensor de humedad
     */
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

    /**
     * Bucle principal del hilo.
     *
     * <p>Lee continuamente datos del socket, los convierte a {@code double}
     * y actualiza el mapa compartido de humedades con el valor recibido
     * bajo la clave del sensor correspondiente.</p>
     */
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
