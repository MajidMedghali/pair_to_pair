import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.io.FileOutputStream;
public class FileNode {
    private String fileName; // Nom du fichier
    private String fileKey; // Clé du fichier
    private long fileSize; // Taille du fichier
    private int partSize; // Taille de chaque partie du fichier
    private int bufferMapLength; // Longueur de la table de buffer
    private HashMap<Integer, Boolean> bufferMap; // Table de buffer
    public byte[][] filePartitions; // Tableau de partitions du fichier
    private FileNode nextFile = null; // Pointeur vers le fichier suivant

    public FileNode(String fileName, String fileKey, long fileSize, int partSize, int index, boolean copy_all_pieces, int number) {
        this.fileName = fileName;
        this.fileKey = fileKey;
        this.fileSize = fileSize;
        this.partSize = partSize;
        this.bufferMapLength = (int) Math.floor((double) fileSize / partSize);
        this.bufferMap = new HashMap<>();
        this.filePartitions = new byte[bufferMapLength][partSize];
        initializeBufferMap();
        filePartitions = initializeFilePartitions(bufferMapLength, partSize);
        if(number==0) 
            bufferMapEmpty();
        else
         copyBinaryFileToPartitions(fileName, index, copy_all_pieces);
    }

    public void storeBinaryFile(String output) {
        try {
            File file = new File(output);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Crée tous les répertoires nécessaires

                file.createNewFile(); // Crée le fichier s'il n'existe pas
            }
            
            FileOutputStream fos = new FileOutputStream(file);
            byte[] binaryFile = this.getBinaryFile();
            fos.write(binaryFile);
            fos.close();
            System.out.println("Fichier binaire écrit avec succès !");
            System.out.println("CreatedFIle in " + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public byte[] getBinaryFile() {
        byte[] binaryFile = new byte[(int) this.fileSize];
        int offset = 0;
    
        for (int i = 0; i < this.bufferMapLength; i++) {
            if (this.bufferMap.get(i)  == true) {
                byte[] partition = this.getPartitionAtIndex(i);
                System.out.println(partition.length);
                System.arraycopy(partition, 0, binaryFile, offset, partition.length);
                offset += partition.length;
            }
        }
        return binaryFile;
    }

    public void bufferMapEmpty(){
        for (int i = 0; i < this.bufferMapLength; i++) 
            this.bufferMap.put(i, false);
    }

    public void afficherContenuPremierIndice() {
        if (filePartitions.length > 0 && filePartitions[0].length > 0) {
            System.out.println("Contenu du premier indice :");

            System.out.println(filePartitions[0]);
            System.out.println();
        } else {
            System.out.println("Le tableau est vide ou ne contient pas de sous-tableaux.");
        }
    }

    public void copyBitsToPartition(int index, byte[] sequence) {

        bufferMap.put(index, true);
        // System.arraycopy(sequence, 0, this.filePartitions[index], 0, this.partSize);
        this.filePartitions[index] = sequence;

    }

    public byte[][] initializeFilePartitions(int bufferMapLength, int partSize) {
        byte[][] filePartitions = new byte[bufferMapLength][partSize];
        return filePartitions;
    }

    public byte[] getPartitionAtIndex(int index) {
        if (index < 0 || index >= this.filePartitions.length) {
            System.err.println("Index out of bounds getpartition." + index);
            return null;
        }

        // for (int x = 0; x < 10; x++)
        // System.out.println("les données sont " + this.filePartitions[index][x] + "");
        return this.filePartitions[index];
    }

    public void copyBinaryFileToPartitions(String filePath, int index, boolean copy_all_pieces) {
        try {
            copy_all_pieces = new Random().nextBoolean();
            byte[] noneBytes = "6e6f6f65".getBytes("ASCII");

            // Lire les données binaires du fichier
            byte[] binaryData = readBinaryFile(filePath);
            int numPartitions = (int) Math.floor((double) binaryData.length / partSize);
            // System.out.println("num_partitions in copyBinaryFileToPartitions =" +
            // numPartitions + "partSize" + partSize);
            for (int i = 0; i < numPartitions; i++) {
                int start = i * partSize;
                int end = Math.min((i + 1) * partSize, binaryData.length);
                byte[] partitionData = new byte[end - start];
                System.arraycopy(binaryData, start, partitionData, 0, end - start);

                // if (i == numPartitions - 1 && (end - start) < partSize) {
                //     partitionData = Arrays.copyOf(partitionData, partSize);
                //     Arrays.fill(partitionData, (byte) 'f');
                //     partitionData = Arrays.copyOf(partitionData, partSize);
                // }
                // System.out.println("partitionData.length" + partitionData.length);
                if (!copy_all_pieces) {
                     if (i== 0) {
                        // Copier les données de la partition vers la destination

                        assert (partitionData.length == partSize);
                        copyBitsToPartition(i, partitionData);
                    }
                } 
                else {
                    // Copier les données de la partition vers la destination

                    assert (partitionData.length == partSize);
                    copyBitsToPartition(i, partitionData);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier binaire : " +
                    e.getMessage());
        }
    }

    public static byte[] readBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return data;
    }

    // marquer une pièce de fichier comme disponible dans la table de buffer
    public void markPieceAvailable(int pieceIndex) {
        bufferMap.put(pieceIndex, true);
    }

    // récupérer le nom du fichier
    public String getFileName() {
        return fileName;
    }

    // Méthode pour récupérer la clé du fichier
    public String getFileKey() {
        return fileKey;
    }

    // Méthode pour récupérer la taille du fichier
    public long getFileSize() {
        return fileSize;
    }

    // Méthode pour récupérer la taille de chaque partie du fichier
    public int getPartSize() {
        return partSize;
    }

    // récupérer la longueur de la table de buffer
    public int getBufferMapLength() {
        return bufferMapLength;
    }

    // récupérer la table de buffer
    public HashMap<Integer, Boolean> getBufferMap() {
        return bufferMap;
    }

    // le fichier suivant
    public FileNode getNextFile() {
        return nextFile;
    }

    // Méthode pour définir le fichier suivant
    public void setNextFile(FileNode nextFile) {
        this.nextFile = nextFile;
    }
    

    public void generateRandomContent() {
        int tmp = (int) (Math.random() * 9) + 1;
        for (int i = 0; i < bufferMapLength; i++) {
            if (i % tmp == 0)
                bufferMap.put(i, true);
            else
                bufferMap.put(i, false);
        }

    }

    public void initializeBufferMap() {
        for (int i = 0; i < bufferMapLength; i++) {
            this.bufferMap.put(i, false);
        }

    }

    // Méthode pour obtenir la taille d'un fichier à partir de son chemin
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.length();
    }

    public void addFileToEnd(FileNode newNode) {
        

        
            // Parcourir jusqu'à la dernière node
            FileNode current = this;
            while (current.getNextFile() != null) {
                current = current.getNextFile();
            }
            // Attribuer le nouveau nœud à la dernière node
            current.setNextFile(newNode);
        
    }

    public String getAllFileKeys() {
        FileNode currentFile=this;
        String fileKeys = "";
        while (currentFile != null) {
            fileKeys += currentFile.fileKey + " ";
            currentFile = currentFile.nextFile;
        }
        return fileKeys.trim();
    }

}
