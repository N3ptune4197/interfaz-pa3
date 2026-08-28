package capa_logica;

import java.awt.Rectangle;

public class Tubo {
    private double X;
    private int anchoTubo;
    private int altoAgujero;
    private int posicionAgujero;
    private double velocidad;
    private boolean superado;

    public Tubo() {
        this.X = 0;
        this.anchoTubo = 60;
        this.altoAgujero = 0;
        this.posicionAgujero = 0;
        this.velocidad = 0;
        this.superado = false;
    }

    public Tubo(double X, int altoPantalla, int altoAgujero, double velocidad) {
        this.X = X;
        this.anchoTubo = 60;
        this.altoAgujero = altoAgujero;
        this.velocidad = velocidad;
        this.superado = false;

        // Genera la posición del hueco de forma aleatoria, con margen arriba y abajo
        // para que nunca quede pegado al borde de la pantalla
        int margen = 80;
        int rango = altoPantalla - altoAgujero - (margen * 2);
        if (rango < 0) rango = 0;
        this.posicionAgujero = margen + (int) (Math.random() * rango);
    }

    // Getters y setters
    public double getX() {
        return X;
    }

    public void setX(double X) {
        this.X = X;
    }

    public int getAnchoTubo() {
        return anchoTubo;
    }

    public void setAnchoTubo(int anchoTubo) {
        this.anchoTubo = anchoTubo;
    }

    public int getAltoAgujero() {
        return altoAgujero;
    }

    public void setAltoAgujero(int altoAgujero) {
        this.altoAgujero = altoAgujero;
    }

    public int getPosicionAgujero() {
        return posicionAgujero;
    }

    public void setPosicionAgujero(int posicionAgujero) {
        this.posicionAgujero = posicionAgujero;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public boolean isSuperado() {
        return superado;
    }

    public void setSuperado(boolean superado) {
        this.superado = superado;
    }

    public void actualizar() {
        X -= velocidad;
    }

    public boolean estaFueraDePantalla() {
        return X + anchoTubo < 0;
    }

    public Rectangle getRectanguloSuperior() {
        return new Rectangle((int)X, 0, anchoTubo, posicionAgujero);
    }

    public Rectangle getRectanguloInferior(int altoPantalla) {
        int yInferior = posicionAgujero + altoAgujero;
        return new Rectangle((int)X, yInferior, anchoTubo, altoPantalla - yInferior);
    }

    public void marcarSuperado() {
        this.superado = true;
    }
}