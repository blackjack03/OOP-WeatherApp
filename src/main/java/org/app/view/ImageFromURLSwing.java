package org.app.view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageFromURLSwing {

    public static void main(String[] args) {
        // Crea il frame
        final JFrame frame = new JFrame("Visualizza Immagine da URL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        try {
            // Inserisci qui l'URL dell'immagine
            final String imageUrl = "https://www.moongiant.com/images/today_phase/moon_day_WanC_5.jpg";
            final URL url = new URL(imageUrl);

            // Carica l'immagine dall'URL
            final Image image = ImageIO.read(url);

            // Ridimensiona l'immagine se necessario
            final Image scaledImage = image.getScaledInstance(500, -1, Image.SCALE_SMOOTH);

            // Aggiungi l'immagine a un JLabel
            final JLabel label = new JLabel(new ImageIcon(scaledImage));

            // Aggiungi il JLabel al frame
            frame.add(label);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mostra il frame
        frame.setVisible(true);
    }
}
