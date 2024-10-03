import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetIpAddress {

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

    public static void main(String[] args) {
        try {
            String ipAddress = getLocalIpAddress();
            System.out.println("Your IP address is: " + ipAddress);
        } catch (SocketException e) {
            System.err.println("Error getting IP address: " + e.getMessage());
        }
    }
}
