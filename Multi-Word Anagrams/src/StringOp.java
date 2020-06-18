import java.util.*;
import java.io.*;

public class StringOp {

    // Confirmed works
    //////////////////////////////////////// Test 3: Combo of Sub-Dictionary ////////////////////////////////////////

    private TreeSet<String> words = new TreeSet<>(); // subdictionary of input string

    private String word = "raziber";
    private int MAX_LENGTH = word.length();
    private List<String> anagrams = new ArrayList<>();

    public void setCombination() {
        char[] r = this.word.toCharArray();
        Arrays.sort(r);
        String ref = new String(r);
        System.out.println(ref);
        String[] subDictionary = new String[this.words.size()];
        int[] count = new int[this.words.size()];
        Iterator<String> it = this.words.iterator();
        int i = 0;
        while (it.hasNext()) {
            subDictionary[i] = it.next();
            i++;
        }
        Arrays.fill(count, 1);
        List<String> combination = new ArrayList<String>();
        setCombination(ref, subDictionary, count, 0, combination);
        System.out.println(anagrams.size());
    }
    private void setCombination(String ref, String[] subDictionary, int[] count, int pos, List<String> combination) {
        if (countLetters(combination) <= MAX_LENGTH) {
            if (countLetters(combination) == MAX_LENGTH) {
                StringBuilder compare = new StringBuilder();
                for (String word : combination) 
                    compare.append(word);
                char[] c = compare.toString().toCharArray();
                Arrays.sort(c);
                String match = new String(c);
                if (ref.equals(match)) {
                    if (combination.size() > 1)
                        multiPermutation(combination);
                    else {
                        this.anagrams.add(list2string(combination));
                        System.out.println(list2string(combination));
                    }
                }
                    
            }
            for (int i = pos; i < count.length; i++) {
                if (count[i] == 0)
                    continue;
                combination.add(subDictionary[i]);
                count[i]--;
                setCombination(ref, subDictionary, count, i, combination);
                combination.remove(combination.size() - 1);
                count[i]++;
            }
        }
    }

    // Confirmed works
    //////////////////////////////////////// Test 2: Creating Sub-Dictionary ////////////////////////////////////////

    private static String dictPath = "dictionary.txt";
    private TrieSTNew<String> dictionary = new TrieSTNew<>();
    private static int MIN_WORD_SIZE = 1;

    public void combination(char[] input) {
        Map<Character, Integer> countMap = new TreeMap<>();
        for (char ch : input) {
            countMap.compute(ch, (key, val) -> { // key: letters
                return val == null ? 1 : val + 1; // value: letter count
            });
        }
        char[] str = new char[countMap.size()]; // letters
        int[] count = new int[countMap.size()]; // letter count
        int index = 0;
        for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
            str[index] = entry.getKey();
            count[index] = entry.getValue();
            index++;
        }
        StringBuilder output = new StringBuilder(); // combination
        combination(str, count, 0, output);
        print(words);
    }
    private void combination(char input[], int count[], int pos, StringBuilder output) {
        String c = new String(output);
        // System.out.println(c);
        permutation(c);
        for (int i = pos; i < input.length; i++) {
            if (count[i] == 0) {
                continue;
            }
            output.append(input[i]); // build combination
            count[i]--;
            combination(input, count, i, output);
            output.setLength(output.length() - 1);
            count[i]++;
        }
    }

    public void permutation(String word) {
        char[] letters = word.toCharArray();
        Arrays.sort(letters);
        String sortedInput = new String(letters);
        permutation("", sortedInput);
    }
    private void permutation(String prefix, String suffix) {
        if (this.dictionary.searchPrefix(prefix) != 0) { // prune non-prefix recursions
            int n = suffix.length();
            if (n == 0 && this.dictionary.searchPrefix(prefix) >= 2 && prefix.length() >= MIN_WORD_SIZE)
                this.words.add(prefix); // adds any words to sub-dictionary (duplicates removed via TreeSet)
            else {
                for (int i = 0; i < n; i++) {
                    if (i > 0 && suffix.charAt(i) == suffix.charAt(i - 1))
                        continue;
                    permutation(prefix + suffix.charAt(i), suffix.substring(0, i) + suffix.substring(i + 1, n));
                }
            }
        }
    }

    private void loadDictionary() throws IOException {
        String s;
        File dictFile = new File(dictPath);
        Scanner scanner = new Scanner(dictFile);
        System.out.println("Loading dictionary...");
        while (scanner.hasNext()) {
            s = scanner.nextLine();
            this.dictionary.put(s, s);
        }
        System.out.println("Dictionary loaded.");
        scanner.close();
    }
    private void print(TreeSet<String> words) {
        System.out.println("Printing sub-dictionary: ");
        for (String s : words) 
            System.out.println(s);
    }

    // Confirmed works
    //////////////////////////////////////////////// Test 1: COMBO STRINGS ////////////////////////////////////////////////

    private String[] sd = {"a", "fish", "can", "be", "tasty"};
    public void listCombination() {
        int[] count = new int[this.sd.length];
        Arrays.fill(count, 1);
        List<String> anagram = new ArrayList<>();
        listCombination(count, 0, anagram);
    }
    private void listCombination(int[] count, int pos, List<String> anagram) {
        System.out.println(list2string(anagram) + ": " +  countLetters(anagram));
        for (int i = pos; i < count.length; i++) { 
            if (count[i] == 0) 
                continue;
            anagram.add(this.sd[i]);
            count[i]--;
            listCombination(count, i, anagram);
            anagram.remove(anagram.size() - 1);
            count[i]++;
        }
    }

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

    // Confirmed works
    /////////////////////////////////// Test 4: Permutation of Multi-Anagrams ///////////////////////////////////
    /** 
     * Note: I likely would have pursued a different method if I checked the guidelines that permutations
     * of multi-string anagrams are considered unique
     */

    public void multiPermutation(List<String> words) {
        multiPermutation(words, 0);
    }

    private void multiPermutation(List<String> words, int pos) {
        if (pos == words.size() - 1) {
            List<String> permutation = new ArrayList<>();
            for (String word : words) {
                permutation.add(word);
            }
            System.out.println(list2string(permutation));
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

    public static void main(String[] args) throws IOException {
        StringOp testSD = new StringOp(); 
        //testSD.listCombination(); // test 1: string combinations, list2string (create anagram), countLetters (for pruning and combinations)
        testSD.loadDictionary(); 
        testSD.combination("and an addenda".toCharArray()); // test 2: sub-dictionary creation
        testSD.setCombination(); // test 3 & 4: create combinations of equal length words, permute combinations of anagrams
    }
}