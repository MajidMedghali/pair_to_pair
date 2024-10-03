import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddFilePanel extends JPanel {
    private JTextField filePathField;
    private JButton browseButton;
    private JButton validateButton;
    private JButton backButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public AddFilePanel(CardLayout cardLayout, JPanel cards) {
        filePathField = new JTextField(20);
        browseButton = new JButton("Parcourir");
        validateButton = new JButton("Valider");
        backButton = new JButton("Retour");

        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    filePathField.setText(selectedFilePath);
                }
            }
        });

        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Ajouter la logique pour gérer le fichier
                cardLayout.show(cards, "peerPanel");
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "peerPanel");
            }
        });

        add(new JLabel("Chemin vers le fichier:"));
        add(filePathField);
        add(browseButton);
        add(validateButton);
        add(backButton);
    }
}
