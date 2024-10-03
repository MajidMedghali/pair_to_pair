import java.util.Map;


public class ChaineTest {
    public static void main(String[] args) {
        // Test de la méthode chaineATableau
        String chaine1 = "123451";
        int[] tableau1 = Chaine.chaineATableau(chaine1);
        System.out.print("Tableau 1 : ");
        Chaine.printList(Chaine.trouverIndicesAvecValeur1(tableau1)); // Trouver les indices où la valeur est égale à 1
        
        // Test de la méthode piecesTable
        String chaine2 = "[1 2 3 4 5 1]";
        int[] tableau2 = Chaine.piecesTable(chaine2);
        System.out.print("Tableau 2 : ");
        Chaine.printList(Chaine.trouverIndicesAvecValeur1(tableau2)); // Trouver les indices où la valeur est égale à 1
        
        // Test de la méthode extraireSousChaine
        String chaine3 = "Ceci est [une sous-chaîne] à extraire.";
        String sousChaine = Chaine.extraireSousChaine(chaine3);
        System.out.println("Sous-chaîne extraite : " + sousChaine);
        
        // Test de la méthode extrairePortElement
        String[] elements = {"127.0.0.1:8080", "localhost:8081"};
        int index = 1;
        String port = Chaine.extrairePortElement(elements, index);
        System.out.println("Port extrait : " + port);
        
        // Test de la méthode extraireMots
        String chaine4 = "Ceci est une phrase de test.";
        int indice = 2;
        String motsExtraits = Chaine.extraireMots(chaine4, indice);
        System.out.println("Mots extraits : " + motsExtraits);
        
        // Test de la méthode extraireElements
        String message = "$key1:value1 $key2:value2 $key3:value3";
        Map<String, String> elementsExtraits = Chaine.extraireElements(message);
        System.out.println("Éléments extraits : " + elementsExtraits);
    }
}

