package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hilo encargado de recibir datos del sensor de temperatura.
 *
 * <p>Este hilo se conecta a un cliente que envía valores de temperatura
 * en formato numérico (double). Los datos recibidos se almacenan en el
 * {@link ConcurrentHashMap} de estado compartido bajo la clave {@code "temperatura"}.</p>
 */
public class HiloReceptorTemperatura extends Thread {

    private Socket clienteTemperatura;
    private final BufferedReader br;
    private double temperatura;
    private ConcurrentHashMap<String, Object> estado;

    /**
     * Devuelve el último valor de temperatura recibido.
     *
     * @return valor de temperatura en grados Celsius
     */
    public double getTemperatura() {
        return temperatura;
    }

    /**
     * Establece manualmente el valor de la temperatura.
     *
     * @param temperatura nuevo valor de temperatura en grados Celsius
     */
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    /**
     * Constructor de la clase.
     *
     * @param clienteTemperatura socket del cliente que envía los datos de temperatura
     * @param estado estructura compartida con el estado global del sistema
     */
    public HiloReceptorTemperatura(Socket clienteTemperatura, ConcurrentHashMap<String, Object> estado) {
        this.clienteTemperatura = clienteTemperatura;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteTemperatura.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bucle principal del hilo.
     *
     * <p>Lee continuamente datos del socket, los convierte a {@code double}
     * y actualiza el mapa compartido de estado bajo la clave {@code "temperatura"}.</p>
     */
    public void run() {
        while (true) {
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
