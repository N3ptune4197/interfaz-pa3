/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package capa_logica;

import java.awt.Rectangle;

/**
 *
 * @author JIMMYSIN
 */
public class Pajaro {
    private double X;
    private double Y;
    private int ancho;
    private int alto;
    private double velocidadY;
    private double gravedad;
    private double fuerzaSalto;
    private boolean vivo; 
    
    public Pajaro(){
        this.X = 100;
        this.Y = 0;
        this.ancho = 40;
        this.alto = 30;
        this.velocidadY = 0;
        this.gravedad = 0.5;
        this.fuerzaSalto = -8;
        this.vivo = true;
    }

    public Pajaro(double X, double Y, double gravedad, double fuerzaSalto) {
        this.X = X;
        this.Y = Y;
        this.ancho = 40;
        this.alto = 30;
        this.velocidadY = 0;
        this.gravedad = gravedad;
        this.fuerzaSalto = fuerzaSalto;
        this.vivo = true;
    }

    public double getX() {
        return X;
    }

    public void setX(double X) {
        this.X = X;
    }

    public double getY() {
        return Y;
    }

    public void setY(double Y) {
        this.Y = Y;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public double getVelocidadY() {
        return velocidadY;
    }

    public void setVelocidadY(double velocidadY) {
        this.velocidadY = velocidadY;
    }

    public double getGravedad() {
        return gravedad;
    }

    public void setGravedad(double gravedad) {
        this.gravedad = gravedad;
    }

    public double getFuerzaSalto() {
        return fuerzaSalto;
    }

    public void setFuerzaSalto(double fuerzaSalto) {
        this.fuerzaSalto = fuerzaSalto;
    }

    public boolean isVivo() {
        return vivo;
    }

    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }
    
    public void actualizar(){
        velocidadY += gravedad;
        Y += velocidadY;
    }
    
    public void saltare(){
        velocidadY = fuerzaSalto;
    }
    
    public boolean chocaConLimites(int altoPantalla){
        return Y < 0 || + alto > altoPantalla;
    }
    
    public Rectangle getRectanngulo(){
        return new Rectangle((int)X, (int)Y, ancho, alto);
    }
}
