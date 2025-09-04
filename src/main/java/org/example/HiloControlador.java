package org.example;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class HiloControlador extends Thread {
    ConcurrentHashMap<String, Object> estado;
    private ConcurrentHashMap<String, Double> humedadades;
    private ConcurrentHashMap<String, Boolean> electrovalvulas;
    private double temperatura;
    private double radiacion;
    private boolean lluvia;

    public HiloControlador(ConcurrentHashMap<String, Object> estado) {
        this.estado = estado;
        this.temperatura = 0.0;
        this.radiacion = 0.0;
        this.lluvia = false;
        for (int i = 0; i < 5; i++) {
            this.humedadades.put(String.valueOf(i),0.0);
        }
        for (int i = 0; i < 7; i++) {
            this.electrovalvulas.put(String.valueOf(i),false);
        }
        this.estado.put("temperatura", 0.0);
        this.estado.put("radiacion", 0.0);
        this.estado.put("lluvia", false);
        this.estado.put("humedadArray", this.humedadades);
    }

    public void run() {
        while (true) {
            try {
                this.temperatura = (double) this.estado.get("temperatura");
                this.radiacion = (double) this.estado.get("radiacion");
                this.lluvia = (boolean) this.estado.get("lluvia");
                this.electrovalvulas = (ConcurrentHashMap<String, Boolean>) this.estado.get("electrovalvulas");
                this.humedadades = (ConcurrentHashMap<String, Double>) this.estado.get("humedadades");

                //Calcular INR


                //Activar electrovalvulas

                System.out.printf("  Temperatura : %.2f °C%n", this.temperatura);
                System.out.printf("  Radiación   : %.2f W/m²%n", this.radiacion);
                System.out.printf("  Lloviendo   : %s%n", this.lluvia ? "Sí" : "No");
                System.out.println("-----------------------------------------");

                double[] humedadArray = (double[]) this.estado.get("humedadArray");
                System.out.printf("  Humedad     : %s%%%n", Arrays.toString(humedadArray));

                System.out.println("-------------------------");
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
