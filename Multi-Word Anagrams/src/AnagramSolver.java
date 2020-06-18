import java.util.*;
import java.io.*;

/**
 * CS 1501: Summer 2019
 * @author Conrad Li 
 * Finds multi-word anagrams for given input strings in input files
 */
public class AnagramSolver {

    private static String dictPath = "dictionary.txt";
    private TrieSTNew<String> dictionary = new TrieSTNew<>();
    private static int MIN_WORD_SIZE = 1;
    private List<String> inputs = new ArrayList<>(); // inputs from file
    private TreeSet<String> words = new TreeSet<>(); // subdictionary of input string
    private List<String> anagrams = new ArrayList<>(); // anagrams of input string
    private List<List<String>> output = new ArrayList<>(); // set of anagram sets per input string

    public void multianagram(String input) {
        this.words = new TreeSet<>(); // reset subdictionary
        this.anagrams = new ArrayList<>(); // reset anagrams list
        String temp = input.replace(" ", ""); 
        combination(temp.toCharArray()); // populate subdictionary with input
        findAnagrams(temp); 
        Collections.sort(this.anagrams, new MyComparator());
        this.output.add(this.anagrams); 
    }

    /********************************************************************/

    /**FINDS ANAGRAMS
     * Sorts input by characters
     * Makes combination of all words in subdictionary < input length
     * Compares characters between input and combinations of equal length
     * @param word reference (input) string 
     */
    public void findAnagrams(String word) {
        char[] r = word.toCharArray();
        Arrays.sort(r); // sort input string
        String ref = new String(r); 
        int MAX_LENGTH = ref.length(); // character count of anagram

        String[] subDictionary = new String[this.words.size()];
        Iterator<String> it = this.words.iterator();
        int i = 0;
        while (it.hasNext()) {
            subDictionary[i] = it.next(); // instantiate subdictionary as list
            // System.out.println(subDictionary[i]);
            i++;
        }

        int[] count = new int[this.words.size()];
        Arrays.fill(count, 1); // ensures subdictionary words can only be used once
        List<String> combination = new ArrayList<String>();
        findAnagrams(ref, subDictionary, count, 0, combination, MAX_LENGTH);
    }

    private void findAnagrams(String ref, String[] subDictionary, int[] count, int pos, List<String> combination, int MAX_LENGTH) {
        if (countLetters(combination) <= MAX_LENGTH) { // prune combinations too long
            if (countLetters(combination) == MAX_LENGTH) { // look at combinations = anagram length
                StringBuilder compare = new StringBuilder();
                for (String word : combination)
                    compare.append(word); 
                char[] c = compare.toString().replace(" ", "").toCharArray();  
                Arrays.sort(c); 
                String match = new String(c); // sorted combination sorted by characters 
                if (ref.equals(match)) { // anagram found if combination has same n of k characters as input
                    if (combination.size() > 1) 
                        multiPermutation(combination); // add permutations of multi-word anagrams
                    else 
                        this.anagrams.add(list2string(combination)); // add single-word anagram 
                }
            }
            // builds combinations of subdictionary words using a list
            for (int i = pos; i < count.length; i++) {
                if (count[i] == 0)
                    continue;
                combination.add(subDictionary[i]); 
                count[i]--;
                findAnagrams(ref, subDictionary, count, i, combination, MAX_LENGTH);
                combination.remove(combination.size() - 1);
                count[i]++;
            }
        }
    }

    /**MAKES PERMUTATIONS OF MULTIWORD ANAGRAM COMBINATIONS
     * @param words words contained in an anagram
     */
    public void multiPermutation(List<String> words) {
        multiPermutation(words, 0);
    }
    private void multiPermutation(List<String> words, int pos) {
        if (pos == words.size() - 1) { 
            List<String> permutation = new ArrayList<>(); 
            for (String word : words) 
                permutation.add(word); 
            this.anagrams.add(list2string(permutation));
        }
        for (int i = pos; i < words.size(); i++) {
            swap(words, i, pos);
            multiPermutation(words, pos + 1);
            swap(words, i, pos);
        }
    }
    private void swap(List<String> words, int i, int j) {
        String temp = words.get(i);
        words.set(i, words.get(j));
        words.set(j, temp);
    }

    /********************************************************************/

    /**CREATE SUBDICTIONARY
     * Makes all combinations of input word
     * Makes all permutations of combinations
     * Creates subdictionary from permutations that match dictionary words
     * @param input input word
     */  
    public void combination(char[] input) {
        Map<Character, Integer> countMap = new TreeMap<>();
        for (char ch : input) {
            countMap.compute(ch, (key, val) -> {  // key: letters
                return val == null ? 1 : val + 1; // value: letter count
            });
        }
        char[] str = new char[countMap.size()]; // letters
        int[] count = new int[countMap.size()]; // letter count
        int index = 0;
        for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
            str[index] = entry.getKey(); // instantiate letters
            count[index] = entry.getValue(); // instantiate count
            index++;
        }
        StringBuilder output = new StringBuilder(); // combination
        combination(str, count, 0, output);
    }
    private void combination(char input[], int count[], int pos, StringBuilder output) {
        String c = new String(output);
        permutation(c); // finds words based on letter combinations
        for (int i = pos; i < input.length; i++) { // build combinations
            if (count[i] == 0) {
                continue;
            }
            output.append(input[i]); 
            count[i]--;
            combination(input, count, i, output);
            output.setLength(output.length() - 1); 
            count[i]++; 
        }
    }

    public void permutation(String word) {
        char[] letters = word.toCharArray();
        Arrays.sort(letters); // sorting removes duplicates later on
        String sortedInput = new String(letters); 
        permutation("", sortedInput);
    }
    private void permutation(String prefix, String suffix) {
        if (this.dictionary.searchPrefix(prefix) != 0) { // PRUNE: non-prefix recursive calls
            int n = suffix.length();
            if (n == 0 && this. dictionary.searchPrefix(prefix) >= 2 && prefix.length() >= MIN_WORD_SIZE)
                this.words.add(prefix); // adds words to sub-dictionary (duplicates removed via TreeSet)
            else {
                for (int i = 0; i < n; i++) {
                    if (i > 0 && suffix.charAt(i) == suffix.charAt(i - 1)) continue; // ignores duplicates
                    permutation(prefix + suffix.charAt(i), suffix.substring(0, i) + suffix.substring(i + 1, n));
                }
            }
        }
    }
    
    /**Loads dictionary into TrieSTNew structure */
    private void loadDictionary() throws IOException {
        String s;
        File dictFile = new File(dictPath);
        Scanner scanner = new Scanner(dictFile);
        System.out.println("\nLoading dictionary...");
        while (scanner.hasNext()) {
            s = scanner.nextLine();
            this.dictionary.put(s, s);
        }
        System.out.println("Dictionary loaded\n");
        scanner.close();
    }

    /********************************************************************/

    public String list2string(List<String> words) {
        StringBuilder str = new StringBuilder();
        for (String word : words)
            str.append(word + " ");
        return new String(str);
    }

    public int countLetters(List<String> words) {
        int sum = 0;
        for (String word : words) {
            sum += word.length();
        }
        return sum;
    }

    private int countSpaces(String s) {
        int n = 0;
        for (char c : s.toCharArray()) {
            if (c == ' ')
                n++;
        }
        return n;
    }

    private List<List<Integer>> anagramCount = new ArrayList<>();
    private void countAnagrams() {
        for (List<String> set : this.output) {
            List<Integer> counter = new ArrayList<>();
            String last = set.get(set.size()-1);
            for (int i = 0; i < countSpaces(last) + 1; i++)
                counter.add(0);
            int n;
            for (String anagram : set) {
                n = countSpaces(anagram);
                counter.set(n, counter.get(n) + 1);
            }
            anagramCount.add(counter);
        }
    }

    private void loadInputs(String inputPath) throws IOException {
        File inputFile = new File(inputPath);
        Scanner scanner = new Scanner(inputFile);
        System.out.println("Loading inputs...");
        while (scanner.hasNext()) 
            this.inputs.add(scanner.nextLine());
        System.out.println("Inputs loaded\n");
        scanner.close();
    }

    private void output2file(String outputPath) throws IOException {
        FileWriter writer;
        try {
            File outputFile = new File(outputPath);
            if (!outputFile.exists())
                outputFile.createNewFile();
            writer = new FileWriter(outputFile);
            BufferedWriter out = new BufferedWriter(writer);
            int i = 0;
            for (List<String> set : this.output) {
                out.write(set.size() + " anagrams found for " + this.inputs.get(i).toUpperCase() + ":");
                out.newLine();
                List<Integer> count = this.anagramCount.get(i);
                for (int k = 1; k < count.size(); k++) {
                    out.write(count.get(k) + " anagrams of size: " + k);
                    out.newLine();
                }
                for (String anagram : set) {
                    out.write(anagram);
                    out.newLine();
                }
                out.newLine();
                i++;
            }
            out.close();
            System.out.println("Output saved to: " + outputPath);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        char c = 'y';
        while (c == 'y') {
            System.out.print("Enter input filename: ");
            String inputPath = scanner.nextLine();
            System.out.print("Enter output filename: ");
            String outputPath = scanner.nextLine();

            AnagramSolver p = new AnagramSolver();
            p.loadDictionary();
            p.loadInputs(inputPath);
            for (String word : p.inputs)
                p.multianagram(word);
            p.countAnagrams();
            p.output2file(outputPath);

            System.out.print("\nInput another file [Y/N]: ");
            c = scanner.nextLine().toLowerCase().charAt(0);
            System.out.println();
        }
        scanner.close();
    }

    // Internal class to override compare method to include # of words
    public class MyComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            if (countSpaces(s1) > countSpaces(s2)) { // larger
                return 1;
            } else if (countSpaces(s1) < countSpaces(s2)) { // smaller
                return -1;
            }
            return s1.compareTo(s2); // equal (compares first char)
        }

        private int countSpaces(String s) {
            int n = 0;
            for (char c : s.toCharArray()) {
                if (c == ' ') 
                    n++;
            }
            return n;
        }
    }
}