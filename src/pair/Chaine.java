import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Chaine {
    private Chaine(){
        
    }
    public static int[] chaineATableau(String chaine) {
        String[] parties = chaine.split(""); // On sépare la chaîne en caractères individuels
        int[] tableau = new int[parties.length];
        for (int i = 0; i < parties.length; i++) {
            tableau[i] = Integer.parseInt(parties[i]); // On convertit chaque caractère en entier
        }
        return tableau;
    }

     public static int[] piecesTable(String chaine) { 
       String nombreString = chaine.substring(1, chaine.length() - 1).trim();
       if(nombreString.length() == 0){
        return null;
       }
        
       String[] parties = nombreString.split(" ");
       int[] tableau = new int[parties.length];
        for (int i = 0; i < parties.length; i++) {
            tableau[i] = Integer.parseInt(parties[i]); // On convertit chaque caractère en entier
        }
        return tableau;
    }


    // elle prend un tableau des entiers et retourn une liste des indices qui contient 1 
    public static List<Integer> trouverIndicesAvecValeur1(int[] tableau) {
        int longueurTableau = tableau.length;
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < longueurTableau; i++) {
            if (tableau[i] == 1) {
                indices.add(i);
            }
        }
        return indices;
    }

        public static void printList(List<Integer> list) {
            System.out.print("[");
            for (int i = 0; i < list.size(); i++) {
                System.out.print(list.get(i));
                if (i < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }


    public static String extraireSousChaine(String chaine) {
        // Trouver l'index du début de la sous-chaîne
        int debutIndex = chaine.indexOf("[");
        // Trouver l'index de fin de la sous-chaîne
        int finIndex = chaine.indexOf("]");
        
        // Vérifier si les indices sont valides
        if (debutIndex != -1 && finIndex != -1 && finIndex > debutIndex) {
            // Extraire et retourner la sous-chaîne entre les indices trouvés
            return chaine.substring(debutIndex, finIndex + 1);
        } else {
            // Si les indices ne sont pas valides, retourner null ou une valeur par défaut
            return null;
        }
    }
    public static String extrairePortElement(String[] elements, int index) {
            // Supprimer les crochets
            
            // Séparer la chaîne par les espaces pour obtenir les éléments individuels
            
            // Vérifier si la chaîne contient au moins un élément
            if (elements.length >= 1  && index < elements.length) {
                // Séparer le premier élément en IP et port en utilisant ":"
                String[] Port = elements[index].split(":");
                // Vérifier si la séparation a réussi et si le port est présent
                if (Port.length == 2) {
                    // Récupérer et retourner le port (le deuxième élément après le séparateur ":")
                    return Port[1];
        
                }
            }
            
            // Si la chaîne ne correspond pas au format attendu, retourner null ou une valeur par défaut
            return null;
        }

    public static String extraireMots(String chaine, int indice) {
        // Diviser la chaîne en mots
        String[] mots = chaine.split(" ");
        
        // Vérifier si l'indice est valide
        if (indice >= 0 && indice < mots.length) {
            // Créer une sous-chaîne à partir de l'indice spécifié
            StringBuilder resultat = new StringBuilder();
            for (int i = indice; i < mots.length; i++) {
                resultat.append(mots[i]);
                resultat.append(" ");
            }
            // Supprimer l'espace final ajouté en trop
            return resultat.toString().trim();
        } else {
            return "";
        }
    }



     public static Map<String, String> extraireElements(String message) {
        Map<String, String> elements = new HashMap<>();
        Pattern pattern = Pattern.compile("\\$(\\w+):(\\w+)");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String index = matcher.group(1);
            String valeur = matcher.group(2);
            elements.put(index, valeur);
        }
        return elements;
    }

}
