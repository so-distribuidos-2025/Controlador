package org.example;

/**
 * Punto de entrada principal del programa.
 */
public class Controlador {

    public static void main(String[] args) {
        ServerTCP serverTCP = new ServerTCP();
        serverTCP.start();
    }
}
