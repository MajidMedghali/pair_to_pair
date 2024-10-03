import java.io.IOException;
import java.util.Arrays;

public class FileNodeTest {
    public static void main(String[] args) {
        // Création d'un objet FileNode pour tester les méthodes
        FileNode fileNode = new FileNode("example.txt", "123456789", 1000, 100, 2, false,1);

        // Affichage du contenu du premier indice
        fileNode.afficherContenuPremierIndice();

        // Lecture du fichier binaire
        try {
            byte[] binaryData = FileNode.readBinaryFile("example.txt");
            System.out.println(
                    "Lecture du fichier binaire réussie. Taille du fichier : " + binaryData.length + " octets");
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier binaire : " + e.getMessage());
        }

        // Copie des données binaires du fichier vers les partitions
        fileNode.copyBinaryFileToPartitions("example.txt", 2, false);
        System.out.println("Copie des données binaires du fichier vers les partitions terminée.");

        // Affichage du contenu du premier indice après la copie des données
        fileNode.afficherContenuPremierIndice();

        // Test de marquage d'une pièce de fichier comme disponible
        fileNode.markPieceAvailable(1);
        System.out.println("La pièce de fichier à l'indice 1 a été marquée comme disponible.");

        // Récupération de la taille du fichier à partir de son chemin
        long fileSize = FileNode.getFileSize("example.txt");
        System.out.println("Taille du fichier example.txt : " + fileSize + " octets");

        // Test de la méthode copyBitsToPartition
        byte[] testData = new byte[fileNode.getPartSize()];
        Arrays.fill(testData, (byte) 'A'); // Remplir avec des 'A' arbitraires
        fileNode.copyBitsToPartition(0, testData);
        System.out.println("Données copiées dans la première partition.");

        // Test de la méthode initializeFilePartitions
        byte[][] partitions = fileNode.initializeFilePartitions(5, 50);
        System.out.println("Taille du tableau de partitions initialisé : " + partitions.length);

        // Test de la méthode getPartitionAtIndex
        byte[] partitionData = fileNode.getPartitionAtIndex(0);
        System.out.println("Données de la première partition : " + Arrays.toString(partitionData));

        // Test de la méthode markPieceAvailable
        fileNode.markPieceAvailable(2);
        System.out.println("La pièce de fichier à l'indice 2 a été marquée comme disponible.");

        // Test des méthodes d'accès aux détails du fichier
        System.out.println("Nom du fichier : " + fileNode.getFileName());
        System.out.println("Clé du fichier : " + fileNode.getFileKey());
        System.out.println("Taille du fichier : " + fileNode.getFileSize());
        System.out.println("Taille de chaque partition : " + fileNode.getPartSize());
        System.out.println("Longueur de la table de buffer : " + fileNode.getBufferMapLength());
        System.out.println("Table de buffer : " + fileNode.getBufferMap());
        System.out.println("Fichier suivant : " + fileNode.getNextFile());
    }
}
