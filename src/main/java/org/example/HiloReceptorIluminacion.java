package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hilo encargado de recibir datos de un sensor de iluminación.
 *
 * <p>Este hilo se conecta a un cliente que envía valores de iluminación
 * en formato numérico (double). Los datos recibidos se almacenan en el
 * {@link ConcurrentHashMap} de estado compartido bajo la clave {@code "radiacion"}.</p>
 */
public class HiloReceptorIluminacion extends Thread {

    private Socket clienteIluminacion;
    private final BufferedReader br;
    private double iluminacion;
    private ConcurrentHashMap<String, Object> estado;

    /**
     * Devuelve el último valor de iluminación recibido.
     *
     * @return valor de iluminación en W/m²
     */
    public double getIlumicacion() {
        return iluminacion;
    }

    /**
     * Establece manualmente el valor de iluminación.
     *
     * @param iluminacion nuevo valor de iluminación
     */
    public void setIlumicacion(double iluminacion) {
        this.iluminacion = iluminacion;
    }

    /**
     * Constructor de la clase.
     *
     * @param clienteIluminacion socket del cliente que envía los datos de iluminación
     * @param estado estructura compartida con el estado global del sistema
     */
    public HiloReceptorIluminacion(Socket clienteIluminacion, ConcurrentHashMap<String, Object> estado) {
        this.clienteIluminacion = clienteIluminacion;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteIluminacion.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bucle principal del hilo.
     *
     * <p>Lee continuamente datos del socket, los convierte a {@code double}
     * y actualiza el mapa compartido de estado bajo la clave {@code "radiacion"}.</p>
     */
    public void run() {
        while (true) {
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

