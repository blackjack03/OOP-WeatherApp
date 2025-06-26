package org.app.weathermode.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.app.weathermode.model.AbstractPair;
import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.LocationSelectorImpl;
import org.app.weathermode.model.Pair;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GUI per la selezione di una località che restituisce un Optional<Integer>
 * contenente l'ID della città scelta o Optional.empty() se l'utente chiude
 * la finestra senza effettuare alcuna selezione.
 */
public class LocationSelectorGUI {

    /**
     * Mostra un semplice messaggio di conferma. Puoi rimuoverlo o modificarlo
     * secondo le tue necessità.
     */
    private static void onButtonPressed(final int value) {
        JOptionPane.showMessageDialog(null, "Location ID: " + value,
                "Selezione Confermata", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Avvia la GUI in modalità bloccante e restituisce l'ID della località
     * selezionata oppure Optional.empty() se la finestra viene chiusa.
     *
     * @param citySelector istanza di LocationSelector da cui ottenere le città
     * @return Optional con l'ID selezionato o Optional.empty()
     */
    public Optional<Integer> start(final LocationSelector citySelector) {
        // Oggetto thread‑safe dove memorizzeremo l'ID scelto
        final AtomicReference<Integer> selectedId = new AtomicReference<>(null);
        // Latch che ci permette di attendere la chiusura della finestra
        final CountDownLatch latch = new CountDownLatch(1);

        // Creiamo l'interfaccia grafica sull'EDT
        SwingUtilities.invokeLater(() -> {
            // Nimbus Look & Feel
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
            }

            // Finestra principale
            final JFrame frame = new JFrame("Scegli la Località");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(650, 500);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setLayout(new BorderLayout(10, 10));
            ((JComponent) frame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            /* =================== PANNELLO NORD =================== */
            final JLabel label = new JLabel("Cerca una città (in inglese):");
            label.setFont(new Font("SansSerif", Font.PLAIN, 16));
            final JTextField textField = new JTextField(20);
            textField.setFont(new Font("SansSerif", Font.PLAIN, 16));

            final JPanel northPanel = new JPanel();
            northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
            northPanel.setOpaque(false);
            northPanel.add(label);
            northPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            northPanel.add(textField);

            frame.add(northPanel, BorderLayout.NORTH);

            /* =================== PANNELLO CENTRALE =================== */
            final JPanel resultPanel = new JPanel(new GridBagLayout());
            resultPanel.setBackground(Color.WHITE);
            final JScrollPane scrollPane = new JScrollPane(resultPanel);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Risultati"));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            frame.add(scrollPane, BorderLayout.CENTER);

            /* =================== PANNELLO SUD =================== */
            final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            final JButton exitButton = new JButton("Esci");
            exitButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            exitButton.addActionListener(e -> frame.dispose());
            bottomPanel.add(exitButton);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            /* =================== LISTENER CAMPO DI TESTO =================== */
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    final String inputText = textField.getText().trim();
                    if (inputText.isEmpty() || inputText.length() < 2) {
                        updateResults(List.of());
                        return;
                    }
                    updateResults(citySelector.getPossibleLocations(inputText));
                }

                private void updateResults(final List<Pair<String, Integer>> locations) {
                    resultPanel.removeAll();
                    final GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.weightx = 1.0;

                    for (final AbstractPair<String, Integer> location : locations) {
                        final String buttonText = location.getX();
                        final int LOCATION_ID = location.getY();

                        final JButton button = new JButton(buttonText);
                        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        button.setFocusPainted(false);
                        button.setBackground(new Color(70, 130, 180));
                        button.setForeground(Color.WHITE);
                        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                        button.addActionListener(evt -> {
                            // onButtonPressed(LOCATION_ID);
                            selectedId.set(LOCATION_ID); // memorizza selezione
                            frame.dispose();             // chiude finestra
                        });
                        resultPanel.add(button, gbc);
                        gbc.gridy++;
                    }

                    if (locations.isEmpty()) {
                        final JLabel noResults = new JLabel("Nessuna città trovata.");
                        noResults.setFont(new Font("SansSerif", Font.ITALIC, 14));
                        noResults.setForeground(Color.GRAY);
                        resultPanel.add(noResults, gbc);
                    }

                    resultPanel.revalidate();
                    resultPanel.repaint();
                }
            });

            /* =================== GESTIONE CHIUSURA FINESTRA =================== */
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    latch.countDown();
                }
            });

            frame.setVisible(true);
        });

        // In attesa che l'utente chiuda la finestra o faccia una selezione
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return Optional.ofNullable(selectedId.get());
    }

    /**
     * Esempio d'uso.
     */
    public static void main(String[] args) {
        final LocationSelectorGUI gui = new LocationSelectorGUI();
        final LocationSelectorImpl ls = new LocationSelectorImpl();
        Optional<Integer> result = gui.start(ls);
        System.out.println("Risultato: " + result);
    }
}



/*import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import org.app.model.LocationSelector;
import org.app.model.Pair;

public class LocationSelectorGUI {

    public static void onButtonPressed(final int value) {
        System.out.println("Location ID: " + value);
    }

    public static void main(String[] args) {

        final LocationSelector city_selector = new LocationSelector();

        final JFrame frame = new JFrame("Scegli la Localita'");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        frame.setLayout(new BorderLayout());

        // Aggiungi il JLabel all'inizio
        final JLabel label = new JLabel("Cerca una citta' (in inglese):");
        frame.add(label, BorderLayout.NORTH);

        final JTextField textField = new JTextField(20);
        // Usa un JPanel per tenere sia il JLabel che la JTextField
        final JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(label);
        northPanel.add(textField);
        frame.add(northPanel, BorderLayout.NORTH);

        final JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        final JScrollPane scrollPane = new JScrollPane(resultPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final String inputText = textField.getText();
                if (inputText.trim().equals("") || inputText.length() < 2) {
                    resultPanel.removeAll();
                    // Refresh GUI
                    resultPanel.revalidate();
                    resultPanel.repaint();
                    return;
                }

                final List<Pair<String, Integer>> locations = city_selector.getPossibleLocations(inputText);

                resultPanel.removeAll();

                for (final Pair<String, Integer> location : locations) {
                    final String buttonText = location.getX();
                    final int LOCATION_ID = location.getY();

                    final JButton button = new JButton(buttonText);

                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            onButtonPressed(LOCATION_ID);
                        }
                    });

                    resultPanel.add(button);
                }
    
                // Refresh GUI
                resultPanel.revalidate();
                resultPanel.repaint();
            }
        });

        frame.setVisible(true);
    }

}*/
