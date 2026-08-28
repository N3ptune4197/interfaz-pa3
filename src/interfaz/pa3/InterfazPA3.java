package interfaz.pa3;

import javax.swing.*;

public class InterfazPA3 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            int ancho = 600;
            int alto = 800; // Puedes ajustar estas medidas

            PanelJuego panel = new PanelJuego(frame, ancho, alto);
            frame.add(panel);
            frame.pack(); // Ajusta al tamaño preferido del panel
            frame.setLocationRelativeTo(null); // Centra en la pantalla
            frame.setVisible(true);
        });
    }
}