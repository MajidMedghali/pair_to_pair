import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class WebPagePanel extends JPanel {
    private JLabel promptLabel;
    private JTextField messageInputField;
    private JButton saveMessageButton;
    private JPanel downloadPanel;
    private JProgressBar progressBar;
    private Pair pair;

    public WebPagePanel() {
        setLayout(new BorderLayout(10, 10)); // Ajouter une marge entre les composants
        try {
            int port1 = findAvailablePort();
            int theTrackerPort = ConfigParser.extractTrackerPort("./config.ini");
            String theTrackerAddress = ConfigParser.extractTrackerAddress("./config.ini");
            int partSize = 2*1024*1024; //2 Mo
            pair = new Pair(port1, theTrackerAddress, theTrackerPort, "./res/txt/", false, partSize,"172.20.10.5");

            // Ajouter une étiquette pour guider l'utilisateur
            promptLabel = new JLabel("Enter your interested message or look message :");
            add(promptLabel, BorderLayout.NORTH);

            // Panel pour contenir le champ de texte et le centrer
            JPanel textFieldPanel = new JPanel();
            textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            messageInputField = new JTextField(20); // Définir la longueur du champ de texte
            textFieldPanel.add(messageInputField);
            add(textFieldPanel, BorderLayout.CENTER);

            // Ajouter un bouton pour sauvegarder et afficher le message
            saveMessageButton = new JButton("Start the download");
            saveMessageButton.addActionListener(e -> {
                String message = messageInputField.getText();
                String[] parts = message.split(" ");
                System.out.println("Message received: " + message);  // Afficher le message dans le terminal
                if (parts[0].equals("interested")) {
                    showDownloadAnimation();
                    pair.download=1;
                    System.out.println("show est appelée");
                    new Thread(() -> {
                        
                         try {
                                Thread.sleep(500); // Attendre un court instant avant de vérifier à nouveau
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        pair.sendGetFileCommand(parts[1]);
                    }).start();
                    new Thread(() -> {
                        // Attendre que pair.download ne soit plus égal à 1
                        while (pair.download == 1) {
                            try {
                                Thread.sleep(10); // Attendre un court instant avant de vérifier à nouveau
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        // Une fois que pair.download n'est plus égal à 1, retirer la barre de progression
                        SwingUtilities.invokeLater(this::removeProgressBar);
                    }).start();
                } else if (parts[0].equals("look")) {
                    System.out.println("look message");
                    // showDownloadAnimation();
                    pair.sendLookCommand(message);
                } else {
                    // Handle other cases if needed
                }
            });
            add(saveMessageButton, BorderLayout.SOUTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int findAvailablePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private void showDownloadAnimation() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        downloadPanel = new JPanel();
        downloadPanel.setLayout(new BoxLayout(downloadPanel, BoxLayout.Y_AXIS));
        downloadPanel.add(Box.createVerticalGlue());
        downloadPanel.add(progressBar);
        downloadPanel.add(Box.createVerticalGlue());
        add(downloadPanel, BorderLayout.CENTER); // Ajouter la barre de progression au centre du panneau principal
        revalidate(); // Mettre à jour le layout
        repaint();
    }

    private void removeProgressBar() {
        if (downloadPanel != null) {
            remove(downloadPanel);
            downloadPanel = null;
            revalidate();
            repaint(); // Rafraîchir l'affichage
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("File sharing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);  // Ajuster la taille si nécessaire
            WebPagePanel webPagePanel = new WebPagePanel();
            frame.getContentPane().add(webPagePanel);
            frame.setVisible(true);
        });
    }
}
