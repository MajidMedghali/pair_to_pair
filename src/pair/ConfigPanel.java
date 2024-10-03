import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigPanel extends JPanel {
    private JTextField ipAddressField;
    private JTextField portField;
    private JButton validateButton;
    private JButton backButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public ConfigPanel(CardLayout cardLayout, JPanel cards) {
        ipAddressField = new JTextField(20);
        portField = new JTextField(20);
        validateButton = new JButton("Valider");
        backButton = new JButton("Retour");

        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ipAddress = ipAddressField.getText();
                String port = portField.getText();
                cardLayout.show(cards, "peerPanel");
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "peerPanel");
            }
        });

        add(new JLabel("Adresse IP du tracker:"));
        add(ipAddressField);
        add(new JLabel("Numéro de port TCP d'écoute du tracker:"));
        add(portField);
        add(validateButton);
        add(backButton);
    }
}
