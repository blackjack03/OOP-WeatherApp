package org.app.weathermode.view;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * <h2>CustomErrorGUI</h2>
 * <p>Utility statica che mostra finestre di dialogo di errore, avviso,
 * informazione o conferma usando Swing oppure JavaFX, a seconda del contesto.</p>
 * <p>Tutti i metodi sono <em>thread‑safe</em>: la versione JavaFX usa
 * {@link Platform#runLater(Runnable)} per eseguire il dialog sull'UI thread.</p>
 */
public final class CustomErrorGUI {

    private CustomErrorGUI() { }

    /**
     * Visualizza un <strong>errore</strong> Swing con icona rossa.
     *
     * @param message testo del messaggio.
     * @param title   titolo della finestra.
     */
    public static void showError(final String message, final String title) {
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        final JDialog dialog = optionPane.createDialog(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Visualizza un <strong>avvertimento</strong> Swing con icona gialla.
     *
     * @param message testo del messaggio.
     * @param title   titolo della finestra.
     */
    public static void showWarning(final String message, final String title) {
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        final JDialog dialog = optionPane.createDialog(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Visualizza una finestra di <strong>informazione</strong> Swing.
     *
     * @param message testo del messaggio.
     * @param title   titolo della finestra.
     */
    public static void showInfo(final String message, final String title) {
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = optionPane.createDialog(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Mostra una finestra di <strong>conferma</strong> Swing (Yes/No).
     *
     * @param message testo della domanda.
     * @param title   titolo della finestra.
     * @return {@code true} se l'utente ha premuto «Yes», {@code false} altrimenti.
     */
    public static boolean showConfirm(final String message, final String title) {
        final int result = JOptionPane.showConfirmDialog(null, message,
            title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Visualizza un <strong>avvertimento</strong> JavaFX.
     *
     * @param message testo del messaggio.
     * @param title   titolo della finestra.
     */
    public static void showWarningJFX(final String message, final String title) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Visualizza un <strong>errore</strong> JavaFX.
     *
     * @param message testo del messaggio.
     * @param title   titolo della finestra.
     */
    public static void showErrorJFX(final String message, final String title) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
