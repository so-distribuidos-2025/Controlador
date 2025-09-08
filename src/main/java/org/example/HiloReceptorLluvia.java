package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hilo encargado de recibir datos del sensor de lluvia.
 *
 * <p>Este hilo se conecta a un cliente que envía datos binarios (0 o 1),
 * indicando si está lloviendo o no. La información se almacena en el
 * {@link ConcurrentHashMap} de estado compartido bajo la clave {@code "lluvia"}.</p>
 */
public class HiloReceptorLluvia extends Thread {

    private Socket clientelluvia;
    private final BufferedReader br;
    private boolean lluvia;
    private ConcurrentHashMap<String, Object> estado;

    /**
     * Devuelve el último estado de lluvia recibido.
     *
     * @return {@code true} si llueve, {@code false} en caso contrario
     */
    public boolean getlluvia() {
        return lluvia;
    }

    /**
     * Establece manualmente el valor de lluvia.
     *
     * @param lluvia nuevo estado de lluvia
     */
    public void setlluvia(boolean lluvia) {
        this.lluvia = lluvia;
    }

    /**
     * Constructor de la clase.
     *
     * @param clientelluvia socket del cliente que envía los datos de lluvia
     * @param estado estructura compartida con el estado global del sistema
     */
    public HiloReceptorLluvia(Socket clientelluvia, ConcurrentHashMap<String, Object> estado) {
        this.clientelluvia = clientelluvia;
        this.estado = estado;
        try {
            this.br = new BufferedReader(new InputStreamReader(clientelluvia.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bucle principal del hilo.
     *
     * <p>Lee continuamente datos del socket (0 o 1), los convierte a
     * {@code boolean} y actualiza el mapa compartido de estado bajo la
     * clave {@code "lluvia"}.</p>
     */
    public void run() {
        while (true) {
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
