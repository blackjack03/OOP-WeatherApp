package org.app.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import org.app.model.AbstractPair;
import org.app.model.LocationSelector;
import org.app.model.Pair;

public class LocationSelectorGUI {

    public static void onButtonPressed(final int value) {
        JOptionPane.showMessageDialog(null, "Location ID: " + value, "Selezione Confermata", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // String currentDir = System.getProperty("user.dir");
        // System.out.println("Current dir using System:" + currentDir);

        // Imposta il Look and Feel a Nimbus per un aspetto più moderno
        try {
            for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final Exception err) {
            // Se Nimbus non è disponibile, usa il default
            err.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            final LocationSelector citySelector = new LocationSelector();

            final JFrame frame = new JFrame("Scegli la Località");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(650, 500);
            frame.setLocationRelativeTo(null); // Centra la finestra

            // Utilizza BorderLayout con padding
            frame.getContentPane().setLayout(new BorderLayout(10, 10));
            ((JComponent) frame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            // Pannello Nord con label e campo di testo
            final JLabel label = new JLabel("Cerca una città (in inglese):");
            label.setFont(new Font("SansSerif", Font.PLAIN, 16));

            final JTextField textField = new JTextField(20);
            textField.setFont(new Font("SansSerif", Font.PLAIN, 16));

            final JPanel northPanel = new JPanel();
            northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
            northPanel.setOpaque(false); // Trasparente per mostrare il background del frame
            northPanel.add(label);
            northPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spazio tra label e campo
            northPanel.add(textField);

            frame.add(northPanel, BorderLayout.NORTH);

            // Pannello Centrale per i risultati
            final JPanel resultPanel = new JPanel();
            resultPanel.setLayout(new GridBagLayout());
            resultPanel.setBackground(Color.WHITE);

            final JScrollPane scrollPane = new JScrollPane(resultPanel);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Risultati"));
            scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll più fluido

            frame.add(scrollPane, BorderLayout.CENTER);

            // Pannello Inferiore con eventuali pulsanti aggiuntivi
            final JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            // Esempio: aggiungi un pulsante "Esci"
            final JButton exitButton = new JButton("Esci");
            exitButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            exitButton.addActionListener(e -> System.exit(0));
            bottomPanel.add(exitButton);

            frame.add(bottomPanel, BorderLayout.SOUTH);

            // Listener per il campo di testo
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(final KeyEvent e) {
                    final String inputText = textField.getText().trim();
                    if (inputText.isEmpty() || inputText.length() < 2) {
                        resultPanel.removeAll();
                        resultPanel.revalidate();
                        resultPanel.repaint();
                        return;
                    }

                    final List<Pair<String, Integer>> locations = citySelector.getPossibleLocations(inputText);

                    resultPanel.removeAll();

                    final GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 0;
                    gbc.gridy = 0;

                    for (final AbstractPair<String, Integer> location : locations) {
                        final String buttonText = location.getX();
                        final int LOCATION_ID = location.getY();

                        final JButton button = new JButton(buttonText);
                        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        button.setFocusPainted(false);
                        button.setBackground(new Color(70, 130, 180));
                        button.setForeground(Color.WHITE);
                        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                        button.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                onButtonPressed(LOCATION_ID);
                            }
                        });

                        gbc.gridx = 0;
                        gbc.weightx = 1.0;
                        resultPanel.add(button, gbc);
                        gbc.gridy++;
                    }

                    // Se non ci sono risultati, mostra un messaggio
                    if (locations.isEmpty()) {
                        final JLabel noResults = new JLabel("Nessuna città trovata.");
                        noResults.setFont(new Font("SansSerif", Font.ITALIC, 14));
                        noResults.setForeground(Color.GRAY);
                        resultPanel.add(noResults, gbc);
                    }

                    // Refresh GUI
                    resultPanel.revalidate();
                    resultPanel.repaint();
                }
            });

            frame.setVisible(true);
        });
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
