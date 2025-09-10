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
 * mediante la creación de hilos dedicados de tipo {@link HiloConexionTCP}.</p>
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
     */
    public static void main(String[] args) {
        ServerTCP serverTCP = new ServerTCP();
        serverTCP.start();
    }
}
