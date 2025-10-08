package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Hilo del servidor TCP, espera las conexiones entrantes y genera los hilos para las conexiones
 * <p>Realiza los siguientes pasos:</p>
 * <ul>
 *   <li>Crea un {@link ConcurrentHashMap} para almacenar el estado global del sistema.</li>
 *   <li>Abre un {@link ServerSocket} en el puerto {@code 20000} para escuchar conexiones de sensores.</li>
 *   <li>Inicia un hilo de tipo {@link HiloControlador} encargado de procesar y mostrar
 *       la información del estado.</li>
 *   <li>En un bucle infinito, acepta nuevas conexiones de clientes y crea un
 *       {@link HiloConexionTCP} por cada uno de ellos para gestionar la comunicación.</li>
 * </ul>
 *
 */
public class ServerTCP extends Thread {
    @Override
    public void run() {
        // Crear el ConcurrentHashMap que tiene todos los datos del estado
        ConcurrentHashMap<String, Object> estado = new ConcurrentHashMap<>();

        try {
            ServerSocket server = new ServerSocket(20000);
            HiloControlador hiloControlador = new HiloControlador(estado);
            hiloControlador.run();
            while (true) {
                Socket s = server.accept();
                HiloConexionTCP handler = new HiloConexionTCP(s, estado, hiloControlador);
                handler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
