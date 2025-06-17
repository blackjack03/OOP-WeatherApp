package org.app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageFromURLSwing {
    // Limiti massimi, dimensioni minime e padding
    private static final int MAX_WIDTH     = 1080;
    private static final int MAX_HEIGHT    = 920;
    private static final int MIN_DIMENSION = 350;
    private static final int PADDING       = 10;

    /**
     * Visualizza immagine da URL senza titolo
     */
    public static void viewIMG(String imageUrl) {
        viewIMG(imageUrl, null, null);
    }

    /**
     * Visualizza immagine da URL con titolo opzionale
     * @param imageUrl URL dell'immagine
     * @param title Titolo da mostrare sopra l'immagine
     */
    public static void viewIMG(final String imageUrl, final String title, String window_title) {
        if (window_title == null) {
            window_title = "Image Viewer";
        }
        final JFrame frame = new JFrame(window_title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Aggiungi titolo se fornito
        if (title != null && !title.trim().isEmpty()) {
            final JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
            titleLabel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
            frame.add(titleLabel, BorderLayout.NORTH);
        }

        try {
            // Carica immagine originale
            final URL url = new URL(imageUrl);
            final Image original = ImageIO.read(url);
            final int origW = original.getWidth(null);
            final int origH = original.getHeight(null);

            final double availableMaxW = MAX_WIDTH - 2 * PADDING;
            final double availableMaxH = MAX_HEIGHT - 2 * PADDING;
            final double availableMin  = MIN_DIMENSION - 2 * PADDING;
            final double scale;

            if (origW > availableMaxW || origH > availableMaxH) {
                // Riduci se supera i limiti
                scale = Math.min(availableMaxW / origW, availableMaxH / origH);
            } else if (origW < availableMin || origH < availableMin) {
                // Ingrandisci se è troppo piccola
                scale = Math.max(availableMin / origW, availableMin / origH);
            } else {
                // Mantieni dimensioni originali
                scale = 1.0;
            }

            final int dispW = (int) (origW * scale);
            final int dispH = (int) (origH * scale);

            // Scaled instance
            final Image scaled = original.getScaledInstance(dispW, dispH, Image.SCALE_SMOOTH);
            final JLabel imageLabel = new JLabel(new ImageIcon(scaled));
            imageLabel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
            frame.add(imageLabel, BorderLayout.CENTER);

            // Adatta finestra
            frame.pack();
            // Assicura dimensione minima
            final int finalW = Math.max(frame.getWidth(), MIN_DIMENSION);
            final int finalH = Math.max(frame.getHeight(), MIN_DIMENSION);
            frame.setSize(finalW, finalH);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        final String imageUrl = "https://www.moongiant.com/images/today_phase/moon_day_WanC_5.jpg";
        SwingUtilities.invokeLater(() -> viewIMG(imageUrl, "Luna di Oggi", "MOON Info"));
    }

}
