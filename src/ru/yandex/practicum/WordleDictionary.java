package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final PrintWriter logWriter;

    private final List<String> words = new ArrayList<>();

    public WordleDictionary(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void prepare() {
        words.removeIf(word -> word.length() != 5);
        logWriter.println("Словарь отформатирован и содержит только пятибуквенные слова");
        logWriter.println("Размер словаря на данный момент: " + words.size());
    }

    public void addAll(Collection<String> collection) {
        words.addAll(collection);
        logWriter.println("В словарь добавлены новые слова");
    }

    public List<String> getWords() {
        return this.words;
    }

    public String get() {
        Random random = new Random();
        int index = random.nextInt(words.size());
        String word = words.get(index);
        return align(word);
    }

    public static String align(String word) {
        return word.replace("ё", "е").toLowerCase().trim();
    }

    public static boolean isCyrillic(String word) {
        String cyrillicLetters = "абвгдежзийклмнопрстуфхцчшщьыъэюя";
        char[] letters = word.toCharArray();

        for (char letter : letters) {
            if (!cyrillicLetters.contains(String.valueOf(letter))) {
                return false;
            }
        }

        return true;
    }

    public List<String> getWordsByLetters(Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> guessedLetters,
                                          List<String> matches) {
        if (matches.isEmpty()) {
            matches = new ArrayList<>(words);
        }

        if (!guessedLetters.isEmpty()) {
            for (Map.Entry<String, LinkedHashMap<String, LinkedHashSet<Integer>>> entry : guessedLetters.entrySet()) {
                String result = entry.getKey();
                LinkedHashMap<String, LinkedHashSet<Integer>> letters = entry.getValue();

                for (Map.Entry<String, LinkedHashSet<Integer>> en : letters.entrySet()) {
                    switch (result) {
                        case "-" -> matches.removeIf(word -> word.contains(en.getKey()));
                        case "+" -> {
                            String letter = en.getKey();
                            matches.removeIf(word -> !word.contains(letter));
                            Set<Integer> indexesSet = en.getValue();
                            for (int i : indexesSet) {
                                matches.removeIf(word -> !word.substring(i, i + 1).equals(letter));
                            }
                        }
                        case "^" -> {
                            String letter = en.getKey();
                            matches.removeIf(word -> !word.contains(letter));
                            Set<Integer> indexesSet = en.getValue();
                            for (int i : indexesSet) {
                                matches.removeIf(word -> word.substring(i, i + 1).equals(letter));
                            }
                        }
                    }
                }
            }
        }

        return matches;
    }

    public boolean isInDictionary(String word) {
        return words.contains(word);
    }
}
