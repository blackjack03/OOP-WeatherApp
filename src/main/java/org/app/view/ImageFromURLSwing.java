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

    private static double calcScale(int origW, int origH) {
        final double availableMaxW = MAX_WIDTH - 2 * PADDING;
        final double availableMaxH = MAX_HEIGHT - 2 * PADDING;
        final double availableMin  = MIN_DIMENSION - 2 * PADDING;

        if (origW > availableMaxW || origH > availableMaxH) {
            return Math.min(availableMaxW / origW, availableMaxH / origH);
        } else if (origW < availableMin || origH < availableMin) {
            return Math.max(availableMin / origW, availableMin / origH);
        } else {
            return 1.0;
        }
    }


    /**
     * Visualizza immagine da URL con titolo opzionale
     * @param imageUrl URL dell'immagine
     * @param title Titolo da mostrare sopra l'immagine
     */
    public static void viewIMG(String imageUrl, String title, String windowTitle) {
        if (windowTitle == null) windowTitle = "Image Viewer";
        final String finalImageTitle = windowTitle;

        // Tutta la GUI viene preparata sull’EDT
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(finalImageTitle);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            if (title != null && !title.isBlank()) {
                JLabel lbl = new JLabel(title, SwingConstants.CENTER);
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
                lbl.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                frame.add(lbl, BorderLayout.NORTH);
            }

            // SwingWorker: carica in background, poi aggiorna l’EDT
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    Image original = ImageIO.read(new URL(imageUrl));
                    int w = original.getWidth(null);
                    int h = original.getHeight(null);
                    double scale = calcScale(w, h);           // tuo metodo di calcolo
                    Image scaled = original.getScaledInstance(
                            (int)(w*scale), (int)(h*scale), Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }

                @Override
                protected void done() {
                    try {
                        JLabel img = new JLabel(get());
                        img.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                        frame.add(img, BorderLayout.CENTER);
                        frame.pack();
                        frame.setSize(
                            Math.max(frame.getWidth(),  MIN_DIMENSION),
                            Math.max(frame.getHeight(), MIN_DIMENSION));
                        frame.setResizable(false);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    } catch (Exception ex) {
                        showErrorOnEDT("Errore nel recupero dell'immagine", "Impossibile visualizzare l'immagine");
                    }
                }
            }.execute();
        });
    }

    private static void showErrorOnEDT(String msg, String title) {
        SwingUtilities.invokeLater(() ->
            CustomErrorGUI.showError(msg, title)
        );
    }

    public static void main(String[] args) {
        final String imageUrl = "https://www.moongiant.com/images/today_phase/moon_day_WanC_5.jpg";
        SwingUtilities.invokeLater(() -> viewIMG(imageUrl, "Luna di Oggi", "MOON Info"));
    }

}
