package capa_presentacion;

import javax.swing.*;
import java.awt.*;

public class InterfazPA3 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            // Altura = 90% de la pantalla
            int alto = (int)(screenSize.height * 0.9);
            // Ancho = 60% del alto (relación 3:5, vertical)
            int ancho = (int)(alto * 0.7);
            
            // Opcional: limitar ancho mínimo/máximo si quieres
            // ancho = Math.max(400, Math.min(ancho, 800));
            JFrame frame = new JFrame("Flappy Bird - 16 Bits");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            PanelJuego panel = new PanelJuego(frame, ancho, alto);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}