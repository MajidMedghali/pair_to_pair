import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigParser {

    public static String extractTrackerAddress(String filename) {
        String trackerAddress = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("tracker-address")) {
                    trackerAddress = line.split("=")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerAddress;
    }

    public static int extractTrackerPort(String filename) {
        int trackerPort = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("tracker-port")) {
                    trackerPort = Integer.parseInt(line.split("=")[1].trim());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerPort;
    }
}