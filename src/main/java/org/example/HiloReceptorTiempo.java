package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
public class HiloReceptorTiempo extends Thread {

    private Socket clienteTiempo;
    private final BufferedReader br;
    private int totalSegundos;
    private RuntimeException runtimeException;
    private ConcurrentHashMap<String, Object> estado;
    private ConcurrentHashMap<String, Integer> temporizadores;
    private String key;
    private PrintWriter pw;

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
     * @param estado              estructura compartida con el estado global del sistema
     */
    public HiloReceptorTiempo(Socket clienteTemporizador, ConcurrentHashMap<String, Object> estado, int id) {
        this.clienteTiempo = clienteTemporizador;
        this.estado = estado;
        this.temporizadores = (ConcurrentHashMap<String, Integer>) this.estado.get("temporizadores");
        this.key = String.valueOf(id);
        try {
            this.br = new BufferedReader(new InputStreamReader(clienteTemporizador.getInputStream()));
            this.pw = new PrintWriter(clienteTemporizador.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void recibirTemporizacion() throws IOException, InterruptedException {
        while (this.totalSegundos != 0) {
            Thread.sleep(1000);
            String entrada = br.readLine();
            totalSegundos = Integer.parseInt(entrada);
            this.temporizadores.put(key, totalSegundos);
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
                totalSegundos = this.temporizadores.getOrDefault(key, 0);
                Thread.sleep(500);
                if (totalSegundos != 0) {
                    pw.println(totalSegundos);
                    recibirTemporizacion();
                }
            } catch (IOException e) {
                throw runtimeException;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
