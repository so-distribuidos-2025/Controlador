package org.example;

import interfaces.IServerRMI;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

/**
 * La clase {@code HiloControlador} representa el hilo principal de control
 * del sistema de riego inteligente.
 *
 * <p>Este hilo consulta periódicamente los datos de sensores almacenados
 * en un {@link ConcurrentHashMap}, calcula el índice de necesidad de riego (INR)
 * para cada parcela y, en base a dicho valor, determina la activación de los
 * temporizadores y las electroválvulas.</p>
 *
 * <p>El INR se calcula según la fórmula:</p>
 * <pre>
 * INR = W1 * (1 - H/100) + W2 * (T/T_MAX) + W3 * (R/R_MAX)
 * </pre>
 * donde:
 * <ul>
 *   <li><b>H</b>: humedad (%) de la parcela.</li>
 *   <li><b>T</b>: temperatura actual.</li>
 *   <li><b>R</b>: radiación solar actual.</li>
 *   <li><b>W1, W2, W3</b>: pesos definidos en las constantes.</li>
 *   <li><b>T_MAX</b> y <b>R_MAX</b>: valores máximos de referencia.</li>
 * </ul>
 *
 * <p>Si hay lluvia, el INR se anula a {@code 0.0}, inhibiendo el riego.</p>
 *
 * <p>En función del INR, se establecen diferentes tiempos de riego:</p>
 * <ul>
 *   <li>0.7 ≤ INR &lt; 0.8 → 5 minutos</li>
 *   <li>0.8 ≤ INR &lt; 0.9 → 7 minutos</li>
 *   <li>INR ≥ 0.9 → 10 minutos</li>
 * </ul>
 * Se abren y cierran las electrovavulas segun los temporizadores
 *
 * <p>El hilo imprime en consola el estado de cada parcela, la temperatura,
 * la radiación y si está lloviendo.</p>
 *
 * @author
 * @version 1.0
 */
public class HiloControlador extends Thread {
    /**
     * Estado global compartido entre todos los hilos.
     */
    ConcurrentHashMap<String, Object> estado;
    /**
     * Valores de humedad por parcela.
     */
    private ConcurrentHashMap<String, Double> humedades;
    /**
     * Servidores RMI de las valvulas
     */
    private ArrayList<IServerRMI> electrovalvulas = new ArrayList<>();
    /**
     * Temporizadores asignados a cada parcela.
     */
    private ConcurrentHashMap<String, Integer> temporizadores;
    /**
     * Índices de necesidad de riego (INR) por parcela.
     */
    private double[] inr = {0.0, 0.0, 0.0, 0.0, 0.0};
    /**
     * Temperatura actual.
     */
    private double temperatura;
    /**
     * Radiación solar actual.
     */
    private double radiacion;
    /**
     * Indica si está lloviendo.
     */
    private boolean lluvia;

    // Pesos y valores de referencia
    private static final double W1 = 0.5;
    private static final double W2 = 0.3;
    private static final double W3 = 0.2;
    private static final double T_MAX = 40.0;
    private static final double R_MAX = 1000.0;

    /**
     * Crea un nuevo hilo de control que inicializa los mapas de estado
     * (humedades, temporizadores, electroválvulas, temperatura, radiación, lluvia).
     * <p>
     * Tambien intentan conectarse a los servidores RMI de las valvulas
     *
     * @param estado el mapa compartido que contiene los datos globales del sistema.
     */
    public HiloControlador(ConcurrentHashMap<String, Object> estado) {
        this.estado = estado;
        this.temperatura = 0.0;
        this.radiacion = 0.0;
        this.lluvia = false;

        this.humedades = new ConcurrentHashMap<>();
        this.estado.put("humedades", humedades);
        for (int i = 0; i < 5; i++) {
            this.humedades.put(String.valueOf(i), 0.0);
        }

        this.temporizadores = new ConcurrentHashMap<>();
        this.estado.put("temporizadores", temporizadores);
        for (int i = 0; i < 5; i++) {
            this.temporizadores.put(String.valueOf(i), 0);
        }

        for (int i = 0; i < 7; i++) {
            try {
                //Cambiar esto segun la idea de Ana
                //Identificar las valvulas por puerto
                int puerto = 21000 + i;
                String direccionRMI = String.format("rmi://localhost:%d/ServerRMI", puerto);
                IServerRMI server = (IServerRMI) Naming.lookup(direccionRMI);
                this.electrovalvulas.add(server); //Revisar que esto no de problemas
                System.out.println("Conectado a la electrovalvula" + i);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                System.err.println("Fallo al conectar la electrovalvula " + i + ": " + e.getMessage());
                this.electrovalvulas.add(null);
            }
        }

        this.estado.put("temperatura", 0.0);
        this.estado.put("radiacion", 0.0);
        this.estado.put("lluvia", false);
        this.estado.put("humedadArray", this.humedades);
    }

    /**
     * Método principal del hilo.
     *
     * <p>En un bucle infinito, realiza las siguientes operaciones:</p>
     * <ul>
     *   <li>Actualiza los valores de sensores desde el mapa de estado.</li>
     *   <li>Calcula el INR para cada parcela.</li>
     *   <li>Si el INR supera ciertos umbrales, ajusta el temporizador
     *       de la parcela correspondiente.</li>
     *   <li>Imprime en consola el estado de cada parcela, junto con la
     *       temperatura, radiación y si está lloviendo.</li>
     * </ul>
     *
     * @throws RuntimeException si el hilo es interrumpido durante la espera.
     */
    @Override
    public void run() {
        while (true) {
            try {
                this.temperatura = (double) this.estado.get("temperatura");
                this.radiacion = (double) this.estado.get("radiacion");
                this.lluvia = (boolean) this.estado.get("lluvia");
                this.humedades = (ConcurrentHashMap<String, Double>) this.estado.get("humedades");
                this.temporizadores = (ConcurrentHashMap<String, Integer>) this.estado.get("temporizadores");

                //TODO Mover cada calculo de cada parcela a nuevo hilo

                // Calcular INR y activar electroválvulas para cada parcela
                for (int i = 0; i < 5; i++) {
                    String key = String.valueOf(i);
                    if (humedades.containsKey(key) && this.electrovalvulas.get(i) != null) {
                        double H = humedades.get(key); // humedad %
                        inr[i] = (W1 * (1 - H / 100.0))
                                + (W2 * (temperatura / T_MAX))
                                + (W3 * (radiacion / R_MAX));
                        if (lluvia) {
                            inr[i] = 0.0; // lluvia inhibe
                        } //TODO cerrar electrovalvulas cuando llueve

                        // Activar electroválvulas según INR
                        if (inr[i] > 0.7 && inr[i] < 0.8) {
                            if (this.temporizadores.get(key) == 0) {
                                this.temporizadores.put(key, 300); // 5 min
                                this.electrovalvulas.get(i).abrirValvula();
                            }
                        }
                        if (inr[i] > 0.8 && inr[i] < 0.9) {
                            if (this.temporizadores.get(key) == 0) {
                                this.temporizadores.put(key, 420); // 7 min
                                this.electrovalvulas.get(i).abrirValvula();
                            }
                        }
                        if (inr[i] > 0.9) {
                            if (this.temporizadores.get(key) == 0) {
                                this.temporizadores.put(key, 600); // 10 min
                                this.electrovalvulas.get(i).abrirValvula();
                            }
                        }

                        boolean estaAbierta = this.electrovalvulas.get(i).estaAbierta();
                        if (this.temporizadores.get(key) == 0 && estaAbierta) {
                            //TODO revisar si se puede hacer mejor
                            this.electrovalvulas.get(i).cerrarValvula();

                        }
                        System.out.printf("Parcela %d -> Humedad = %.2f %% | INR = %.3f |", i, H, inr[i]);
                        System.out.printf(" Electrovalvula %d: %s%n", i, estaAbierta ? "Abierta" : "Cerrada");
                        System.out.printf(" Temporizador: %s%n", (this.temporizadores.get(key) > 0) ? "Activo" : "Apagado");
                    } else {
                        System.out.printf("Parcela %d no válida%n", i);
                    }
                }

                // Mostrar estado general
                System.out.printf("  Temperatura : %.2f °C%n", this.temperatura);
                System.out.printf("  Radiación   : %.2f W/m²%n", this.radiacion);
                System.out.printf("  Lloviendo   : %s%n", this.lluvia ? "Sí" : "No");
                System.out.println("-----------------------------------------");

                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
