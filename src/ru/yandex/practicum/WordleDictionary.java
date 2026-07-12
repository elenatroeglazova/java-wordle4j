package ru.yandex.practicum;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words = new ArrayList<>();

    public void prepare() {
        words.removeIf(word -> word.length() != 5);
    }

    public void addAll(Collection<String> collection) {
        words.addAll(collection);
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

    public boolean isInDictionary(String word) {
        return words.contains(word);
    }
}
