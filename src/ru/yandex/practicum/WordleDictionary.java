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

    private static final int GAME_WORD_LENGTH = 5;

    public WordleDictionary(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void prepare() {
        words.removeIf(word -> word.length() != GAME_WORD_LENGTH);
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

    public static void removeWordsWithMissingChars(Set<Character> missingCharacters, List<String> matches) {
        if (missingCharacters == null || missingCharacters.isEmpty()) {
            return;
        }

        for (Character character : missingCharacters) {
            matches.removeIf(word -> word.contains(character.toString()));
        }
    }

    public static void removeWordsWithoutMatchingChars(List<Character> exactMatches, List<String> matches) {
        if (exactMatches == null || exactMatches.isEmpty()) {
            return;
        }

        for (int i = 0; i < exactMatches.size(); i++) {
            Character character = exactMatches.get(i);
            if (character == null) {
                continue;
            }

            final int index = i;
            matches.removeIf(word -> !word.substring(index, index + 1).equals(character.toString()));
        }
    }

    public static void removeWordsWithoutPresentChars(List<Character> presentCharacters, List<String> matches) {
        if (presentCharacters == null || presentCharacters.isEmpty()) {
            return;
        }

        for (int i = 0; i < presentCharacters.size(); i++) {
            Character character = presentCharacters.get(i);
            if (character == null) {
                continue;
            }

            final int index = i;
            matches.removeIf(word -> !word.contains(character.toString()));
            matches.removeIf(word -> word.substring(index, index + 1).equals(character.toString()));
        }
    }

    public boolean isInDictionary(String word) {
        return words.contains(word);
    }
}
