package org.example;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class HiloControlador extends Thread {
    ConcurrentHashMap<String, Object> estado;
    private ConcurrentHashMap<String, Double> humedades;
    private ConcurrentHashMap<String, Boolean> electrovalvulas;
    private ConcurrentHashMap<String, Integer> temporizadores;
    private double[] inr = {0.0, 0.0, 0.0, 0.0, 0.0};
    private double temperatura;
    private double radiacion;
    private boolean lluvia;
    private static final double W1 = 0.5;
    private static final double W2 = 0.3;
    private static final double W3 = 0.2;
    private static final double T_MAX = 40.0;
    private static final double R_MAX = 1000.0;

    public HiloControlador(ConcurrentHashMap<String, Object> estado) {
        this.estado = estado;
        this.temperatura = 0.0;
        this.radiacion = 0.0;
        this.lluvia = false;
        this.humedades = new ConcurrentHashMap<String, Double>();
        this.estado.put("humedades", humedades);
        for (int i = 0; i < 5; i++) {
            this.humedades.put(String.valueOf(i), 0.0);
        }
        this.temporizadores = new ConcurrentHashMap<String, Integer>();
        this.estado.put("temporizadores", temporizadores);
        for (int i = 0; i < 5; i++) {
            this.temporizadores.put(String.valueOf(i), 0);
        }
        this.electrovalvulas = new ConcurrentHashMap<String, Boolean>();
        this.estado.put("electrovalvulas", humedades);
        for (int i = 0; i < 7; i++) {
            this.electrovalvulas.put(String.valueOf(i), false);
        }
        this.estado.put("temperatura", 0.0);
        this.estado.put("radiacion", 0.0);
        this.estado.put("lluvia", false);
        this.estado.put("humedadArray", this.humedades);
    }

    public void run() {
        while (true) {
            try {
                this.temperatura = (double) this.estado.get("temperatura");
                this.radiacion = (double) this.estado.get("radiacion");
                this.lluvia = (boolean) this.estado.get("lluvia");
                this.electrovalvulas = (ConcurrentHashMap<String, Boolean>) this.estado.get("electrovalvulas");
                this.humedades = (ConcurrentHashMap<String, Double>) this.estado.get("humedades");
                this.temporizadores = (ConcurrentHashMap<String, Integer>) this.estado.get("temporizadores");

                //Calcular INR
                for (int i = 0; i < 5; i++) {
                    String key = String.valueOf(i);
                    if (humedades.containsKey(key)) {
                        double H = humedades.get(key); // humedad %
                        inr[i] = (W1 * (1 - H / 100.0))
                                + (W2 * (temperatura / T_MAX))
                                + (W3 * (radiacion / R_MAX));
                        if (lluvia) {
                            inr[i] = 0.0; // lluvia inhibe
                        }
                        System.out.printf("Parcela %d -> Humedad = %.2f %% | INR = %.3f%n", i, H, inr[i]);
                    } else {
                        System.out.printf("Humedad parcela %d no válida%n", i);
                    }
                }


                //Activar electrovalvulas

                for (int i = 0; i < 5; i++) {
                    String key = String.valueOf(i);

                    if (inr[i] > 0.7 && inr[i] < 0.8) {
                        //temporizador seteado en 5min.
                        if (this.temporizadores.get(key) != 0) {
                            this.temporizadores.put(key, 300);
                        }

                        //Activo electroválvula
                    }
                    if (inr[i] > 0.8 && inr[i] < 0.9) {
                        //temporizador seteado en 7min.
                        if (this.temporizadores.get(key) != 0) {
                            this.temporizadores.put(key, 420);
                        }
                        //Activo electroválvula
                    }
                    if (inr[i] > 0.9) {
                        //temporizador seteado en 10min.
                        if (this.temporizadores.get(key) != 0) {
                            this.temporizadores.put(key, 600);
                        }

                    }
                    //Activo electroválvula
                }

                System.out.printf("  Temperatura : %.2f °C%n", this.temperatura);
                System.out.printf("  Radiación   : %.2f W/m²%n", this.radiacion);
                System.out.printf("  Lloviendo   : %s%n", this.lluvia ? "Sí" : "No");


                System.out.println("-----------------------------------------");
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
