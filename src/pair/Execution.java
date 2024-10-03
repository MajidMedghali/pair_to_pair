import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
// import Pair;
public class Execution {
    public static void main(String[] args) {
        try {
            // Création des fichiers
            int port1 = Pair.findAvailablePort();
             String ipAddress = getLocalIpAddress();
            int theTrackerPort = ConfigParser.extractTrackerPort("config.ini");
            String theTrackerAddress = ConfigParser.extractTrackerAddress("config.ini");
            int partSize = 100*1024*1024; //2 Mo
            boolean copy_all_pieces  = new Random().nextBoolean();
            Pair pair = new Pair(port1, theTrackerAddress, theTrackerPort, "./res/txt", copy_all_pieces ,partSize,ipAddress);

            // Attendre l'entrée de l'utilisateur
            Scanner scanner = new Scanner(System.in);
            boolean validInput = false;
            
            while (!validInput) {
                System.out.print("Entrez votre commande 'interested' ou votre commande 'look': ");
                String input = scanner.nextLine();
                if (input.startsWith("interested")) {
                    String hashKey = input.substring(10).trim();
                    if (hashKey.length() == 32) {

                        new Thread(() -> {
                            try {
                                Thread.sleep(2000); // Delay for a bit before sending the interest message
                            pair.sendGetFileCommand(hashKey);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        validInput = true;
                    } else {
                        System.out.println("La clé de hachage doit être de 32 bits.");
                    }
                } else if (input.startsWith("look")) {{
                    // String hashKey = input.substring(4).trim();
                    new Thread(() -> {
                            try {

                                Thread.sleep(2000); // Delay for a bit before sending the interest message
                                pair.sendLookCommand(input);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        validInput = true;
                }
            }
            else {
                System.out.println("La commande doit commencer par 'interested' ou par 'look'.");
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static String getLocalIpAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // Skip loopback interfaces
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                // Only return IPv4 addresses
                if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                    return addr.getHostAddress();
                }
            }
        }
        return null;
    }
}