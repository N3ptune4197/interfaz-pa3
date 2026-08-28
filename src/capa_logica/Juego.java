/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package capa_logica;






import capa_datos.PuntuacionDAO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Diego Estrada
 */
public class Juego {
    
    public enum Estado {
        MENU,JUGANDO,GAME_OVER
    }

    private Estado estado;

    // Objetos del juego

    private Pajaro pajaro;
    private List<Tubo> tubos;
    private Puntuacion puntuacion;
    private PuntuacionDAO puntuacionDAO;
    
    private double velocidadBase;
    private int ultimoPuntoIncremento = 0; 

    // Configuracion de pantalla
    
    private int anchoPantalla;
    private int altoPantalla;

    // Configuracion de los tubos

    private int altoAgujero;
    private double velocidadTubos;
    private int distanciaEntreTubos;

    // Control de generacion

    private int contadorTubos;

    private Random random;

    public Juego(int anchoPantalla, int altoPantalla) {

        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;

        this.estado = Estado.MENU;

        this.tubos = new ArrayList<>();
        this.puntuacionDAO = new PuntuacionDAO();
        this.random = new Random();

        // Configuración inicial
        this.altoAgujero = 180;
        this.velocidadTubos = 3.0;
        this.distanciaEntreTubos = 320;
        
        this.velocidadBase = 3.0;
        this.velocidadTubos = velocidadBase;

        this.contadorTubos = 0;

        // Crear puntuación y cargar récord
        this.puntuacion = new Puntuacion();

        int mejorPuntuacion = puntuacionDAO.obtenerMejorPuntuacion();
        this.puntuacion.setMejorPuntuacion(mejorPuntuacion);

        // Crear pájaro
        crearPajaro();
    }

    /**
     * Crea o reinicia el pájaro.
     */
    private void crearPajaro() {

        double posicionX = anchoPantalla * 0.25;
        double posicionY = altoPantalla * 0.45;

        pajaro = new Pajaro(
                posicionX,
                posicionY,
                0.5,
                -8
        );
    }

    // =========================================================
    // INICIO DEL JUEGO
    // =========================================================

    /**
     * Inicia una nueva partida.
     */
    public void iniciarJuego() {

        crearPajaro();

        tubos.clear();

        puntuacion.reiniciar();

        contadorTubos = 0;
        velocidadTubos = velocidadBase;   // RESTAURAMOS
        ultimoPuntoIncremento = 0;        // Reiniciamos bandera
        estado = Estado.JUGANDO;

        // Crear el primer tubo
        generarTubo();
    }

    /**
     * Reinicia la partida después de perder.
     */
    public void reiniciarJuego() {
        iniciarJuego();
    }

    // =========================================================
    // ACTUALIZACIÓN DEL JUEGO
    // =========================================================

    /**
     * Actualiza toda la lógica del juego.
     *
     * Este método debe ser llamado periódicamente desde
     * el Timer utilizado por PanelJuego.
     */
    public void actualizar() {

        if (estado != Estado.JUGANDO) {
            return;
        }

        // Actualizar pájaro
        pajaro.actualizar();

        // Actualizar tubos
        actualizarTubos();

        // Generar nuevos tubos
        controlarGeneracionTubos();
        
        // comprobar puntuacon
        comprobarPuntuacion();
        
        int pts = puntuacion.getPuntosActuales();
        if (pts > 0 && pts % 10 == 0 && pts != ultimoPuntoIncremento) {
            ultimoPuntoIncremento = pts;
            velocidadTubos = Math.min(velocidadTubos + 0.15, 8.0);
        }

        // Comprobar colisiones
        comprobarColisiones();

        // Comprobar límites
        comprobarLimites();
        
    }

    // =========================================================
    // PÁJARO
    // =========================================================

    /**
     * Hace saltar al pájaro.
     */
    public void saltar() {

        if (estado == Estado.MENU) {

            iniciarJuego();

            pajaro.saltare();

        } else if (estado == Estado.JUGANDO) {

            pajaro.saltare();

        } else if (estado == Estado.GAME_OVER) {

            reiniciarJuego();

            pajaro.saltare();
        }
    }

    // =========================================================
    // TUBOS
    // =========================================================

    /**
     * Genera un nuevo tubo.
     */
    private void generarTubo() {

        double posicionX = anchoPantalla;

        Tubo tubo = new Tubo(
                posicionX,
                altoPantalla,
                altoAgujero,
                velocidadTubos
        );

        tubos.add(tubo);
    }

    /**
     * Actualiza todos los tubos.
     */
    private void actualizarTubos() {

        Iterator<Tubo> iterator = tubos.iterator();

        while (iterator.hasNext()) {

            Tubo tubo = iterator.next();

            tubo.actualizar();

            // Eliminar tubos que ya salieron de la pantalla
            if (tubo.estaFueraDePantalla()) {
                iterator.remove();
            }
        }
    }

    /**
     * Controla cuándo se debe crear un nuevo tubo.
     */
    private void controlarGeneracionTubos() {

        if (tubos.isEmpty()) {

            generarTubo();

            return;
        }

        Tubo ultimoTubo = tubos.get(tubos.size() - 1);

        if (ultimoTubo.getX()
                <= anchoPantalla - distanciaEntreTubos) {

            generarTubo();
        }
    }

    // =========================================================
    // COLISIONES
    // =========================================================

    /**
     * Comprueba si el pájaro ha chocado con algún tubo.
     */
    private void comprobarColisiones() {

        for (Tubo tubo : tubos) {

            boolean colisionSuperior =
                    pajaro.getRectanngulo()
                    .intersects(tubo.getRectanguloSuperior());

            boolean colisionInferior =
                    pajaro.getRectanngulo()
                    .intersects(
                            tubo.getRectanguloInferior(altoPantalla)
                    );

            if (colisionSuperior || colisionInferior) {

                terminarJuego();

                return;
            }
        }
    }

    /**
     * Comprueba si el pájaro salió de los límites de la pantalla.
     */
    private void comprobarLimites() {

        if (pajaro.getY() < 0
                || pajaro.getY() + pajaro.getAlto() > altoPantalla) {

            terminarJuego();
        }
    }

    // =========================================================
    // PUNTUACIÓN
    // =========================================================

    /**
     * Comprueba si el pájaro ha superado algún tubo.
     */
    private void comprobarPuntuacion() {

        for (Tubo tubo : tubos) {

            if (!tubo.isSuperado()
                    && pajaro.getX()
                    > tubo.getX() + tubo.getAnchoTubo()) {

                tubo.marcarSuperado();

                puntuacion.sumarPunto();
            }
        }
    }

    // =========================================================
    // FIN DEL JUEGO
    // =========================================================

    /**
     * Finaliza la partida y guarda el récord.
     */
    private void terminarJuego() {

        if (estado != Estado.JUGANDO) {
            return;
        }

        estado = Estado.GAME_OVER;

        pajaro.setVivo(false);

        // Guardar récord
        puntuacionDAO.guardarMejorPuntuacion(
                puntuacion.getMejorPuntuacion()
        );
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Estado getEstado() {
        return estado;
    }

    public Pajaro getPajaro() {
        return pajaro;
    }

    public List<Tubo> getTubos() {
        return tubos;
    }

    public Puntuacion getPuntuacion() {
        return puntuacion;
    }

    public int getAnchoPantalla() {
        return anchoPantalla;
    }

    public int getAltoPantalla() {
        return altoPantalla;
    }

    public int getAltoAgujero() {
        return altoAgujero;
    }

    public double getVelocidadTubos() {
        return velocidadTubos;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setAnchoPantalla(int anchoPantalla) {
        this.anchoPantalla = anchoPantalla;
    }

    public void setAltoPantalla(int altoPantalla) {
        this.altoPantalla = altoPantalla;
    }

    public void setAltoAgujero(int altoAgujero) {
        this.altoAgujero = altoAgujero;
    }

    public void setVelocidadTubos(double velocidadTubos) {
        this.velocidadTubos = velocidadTubos;
    }
}
