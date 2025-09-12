package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La clase {@code HiloConexionTCP} representa el hilo encargado de gestionar
 * la conexión inicial de un dispositivo con el servidor {@link Controlador}.
 *
 * <p>Al establecerse una conexión, el hilo identifica el tipo de dispositivo
 * (por ejemplo: {@code humedad}, {@code temperatura}, {@code lluvia}, 
 * {@code temporizador}, {@code iluminacion}, {@code electrovalvula}) y crea 
 * un hilo receptor específico para manejar la comunicación con dicho dispositivo.</p>
 *
 * <p>El estado compartido del sistema se mantiene en un 
 * {@link ConcurrentHashMap}, donde los distintos hilos receptores actualizan
 * la información correspondiente.</p>
 *
 * <p>En caso de recibir un tipo no reconocido, se muestra un mensaje por consola.</p>
 *
 * @author  
 * @version 1.0
 */
public class HiloConexionTCP extends Thread {
    /** Socket asociado a la conexión del dispositivo. */
    private Socket s;
    /** Tipo de dispositivo conectado (ej: humedad, temperatura, lluvia). */
    String tipoDispositivo = "";
    /** Estado global compartido entre todos los hilos. */
    ConcurrentHashMap<String, Object> estado;

    /**
     * Crea un nuevo hilo de conexión para gestionar un dispositivo.
     *
     * @param s      el {@link Socket} de comunicación con el dispositivo.
     * @param estado el mapa compartido con el estado global del sistema.
     */
    public HiloConexionTCP(Socket s, ConcurrentHashMap<String, Object> estado) {
        this.s = s;
        this.estado = estado;
    }

    /**
     * Método principal del hilo.
     *
     * <p>Realiza los siguientes pasos:</p>
     * <ul>
     *   <li>Lee el tipo de dispositivo desde el flujo de entrada.</li>
     *   <li>Según el tipo, inicializa el hilo receptor correspondiente.</li>
     *   <li>En caso de que el dispositivo no sea reconocido, imprime un mensaje de error.</li>
     * </ul>
     *
     * @throws RuntimeException si ocurre un error de entrada/salida durante la conexión.
     */
    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            tipoDispositivo = br.readLine();
            int id;
            switch (tipoDispositivo) {
                case "humedad":
                    id = Integer.parseInt(br.readLine()); // Leer id
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
                    id = Integer.parseInt(br.readLine()); // Leer id
                    System.out.printf("---Conectado temporizador %d---\n", id);
                    HiloReceptorTiempo receptorTiempo = new HiloReceptorTiempo(s, estado, id);
                    receptorTiempo.run(); // Nota: se ejecuta en el mismo hilo
                    break;
                case "iluminacion":
                    System.out.println("---Conectado sensor iluminacion---");
                    HiloReceptorIluminacion receptorIluminacion = new HiloReceptorIluminacion(s, estado);
                    receptorIluminacion.run(); // Nota: se ejecuta en el mismo hilo
                    break;
                case "electrovalvula":
                    id = Integer.parseInt(br.readLine()); // Leer id
                    System.out.printf("---Conectado electrovalvula %d---\n", id);
                    break;
                default:
                    System.out.println("Disposivo no reconocido");
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
