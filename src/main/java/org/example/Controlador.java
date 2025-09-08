package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La clase {@code Controlador} implementa el servidor principal del sistema de invernadero.
 *
 * <p>Se encarga de recibir conexiones entrantes de diferentes sensores (por ejemplo,
 * {@code SensorLluvia}) a través del puerto {@code 20000}, y gestiona su comunicación
 * mediante la creación de hilos dedicados de tipo {@link HiloConexion}.</p>
 *
 * <p>Además, mantiene un {@link ConcurrentHashMap} compartido que almacena el estado
 * general de los sensores conectados. Dicho estado puede ser consultado y procesado
 * por un hilo adicional de tipo {@link HiloControlador} que imprime o gestiona la
 * información del sistema.</p>
 *
 * <p>Tras ejecutarlo, el servidor quedará a la espera de conexiones
 * en el puerto {@code 20000}.</p>
 *
 * @author  
 * @version 1.0
 */
public class Controlador {
    /**
     * Punto de entrada principal del programa.
     *
     * <p>Realiza los siguientes pasos:</p>
     * <ul>
     *   <li>Crea un {@link ConcurrentHashMap} para almacenar el estado global del sistema.</li>
     *   <li>Abre un {@link ServerSocket} en el puerto {@code 20000} para escuchar conexiones de sensores.</li>
     *   <li>Inicia un hilo de tipo {@link HiloControlador} encargado de procesar y mostrar
     *       la información del estado.</li>
     *   <li>En un bucle infinito, acepta nuevas conexiones de clientes y crea un
     *       {@link HiloConexion} por cada uno de ellos para gestionar la comunicación.</li>
     * </ul>
     *
     * @param args argumentos de la línea de comandos (no se utilizan en este programa).
     * @throws RuntimeException si ocurre un error al abrir el servidor o aceptar conexiones.
     */
    public static void main(String[] args) {
        // Crear el ConcurrentHashMap que tiene todos los datos del estado
        ConcurrentHashMap<String, Object> estado = new ConcurrentHashMap<>();

        try {
            ServerSocket server = new ServerSocket(20000);
            HiloControlador controladorPrint = new HiloControlador(estado);
            controladorPrint.start();
            while (true) {
                Socket s = server.accept();
                HiloConexion handler = new HiloConexion(s, estado);
                handler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
