/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package capa_logica;

/**
 *
 * @author JIMMYSIN
 */
public class Puntuacion {
    private int puntosActuales;
    private int mejorPuntuacion;

    public Puntuacion() {
        this.puntosActuales = 0;
        this.mejorPuntuacion = 0;
    }
    
    public Puntuacion(int puntosActuales, int mejorPuntuacion) {
        this.puntosActuales = 0;
        this.mejorPuntuacion = mejorPuntuacion;
    }

    public int getPuntosActuales() {
        return puntosActuales;
    }

    public void setPuntosActuales(int puntosActuales) {
        this.puntosActuales = puntosActuales;
    }

    public int getMejorPuntuacion() {
        return mejorPuntuacion;
    }

    public void setMejorPuntuacion(int mejorPuntuacion) {
        this.mejorPuntuacion = mejorPuntuacion;
    }
    
    public void sumarPunto(){
        puntosActuales++;
        if (puntosActuales > mejorPuntuacion) {
            mejorPuntuacion = puntosActuales;
        }
    }
    
    public void reiniciar(){
        puntosActuales = 0;
    }
}
