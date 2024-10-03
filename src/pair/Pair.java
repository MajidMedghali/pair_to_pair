import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Base64;


public class Pair {
    private ServerSocket serverSocket;
    private List<Socket> clientSockets;
    private List<Socket> connectionSockets;
    private List<PrintWriter> outToPairs;
    private List<BufferedReader> inToThis;
    public final int port;
    private FileNode fileListHead;
    private String trackerAddress;
    private int trackerPort;
    private Socket trackerSocket; // Socket pour la connexion au tracker
    private volatile boolean isConnected = false;
    private static int partition_copy_index = 1;
    public int download = 0;
    private String adresseIp; 
    public FileNode getFileHead() {
        return this.fileListHead;
    }

    public Pair(int port, String trackerAddress, int trackerPort, String path, boolean copy_all_pieces, int partSize, String adresseIp) {
        this.port = port;
        this.adresseIp=adresseIp;
        this.trackerAddress = trackerAddress;
        this.trackerPort = trackerPort;
        this.connectionSockets = new ArrayList<>();
        this.outToPairs = new ArrayList<>();
        new Thread(() -> startServer(port)).start();
        this.fileListHead = scanDirectory(path, partSize, copy_all_pieces);
        connectToTracker();

    }


    public void storeFile(String key, String output){
        FileNode file = this.findFile(key);
        file.storeBinaryFile(output);
    }
    public static void getRandomNumber(int MAX) {
        partition_copy_index = (int) (Math.random() * (MAX)) + 1;
    }

    public void connectToTracker() {
        new Thread(() -> {
            try {
                try{

                    Thread.sleep(4000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                
                trackerSocket = new Socket(trackerAddress, trackerPort);
                System.out.println("Connected to tracker at " + trackerAddress + ":" + trackerPort);
                isConnected = true;
                PrintWriter outToTracker = new PrintWriter(trackerSocket.getOutputStream(), true);

                // Construire la commande "announce" avec les fichiers du pair
                StringBuilder announceCommand = new StringBuilder("announce listen ");
                announceCommand.append(this.port).append(" seed [");

                // Ajouter les informations des fichiers pour ce pair
                boolean firstFile = true;
                FileNode currentFile = fileListHead;
                System.out.println(currentFile.getFileName());
                while (currentFile != null) {
                    if (!firstFile) {
                        announceCommand.append(" ");
                    }
                    announceCommand.append(currentFile.getFileName())
                            .append(" ")
                            .append(currentFile.getFileSize())
                            .append(" ")
                            .append(currentFile.getPartSize())
                            .append(" ")
                            .append(currentFile.getFileKey());
                    firstFile = false;
                    currentFile = currentFile.getNextFile();
                }
                announceCommand.append("]");

                // Envoyer la commande "announce" au tracker
                outToTracker.println(announceCommand.toString());
                System.out.println("Sent announce command: " + announceCommand.toString());

                // Lire la réponse du tracker
                BufferedReader inFromTracker = new BufferedReader(
                        new InputStreamReader(trackerSocket.getInputStream()));
                String response = inFromTracker.readLine();
                System.out.println("Tracker response: " + response);

            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                return;
            }
        }).start();
    }

    public void sendLookCommand(String criteria) {
        try (Socket socket = new Socket(trackerAddress, trackerPort);

                OutputStream outputStream = socket.getOutputStream()) {
            String command = criteria;
            outputStream.write(command.getBytes());
            String receivedMessage;
            BufferedReader inFromTracker = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((receivedMessage = inFromTracker.readLine()) != null) {
                System.out.println(" ");

                System.out.println("le message reçu est " + receivedMessage);
                String[] elements = Chaine.extraireSousChaine(receivedMessage).replaceAll("\\[|\\]", "").split(" ");
                    if(findFile(elements[3])!=null)
                        sendGetFileCommand(elements[3]);
                    else{
                           FileNode lastFile = new FileNode(elements[0],elements[3],Long.valueOf(elements[1]),Integer.parseInt(elements[2]),0,false, 0);
                             getFileHead().addFileToEnd(lastFile);

                            System.out.println("creation d'un nouvaeu fichier");
                        //  FileNode lastFile = new FileNode(elements[0],elements[3],Long.valueOf(elements[1]),Integer.parseInt(elements[2]),0);
                        getFileHead().addFileToEnd(lastFile);
                         /* la commande de update en envoyant la clé elements[3  ]** */
                         sendGetFileCommand(elements[3]);
                        // sendGetFileCommand(elements[3]);
                    }
                    

                }
                System.out.println(" ");

            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendGetFileCommand(String key) {
        try (

                Socket socket = new Socket(trackerAddress, trackerPort);
                OutputStream outputStream = socket.getOutputStream()) {
            String command = "getfile " + key;
            this.download = 1;
            outputStream.write(command.getBytes());
            String receivedMessage;
            BufferedReader inFromTracker = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((receivedMessage = inFromTracker.readLine()) != null) {
                String[] elements = Chaine.extraireSousChaine(receivedMessage).replaceAll("\\[|\\]", "").split(" ");
                if (elements.length == 1)
                    this.download = 0;
                for (int i = 0; i < elements.length; i++) {
                    String messageresponse = Chaine.extrairePortElement(elements, i);
                    if (Integer.parseInt(messageresponse) != this.port) {
                        PairTarget target = new PairTarget(elements[i].substring(0, elements[i].indexOf(':')), Integer.parseInt(messageresponse));
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000); // Delay for a bit before sending the interest message
                                this.sendMessageToPair("interested " + key, target);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            clientSockets = new ArrayList<>();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                new Thread(() -> handleClientCommunication(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendGetPiecesMessage(String key, List<Integer> list) {

        String Message = "<getpieces " + key + " [";
        for (int i = 0; i < list.size(); i++) {
            if (findFile(key).getBufferMap().get(list.get(i)) == false) {
                Message += list.get(i);

                if (i < list.size() - 1) {
                    Message += " ";
                }

            }
        }
        Message += "]";
        return Message;
    }

    private void handleClientCommunication(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            outToPairs.add(out);

            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                System.out.println();
                System.out.println();
                String[] parts = receivedMessage.split(" ");
                if (parts.length >= 2) {

                    if (parts[0].equals("interested")) {

                        System.out.println("< " + receivedMessage);
                        String requestedFileKey = parts[1];
                        FileNode requestedFile = findFile(requestedFileKey);

                        if (requestedFile != null) {
                            out.println(">have " + requestedFileKey + " " + getFileBufferMap(requestedFileKey));
                        } else {
                            System.out.println("File not found");
                        }
                    } else if (parts[0].equals(">have")) {
                        System.out.println(receivedMessage);
                        String bufferMapString = parts[2];
                        int[] t = Chaine.chaineATableau(bufferMapString);
                        List<Integer> list = Chaine.trouverIndicesAvecValeur1(t);
                        System.out.println(" le pair possède les partiers suivantes du fichier: ");
                        Chaine.printList(list);
                        System.out.println("my buffer map = " + getFileBufferMap(parts[1]));
                        String getPieces = sendGetPiecesMessage(parts[1], list);
                        out.println(getPieces);
                    } else if (parts[0].equals("<getpieces")) {
                        System.out.println(receivedMessage);
                        String parties = Chaine.extraireMots(receivedMessage, 2);
                        System.out.println("my buffer map = " + getFileBufferMap(parts[1]));
                        int[] t = Chaine.piecesTable(parties);
                        if (t == null) {
                        } else {

                            String Message = ">data " + parts[1] + " [ ";
                            for (int i = 0; i < t.length; i++) {
                                byte[] data = findFile(parts[1]).getPartitionAtIndex(i);
                                Message += t[i] + ":";
                            
                               Message += Base64.getEncoder().encodeToString(data);

                                if (i != t.length - 1)
                                    Message += " ";
                            }
                            Message += " ]";
                            out.println(Message);
                        }

                    } else if (parts[0].equals(">data")) {
                        System.out.println(receivedMessage);
                        String parties = Chaine.extraireMots(receivedMessage, 2);
                        savedata(parties, findFile(parts[1]));
                        FileNode concernedFile = this.findFile(parts[1]);
                        File file = new File(concernedFile.getFileName());

                        this.storeFile( parts[1], "received_message/" + file.getName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void savedata(String s, FileNode file) {
        if (s != null) {
            String[] couples = s.split(" ");
            for (String couple : couples) {
                if (couple.contains(":")) {
                    String[] parts = couple.split(":");
                    String key = parts[0];
                    String value = parts[1];
                    byte[] binaryData = Base64.getDecoder().decode(value);
                    file.copyBitsToPartition(Integer.parseInt(key), binaryData);
             }
            this.download = 0;
            }
        }
    }


    //  private void savedata(String s, FileNode file) {
    //     if (s != null) {
    //         String[] couples = s.split(" ");
    //         for (String couple : couples) {
    //             if (couple.contains(":")) {
    //                 String[] parts = couple.split(":");
    //                 String key = parts[0];
    //                 String value = parts[1].replace("-", "");;

    //                 byte[] binaryData = value.getBytes();
    //                 file.copyBitsToPartition(Integer.parseInt(key), binaryData);
    //                 byte [] stored = file.getPartitionAtIndex(Integer.parseInt(key)); 
    //             try{
    //                 System.out.println(
    //                         "verify stored data [" + new String(convertHexToBinary(new String(stored, "ASCII"))) + "]");
    //             }

    //             catch(UnsupportedEncodingException e){

    //             }
    //         }
    //         this.download = 0;
    //     }
    // }
    // }



    public static byte[] convertHexToBinary(String hexContent) {
        int len = hexContent.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) { // Modifier la condition de boucle
            data[i / 2] = (byte) ((Character.digit(hexContent.charAt(i), 16) << 4)
                    + Character.digit(hexContent.charAt(i + 1), 16));
        }
        return data;
    }


    public void sendMessageToPair(String message, PairTarget target) {
        try {
            // connexion au pair spécifié
            Socket socket = new Socket(target.address, target.port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            outToPairs.add(out);
            out.println(message);
            connectionSockets.add(socket);
            new Thread(() -> handleClientCommunication(socket)).start();
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

    public void close() {
        try {
            for (Socket socket : clientSockets) {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
            for (PrintWriter out : outToPairs) {
                if (out != null) {
                    out.close();
                }
            }
            // fermer les connexions aux pairs
            for (Socket socket : connectionSockets) {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
            // fermer le serveur
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (inToThis != null) {
                for (BufferedReader in : inToThis) {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // méthode pour obtenir le buffermap du fichier à partir de sa clé
    public String getFileBufferMap(String fileKey) {
        FileNode file = findFile(fileKey);
        if (file != null) {
            StringBuilder bufferMapBuilder = new StringBuilder();
            HashMap<Integer, Boolean> bufferMap = file.getBufferMap();

            for (int i = 0; i < file.getBufferMapLength(); i++) {
                bufferMapBuilder.append(bufferMap.get(i) ? "1" : "0");
            }
            return bufferMapBuilder.toString();
        } else {
            return ""; 
        }
    }

    public FileNode findFile(String fileKey) {
        FileNode currentFile = fileListHead;
        while (currentFile != null) {
            if (currentFile.getFileKey().equals(fileKey)) {
                return currentFile; 
            }
            currentFile = currentFile.getNextFile(); // Passer au prochain nœud
        }
        return null; // Retourner null si le fichier n'est pas trouvé
    }

    // cette méthode sert à scanner un dossier pour un seul pair 
    //et de créer une liste de FileNode de ces fichiers
    // Pourtant on stcokent pas tous les pièces du fichier 
    // et on stocke les pièces dont l indice == 0 mod partition_copy_index
    // Comme cela les pour notre demonstration les differents pairs auront pas tous les pièces
    // du meme fichier
    public FileNode scanDirectory(String path, int part_size, boolean copy_all_pieces) {
        File directory = new File(path);

        // Vérifier si le chemin spécifié est un répertoire
        if (!directory.isDirectory()) {
            System.out.println("Le chemin spécifié n'est pas un répertoire.");
            return null;
        }

        // Obtenir la liste des fichiers dans le répertoire
        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("Erreur lors de la récupération de la liste des fichiers.");
            return null;
        }
        File file_0 = files[0];
        FileNode fileHead = null;
        if (file_0.isFile()) {
            String file_name = new File(path, file_0.getName()).getPath();
            long fileLength = file_0.length();
            try {
                // Calculer la clé de hachage MD5 du fichier
                String md5Hash = calculateMD5(file_0);
                getRandomNumber((int) Math.floor(fileLength / part_size));

                // System.out.println("filepath = " + file_name + "partitions_index = " + partition_copy_index);
                fileHead = new FileNode(file_name, md5Hash, fileLength, part_size, partition_copy_index, false,1);

            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            System.out.println();
        }

        // Parcourir chaque fichier dans le répertoire
        FileNode previous = fileHead;
        for (int i = 1; i < files.length; ++i) {
            File file = files[i];
            if (file.isFile()) {
                String file_name = new File(path, file.getName()).getPath();
                long fileLength = file.length();
                try {
                    // Calculer la clé de hachage MD5 du fichier
                    String md5Hash = calculateMD5(file);
                    getRandomNumber((int) Math.floor(fileLength / part_size));
                    // System.out.println("filepath = " + file_name + "partitions_index = " + partition_copy_index);
                    FileNode tmpFile = new FileNode(file_name, md5Hash, fileLength, part_size, partition_copy_index, false,1);
                    previous.setNextFile(tmpFile);
                    previous = tmpFile;
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                System.out.println();
            }
        }
        return fileHead;
    }

    // generer un e clé de hachage suivant l algorithme md5
    private String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        Path filePath = file.toPath();

        byte[] data = Files.readAllBytes(filePath);
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

}