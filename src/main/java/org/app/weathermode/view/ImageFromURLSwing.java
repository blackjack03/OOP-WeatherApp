package org.app.weathermode.view;

// CHECKSTYLE: AvoidStarImport OFF
import javax.swing.*;
import java.awt.*;
// CHECKSTYLE: AvoidStarImport ON

import javax.swing.border.EmptyBorder;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * <h2>ImageFromURLSwing</h2>
 * <p>Utility finale che scarica e visualizza un'immagine da un URL in una
 * finestra Swing ridimensionata automaticamente.</p>
 * <p>Fornisce due soli metodi pubblici {@link #viewIMG(String)} e
 * {@link #viewIMG(String, String, String)}. Tutta la logica di caricamento
 * avviene su uno {@link SwingWorker} per non bloccare l'EDT.</p>
 */
public final class ImageFromURLSwing {
    // Limiti massimi, dimensioni minime e padding
    private static final int MAX_WIDTH     = 1080;
    private static final int MAX_HEIGHT    = 920;
    private static final int MIN_DIMENSION = 350;
    private static final int PADDING       = 10;

    private ImageFromURLSwing() { }

    /**
     * Mostra un'immagine recuperata da URL senza titolo né testo aggiuntivo.
     *
     * @param imageUrl URL dell'immagine da visualizzare.
     */
    public static void viewIMG(final String imageUrl) {
        viewIMG(imageUrl, null, null);
    }

    // CHECKSTYLE: MagicNumber OFF
    private static double calcScale(final int origW, final int origH) {
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
    // CHECKSTYLE: MagicNumber ON

    /**
     * Visualizza un'immagine da URL con titolo facoltativo sopra la figura e
     * titolo finestra personalizzabile.
     *
     * @param imageUrl    URL dell'immagine.
     * @param title       testo mostrato sopra l'immagine (può essere {@code null}).
     * @param winTitle titolo della finestra (se {@code null} diventa "Image Viewer").
     */
    public static void viewIMG(final String imageUrl, final String title, final String winTitle) {
        String windowTitle = winTitle;
        if (windowTitle == null) {
            windowTitle = "Image Viewer";
        }
        final String finalImageTitle = windowTitle;

        // Tutta la GUI viene preparata sull’EDT
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame(finalImageTitle);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            if (title != null && !title.isBlank()) {
                final JLabel lbl = new JLabel(title, SwingConstants.CENTER);
                final float fontSize = 20f;
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, fontSize));
                lbl.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                frame.add(lbl, BorderLayout.NORTH);
            }

            // SwingWorker: carica in background, poi aggiorna l’EDT
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    final Image original = ImageIO.read(new URL(imageUrl));
                    final int w = original.getWidth(null);
                    final int h = original.getHeight(null);
                    final double scale = calcScale(w, h);
                    final Image scaled = original.getScaledInstance(
                            (int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }

                @Override
                protected void done() {
                    try {
                        final JLabel img = new JLabel(get());
                        img.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                        frame.add(img, BorderLayout.CENTER);
                        frame.pack();
                        frame.setSize(
                            Math.max(frame.getWidth(),  MIN_DIMENSION),
                            Math.max(frame.getHeight(), MIN_DIMENSION));
                        frame.setResizable(false);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    } catch (final Exception ex) { // NOPMD
                        showErrorOnEDT("Errore nel recupero dell'immagine", "Impossibile visualizzare l'immagine");
                    }
                }
            }.execute();
        });
    }

    private static void showErrorOnEDT(final String msg, final String title) {
        SwingUtilities.invokeLater(() ->
            CustomErrorGUI.showError(msg, title)
        );
    }

}
