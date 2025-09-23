package org.example;

import interfaces.IServerRMI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Representa el hilo principal de control del sistema de invernadero.
 *
 * <p>Esta clase actúa como el coordinador central que gestiona el estado
 * general del entorno (temperatura, radiación, lluvia) y supervisa
 * las distintas parcelas del invernadero. Cada parcela es gestionada por
 * su propio hilo de tipo {@link HiloParcela}. Se encarga de obtener los datos
 * necesarios para cada impresion de estado del sistema</p>
 *
 *   <li>Inicializar y lanzar los hilos para las 5 parcelas del sistema.</li>
 *   <li>Asignar los sensores de humedad y temporizadores que se conectan
 *       al sistema a su {@link HiloParcela} correspondiente.</li>
 *   <li>En un bucle continuo, leer el estado global del entorno desde un
 *       mapa compartido ({@link ConcurrentHashMap}).</li>
 *   <li>Mostrar en consola un informe periódico del estado de cada parcela
 *       (humedad, INR, estado de la electroválvula y temporizador) y del
 *       estado general del invernadero.</li>
 *
 * @author Brunardo19
 */
public class HiloControlador extends Thread {

    /** Mapa compartido que almacena el estado global del sistema. */
    ConcurrentHashMap<String, Object> estado;

    /** Lista que contiene los hilos de gestión para cada una de las parcelas. */
    private List<HiloParcela> listaParcelas = new ArrayList<>();

    /** Último valor de temperatura registrado. */
    private double temperatura;
    /** Último valor de radiación solar registrado. */
    private double radiacion;
    /** Estado actual de la lluvia (true si está lloviendo). */
    private boolean lluvia;

    /**
     * Construye e inicializa el hilo de control principal.
     *
     * <p>En el constructor se inicializan los valores por defecto para el estado
     * del entorno, se crean y arrancan los cinco hilos {@link HiloParcela}
     * que gestionarán cada una de las parcelas del invernadero.</p>
     *
     * @param estado el mapa {@link ConcurrentHashMap} compartido que contiene el
     *               estado global del sistema.
     */
    public HiloControlador(ConcurrentHashMap<String, Object> estado) {
        this.estado = estado;
        this.temperatura = 0.0;
        this.radiacion = 0.0;
        this.lluvia = false;

        this.estado.put("temperatura", 0.0);
        this.estado.put("radiacion", 0.0);
        this.estado.put("lluvia", false);

        for (int i = 0; i < 5; i++) {
            listaParcelas.add(new HiloParcela(i, estado));
            listaParcelas.get(i).start();
        }
    }

    /**
     * Asocia un hilo receptor de datos de humedad con una parcela específica.
     *
     * @param hr la instancia de {@link HiloReceptorHumedad} que se va a asignar.
     * @param id el identificador de la parcela (0-4) a la que se asignará el sensor.
     */
    public void setSensorHumedad(HiloReceptorHumedad hr, int id){
        listaParcelas.get(id).setHiloHumedad(hr);
    }

    /**
     * Asocia un hilo receptor de datos de un temporizador con una parcela específica.
     *
     * @param hr la instancia de {@link HiloReceptorTiempo} que se va a asignar.
     * @param id el identificador de la parcela (0-4) a la que se asignará el temporizador.
     * @throws IOException si ocurre un error al configurar el canal de comunicación con el temporizador.
     */
    public void setSensorTiempo(HiloReceptorTiempo hr, int id) throws IOException {
        listaParcelas.get(id).setHiloTiempo(hr);
    }

    /**
     * Bucle principal de ejecución del hilo controlador.
     *
     * <p>Este método se ejecuta de forma continua y realiza las siguientes tareas
     * en cada iteración:</p>
     * <ol>
     *   <li>Actualiza las variables locales de temperatura, radiación y lluvia
     *       leyendo los valores del mapa de estado compartido.</li>
     *   <li>Recorre la lista de parcelas y muestra en consola el estado detallado de cada una.</li>
     *   <li>Muestra un resumen del estado ambiental general del invernadero.</li>
     *   <li>Espera 1 segundo antes de la siguiente actualización.</li>
     * </ol>
     */
    @Override
    public void run() {
        while (true) {
            try {
                //Obtener estado
                this.temperatura = (double) this.estado.get("temperatura");
                this.radiacion = (double) this.estado.get("radiacion");
                this.lluvia = (boolean) this.estado.get("lluvia");

                //Mostrar estado por parcelas
                for (int i = 0; i < 5; i++) {
                    double humedad = listaParcelas.get(i).getHumedad();
                    double inr = listaParcelas.get(i).getInr();
                    boolean estaAbierta = listaParcelas.get(i).getElectrovalvula().estaAbierta();
                    boolean temporizadorActivo = listaParcelas.get(i).getEstadoTemporizador() == 1;
                    System.out.printf("Parcela %d -> Humedad = %.2f %% | INR = %.3f |", i, humedad, inr);
                    System.out.printf(" Electrovalvula %d: %s%n", i, estaAbierta ? "Abierta" : "Cerrada");
                    System.out.printf(" Temporizador: %s%n", (temporizadorActivo) ? "Activo" : "Apagado");
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            // Mostrar estado general
            System.out.printf("  Temperatura : %.2f °C%n", this.temperatura);
            System.out.printf("  Radiación   : %.2f W/m²%n", this.radiacion);
            System.out.printf("  Lloviendo   : %s%n", this.lluvia ? "Sí" : "No");
            System.out.println("-----------------------------------------");

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}