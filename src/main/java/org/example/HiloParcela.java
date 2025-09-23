package org.example;

import interfaces.IServerRMI;

import java.util.concurrent.ConcurrentHashMap;

public class HiloParcela extends Thread{
    private HiloReceptorHumedad hiloHumedad;
    private HiloReceptorTiempo hiloTiempo;
    private IServerRMI electrovalvula;
    private int puerto;
    private int id;
    private boolean lluvia;
    private double radiacion;
    private double temperatura;

    private int tiempo;
    private double humedad;
    private double inr;

    ConcurrentHashMap estado;

    public HiloParcela(HiloReceptorHumedad hiloHumedad, HiloReceptorTiempo hiloTiempo, IServerRMI electrovalvula, int puerto, int id, int tiempo, double humedad, ConcurrentHashMap estado) {
        this.hiloHumedad = hiloHumedad;
        this.hiloTiempo = hiloTiempo;
        this.electrovalvula = electrovalvula;
        this.puerto = puerto;
        this.id = id;
        this.tiempo = 0;
        this.humedad = 0;
        this.estado = estado;
        this.lluvia = false;
        this.radiacion = 0;
        this.temperatura = 0;
        this.inr = 0;
    }

    public void setHiloHumedad(HiloReceptorHumedad hiloHumedad) {
        this.hiloHumedad = hiloHumedad;
    }

    public void setHiloTiempo(HiloReceptorTiempo hiloTiempo) {
        this.hiloTiempo = hiloTiempo;
    }

    @Override
    public void run() {
        while (true){
            try {
                this.radiacion = (Double) this.estado.get("radiacion");
                this.lluvia = (Boolean) this.estado.get("lluvia");
                this.temperatura = (Double) this.estado.get("temperatura");
                if (this.hiloHumedad != null && this.hiloTiempo != null){
                    if (!lluvia) {
                        this.inr = Inr.calcularInr(humedad, radiacion, temperatura);
                        if (inr > 0.7 && inr < 0.8) {
                            hiloTiempo.
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

                    }else{
                        inr = 0;
                    }
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
