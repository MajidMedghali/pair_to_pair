import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        // Texte à stocker
        String texte = "Bonjour le monde!";

        // Encodage du texte en bytes avec UTF-8
        byte[] bytesTexte = texte.getBytes(StandardCharsets.UTF_8);

        // Division du texte en parties si nécessaire
        int taillePartition = 10; // Taille de chaque partition
        int nombrePartitions = (int) Math.ceil((double) bytesTexte.length / taillePartition);
        byte[][] filePartitions = new byte[nombrePartitions][];

        // Remplissage des partitions avec les données du texte
        for (int i = 0; i < nombrePartitions; i++) {
            int debutIndex = i * taillePartition;
            int finIndex = Math.min(debutIndex + taillePartition, bytesTexte.length);
            filePartitions[i] = Arrays.copyOfRange(bytesTexte, debutIndex, finIndex);
        }

        // Affichage du contenu de chaque partition
        for (int i = 0; i < nombrePartitions; i++) {
            System.out.println("Partition " + i + ": " + new String(filePartitions[i], StandardCharsets.UTF_8) + " "+ filePartitions[i]);
        }
    }
}
