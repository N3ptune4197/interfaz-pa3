package interfaz.pa3;

import capa_logica.Juego;
import capa_logica.Pajaro;
import capa_logica.Tubo;
import capa_logica.Puntuacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.List;
import javax.sound.sampled.*;

public class PanelJuego extends JPanel implements ActionListener, KeyListener {

    private Juego juego;
    private Timer timer;
    private float anguloAla = 0;
    private double offsetNubes = 0;
    
    
    private long tiempoGameOver = 0;
    private static final int RETRASO_REINICIO = 1500; 

    // Sonidos (guardamos la URL, no el Clip)
    private URL urlSalto;
    private URL urlMuerte;

    private boolean espacioPresionado = false;
    private Juego.Estado estadoAnterior;

    public PanelJuego(JFrame ventanaPadre, int ancho, int alto) {
        this.setPreferredSize(new Dimension(ancho, alto));
        this.setFocusable(true);
        this.addKeyListener(this);
        juego = new Juego(ancho, alto);
        estadoAnterior = juego.getEstado();

        cargarSonidos();
        timer = new Timer(16, this);
        timer.start();
    }

    private void cargarSonidos() {
        // Mantengo tus rutas originales
        urlSalto = getClass().getResource("../../resources/sonidos/salto.wav");
        urlMuerte = getClass().getResource("../../resources/sonidos/muerte.wav");

        if (urlSalto == null) System.out.println("No se encontró salto.wav");
        if (urlMuerte == null) System.out.println("No se encontró muerte.wav");
    }

    /**
     * Reproduce un sonido creando un nuevo Clip cada vez (más fiable).
     * Se ejecuta en un hilo para no bloquear el EDT.
     */
    private void reproducirSonido(URL url) {
        if (url == null) return;

        new Thread(() -> {
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(url)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());
            }
        }).start();
    }

    // =========================================================
    // PINTADO (TODO IGUAL QUE ANTES)
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarFondo(g2);
        Juego.Estado estado = juego.getEstado();

        if (estado == Juego.Estado.MENU) {
            dibujarMenu(g2);
        } else if (estado == Juego.Estado.JUGANDO || estado == Juego.Estado.GAME_OVER) {
            dibujarTubos(g2);
            dibujarPajaroAnimado(g2);
            dibujarPuntuacion(g2);
            if (estado == Juego.Estado.GAME_OVER) {
                dibujarGameOver(g2);
            }
        }
    }

    private void dibujarFondo(Graphics2D g) {
        int ancho = getWidth(), alto = getHeight();
        g.setColor(new Color(100, 180, 255));
        g.fillRect(0, 0, ancho, alto);

        g.setColor(new Color(255, 255, 255, 180));
        offsetNubes += 0.3;
        if (offsetNubes > 200) offsetNubes = 0;
        for (int i = 0; i < 6; i++) {
            int x = (int)((i * 180 + offsetNubes) % (ancho + 100)) - 50;
            int y = 60 + i * 70 % (alto / 2);
            dibujarNubePixel(g, x, y, 40 + i * 8);
        }

        g.setColor(new Color(80, 160, 40));
        g.fillRect(0, alto - 60, ancho, 60);
        g.setColor(new Color(60, 130, 30));
        for (int i = 0; i < ancho; i += 20) {
            g.fillRect(i, alto - 60, 10, 10);
            g.fillRect(i + 10, alto - 50, 10, 10);
        }
    }

    private void dibujarNubePixel(Graphics2D g, int x, int y, int tamaño) {
        int[][] bloques = {
            {0, 0, 1, 1, 0, 0},
            {0, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1}
        };
        int tam = tamaño / 6;
        for (int i = 0; i < bloques.length; i++) {
            for (int j = 0; j < bloques[i].length; j++) {
                if (bloques[i][j] == 1) {
                    g.fillRect(x + j * tam, y + i * tam, tam, tam);
                }
            }
        }
    }

    private void dibujarMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Press Start 2P", Font.BOLD, 48));
        String titulo = "FLAPPY BIRD";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(titulo)) / 2;
        int y = getHeight() / 2 - 80;
        g.drawString(titulo, x, y);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 20));
        String instruccion = "SPACE o UP para empezar";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(instruccion)) / 2;
        y += 80;
        g.drawString(instruccion, x, y);

        dibujarPajaro(g, juego.getPajaro().getX(), juego.getPajaro().getY(), 0);
    }

    private void dibujarPajaroAnimado(Graphics2D g) {
        Pajaro pajaro = juego.getPajaro();
        double x = pajaro.getX(), y = pajaro.getY();
        if (juego.getEstado() == Juego.Estado.GAME_OVER) {
            anguloAla = -30;
        } else if (pajaro.getVelocidadY() < -2) {
            anguloAla = 45;
        } else {
            anguloAla = 15 * (float) Math.sin(System.currentTimeMillis() / 200.0);
        }
        dibujarPajaro(g, x, y, anguloAla);
    }

    private void dibujarPajaro(Graphics2D g, double x, double y, float anguloAla) {
        int px = (int) x, py = (int) y;
        int ancho = 40, alto = 30;

        g.setColor(new Color(255, 220, 50));
        g.fillRect(px + 5, py + 5, ancho - 10, alto - 10);

        g.setColor(Color.WHITE);
        g.fillRect(px + 22, py + 8, 8, 8);
        g.setColor(Color.BLACK);
        g.fillRect(px + 26, py + 10, 4, 4);

        g.setColor(new Color(255, 140, 0));
        int[] picoX = {px + ancho - 5, px + ancho + 8, px + ancho - 5};
        int[] picoY = {py + 10, py + 15, py + 20};
        g.fillPolygon(picoX, picoY, 3);

        g.setColor(new Color(200, 170, 30));
        if (anguloAla > 0) {
            int[] alaX = {px + 10, px + 20, px + 28};
            int[] alaY = {py + 8, py - 10, py + 5};
            g.fillPolygon(alaX, alaY, 3);
        } else {
            int[] alaX = {px + 10, px + 5, px + 20};
            int[] alaY = {py + 22, py + 30, py + 18};
            g.fillPolygon(alaX, alaY, 3);
        }

        g.setColor(new Color(180, 150, 20));
        int[] colaX = {px + 2, px - 5, px + 2};
        int[] colaY = {py + 12, py + 15, py + 18};
        g.fillPolygon(colaX, colaY, 3);
    }

    private void dibujarTubos(Graphics2D g) {
        List<Tubo> tubos = juego.getTubos();
        if (tubos == null || tubos.isEmpty()) return;

        for (Tubo tubo : tubos) {
            int x = (int) tubo.getX();
            int ancho = tubo.getAnchoTubo();
            int posAgujero = tubo.getPosicionAgujero();
            int altoAgujero = tubo.getAltoAgujero();

            // === TUBO SUPERIOR ===
            int altoSup = posAgujero;
            // Sombrero (parte ancha) - 10 píxeles más ancho
            int sombreroAncho = ancho + 14;
            int sombreroAlto = 20;

            // Dibujar sombrero superior
            g.setColor(new Color(40, 140, 40));
            g.fillRect(x - 7, altoSup - sombreroAlto, sombreroAncho, sombreroAlto);
            g.setColor(new Color(30, 110, 30));
            g.drawRect(x - 7, altoSup - sombreroAlto, sombreroAncho, sombreroAlto);

            // Cuerpo superior (con textura de ladrillos)
            g.setColor(new Color(50, 180, 50));
            g.fillRect(x, 0, ancho, altoSup - sombreroAlto);
            // Dibujar líneas horizontales cada 12 píxeles
            g.setColor(new Color(30, 140, 30));
            for (int i = 12; i < altoSup - sombreroAlto; i += 16) {
                g.drawLine(x, i, x + ancho, i);
            }
            // Líneas verticales alternadas
            g.setColor(new Color(40, 160, 40));
            for (int i = 12; i < altoSup - sombreroAlto; i += 32) {
                g.drawLine(x + 12, i, x + 12, i + 16);
                g.drawLine(x + 36, i + 16, x + 36, i + 32);
            }
            // Borde exterior
            g.setColor(new Color(20, 100, 20));
            g.drawRect(x, 0, ancho, altoSup - sombreroAlto);

            // === TUBO INFERIOR ===
            int yInf = posAgujero + altoAgujero;
            int altoInf = getHeight() - yInf;

            // Sombrero inferior (parte ancha abajo)
            g.setColor(new Color(40, 140, 40));
            g.fillRect(x - 7, yInf, sombreroAncho, sombreroAlto);
            g.setColor(new Color(30, 110, 30));
            g.drawRect(x - 7, yInf, sombreroAncho, sombreroAlto);

            // Cuerpo inferior
            g.setColor(new Color(50, 180, 50));
            g.fillRect(x, yInf + sombreroAlto, ancho, altoInf - sombreroAlto);
            // Líneas horizontales
            g.setColor(new Color(30, 140, 30));
            for (int i = yInf + sombreroAlto + 12; i < getHeight(); i += 16) {
                g.drawLine(x, i, x + ancho, i);
            }
            // Líneas verticales alternadas
            g.setColor(new Color(40, 160, 40));
            for (int i = yInf + sombreroAlto + 12; i < getHeight(); i += 32) {
                g.drawLine(x + 12, i, x + 12, i + 16);
                g.drawLine(x + 36, i + 16, x + 36, i + 32);
            }
            // Borde exterior
            g.setColor(new Color(20, 100, 20));
            g.drawRect(x, yInf + sombreroAlto, ancho, altoInf - sombreroAlto);
        }
    }
    
    
    private void dibujarPuntuacion(Graphics2D g) {
        Puntuacion p = juego.getPuntuacion();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Press Start 2P", Font.BOLD, 20));
        g.drawString("PTS: " + p.getPuntosActuales(), 20, 40);
        g.drawString("BEST: " + p.getMejorPuntuacion(), 20, 70);
    }

    private void dibujarGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.setFont(new Font("Press Start 2P", Font.BOLD, 48));
        String msg = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2 - 60;
        g.drawString(msg, x, y);

        Puntuacion p = juego.getPuntuacion();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Press Start 2P", Font.PLAIN, 24));
        msg = "SCORE: " + p.getPuntosActuales();
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        y += 70;
        g.drawString(msg, x, y);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 16));
        msg = "SPACE para reiniciar";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg)) / 2;
        y += 50;
        g.drawString(msg, x, y);
    }

    // =========================================================
    // ACTION & KEY LISTENER (SOLO CAMBIA la llamada a reproducirSonido)
    // =========================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (juego.getEstado() == Juego.Estado.JUGANDO) {
            juego.actualizar();
        }

        // Detectar momento exacto en que pasa a GAME_OVER
        if (estadoAnterior == Juego.Estado.JUGANDO
                && juego.getEstado() == Juego.Estado.GAME_OVER) {
            reproducirSonido(urlMuerte);
            tiempoGameOver = System.currentTimeMillis(); // Guardamos el instante
        }

        estadoAnterior = juego.getEstado();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_SPACE) {
            return;
        }

        if (espacioPresionado) {
            return;
        }
        espacioPresionado = true;

        Juego.Estado estado = juego.getEstado();

        if (estado == Juego.Estado.GAME_OVER) {
            long ahora = System.currentTimeMillis();
            if (ahora - tiempoGameOver >= RETRASO_REINICIO) {
                // Solo reinicia si ha pasado el retraso
                juego.reiniciarJuego();
                // Opcional: reproducir sonido de salto al reiniciar
                // reproducirSonido(urlSalto);
            }
            // Si no ha pasado el tiempo, no hacemos nada
            repaint();
            return;
        } else {
            juego.saltar();
            if (estado == Juego.Estado.MENU || estado == Juego.Estado.JUGANDO) {
                reproducirSonido(urlSalto);
            }
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            espacioPresionado = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No usado
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird - 16 Bits");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            int ancho = 600, alto = 800;
            PanelJuego panel = new PanelJuego(frame, ancho, alto);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}