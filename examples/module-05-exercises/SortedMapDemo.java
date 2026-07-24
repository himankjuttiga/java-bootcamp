import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SortedMapDemo {
    public static void main(String[] args) {
        // TODO: declare Map<String, Double> prices as new HashMap<>()
        Map<String, Double> prices = new HashMap<>();

        // TODO: put three title → price mappings
        //   "The Hobbit" -> 12.99, "Dune" -> 15.50, "Annihilation" -> 9.99
        prices.put("The Hobbit", 12.99);
        prices.put("Dune", 15.50);
        prices.put("Annihilation", 9.99);

        // TODO: print HashMap key order (unspecified — may vary between runs)
        System.out.println("HashMap order: " + prices.keySet());

        // TODO: create TreeMap<String, Double> sortedPrices from prices
        Map<String, Double> sortedPrices = new TreeMap<>(prices);
        System.out.println("TreeMap order: " + sortedPrices.keySet());

        // TODO: declare TreeMap<String, Double> tree from prices
        //   (TreeMap type needed for firstKey / lastKey)
        TreeMap<String, Double> tree = new TreeMap<>(prices);
        System.out.println("First title: " + tree.firstKey());
        System.out.println("Last title: " + tree.lastKey());
    }
}