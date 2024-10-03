// import java.util.List;
// import java.util.ArrayList;

// /* In this test I create a instance of pair then I test the functions
// insertFileAtEnd, insertFileAtEnd and findFile*/

// public class PairTest {

// public static void main(String[] args) {

// int theTrackerPort = ConfigParser.extractTrackerPort("../../config.ini");
// String theTrackerAddress =
// ConfigParser.extractTrackerAddress("../../config.ini");
// int partSize = 64;
// // Création d'une instance de Pair pour le test
// Pair pair = new Pair(1234, theTrackerAddress, theTrackerPort,
// "../../generated_folders/folder_1", false,
// partSize);

// // Test de la méthode findFile
// FileNode foundFile = pair.findFile("2334545DFSG3443");
// if (foundFile != null) {
// System.out.println("Fichier trouvé: " + foundFile.getFileName());
// } else {
// System.out.println("Fichier non trouvé.");
// }

// // Test de la méthode scanDirectory
// FileNode fileListHead = pair.scanDirectory("./generated_folders/folder_1",
// partSize, false);
// if (fileListHead != null) {
// System.out.println("Liste des fichiers scannée avec succès.");
// } else {
// System.out.println("Erreur lors du scan du répertoire.");
// }
// // Test de la méthode startServer (ce test continuera indéfiniment jusqu'à ce
// // qu'il soit interrompu)
// pair.startServer(8080);

// // Test de la méthode getFileHead
// FileNode head = pair.getFileHead();
// if (head != null) {
// System.out.println("Tête de fichier: " + head.getFileName());
// } else {
// System.out.println("Aucun fichier trouvé.");
// }

// // Test de la méthode getFileBufferMap
// String fileKey = "your_file_key_here";
// String bufferMap = pair.getFileBufferMap(fileKey);
// if (!bufferMap.isEmpty()) {
// System.out.println("Buffer map du fichier " + fileKey + " : " + bufferMap);
// } else {
// System.out.println("Aucune carte tampon trouvée pour le fichier " + fileKey);
// }

// // Fermeture de la connexion et des ressources à la fin du test
// pair.close();
// }
// }
