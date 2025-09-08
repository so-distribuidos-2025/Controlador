package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hilo encargado de gestionar la comunicación con un temporizador.
 *
 * <p>Este hilo recibe información desde un cliente de temporizador y
 * actualiza el estado global en el {@link ConcurrentHashMap}. Cada
 * temporizador se identifica por un {@code id} y controla un contador
 * de segundos que puede reiniciarse o detenerse según la entrada.</p>
 */
public class HiloReceptorTiempo implements Runnable {

    private Socket clienteTiempo;
    private final BufferedReader br;
    private int totalSegundos;
    private RuntimeException runtimeException;
    private ConcurrentHashMap<String, Object> estado;
    private ConcurrentHashMap<String, Integer> temporizadores;
    private int id;

    /**
     * Devuelve el valor actual del temporizador en segundos.
     *
     * @return segundos restantes o acumulados
     */
    public int getTotalSegundos() {
        return totalSegundos;
    }

    /**
     * Establece manualmente el valor del temporizador.
     *
     * @param totalSegundos nuevo valor en segundos
     */
    public void setTotalSegundos(int totalSegundos) {
        this.totalSegundos = totalSegundos;
    }

    /**
     * Constructor de la clase.
     *
     * @param clienteTemporizador socket del cliente que envía los datos del temporizador
     * @param estado estructura compartida con el estado global del sistema
     */
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

    /**
     * Bucle principal del hilo.
     *
     * <p>Verifica periódicamente el estado del temporizador asociado al {@code id}.
     * Si el valor no es cero, lee nuevos datos desde el socket y actualiza el
     * contador de segundos.</p>
     */
    @Override
    public void run() {
        while (true) {
            try {
                totalSegundos = this.temporizadores.getOrDefault(String.valueOf(this.id), 0);
                if (totalSegundos != 0) {
                    String entrada = br.readLine();
                    totalSegundos = Integer.parseInt(entrada);
                    System.out.printf("temporizador %d parado%n", id);
                }
            } catch (IOException e) {
                throw runtimeException;
            }
        }
    }
}
