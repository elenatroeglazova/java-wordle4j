package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.WordIsNotOfSpecifiedLength;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WordleTest {
    private static PrintWriter consoleWriter;
    private WordleGame game;

    @BeforeAll
    public static void setUpAll() {
        consoleWriter = new PrintWriter(System.out, true);
    }

    @BeforeEach
    public void setUp() {
        game = new WordleGame(consoleWriter);
        List<String> words = new ArrayList<>(List.of("мост", "молоко", "цифра", "фокус"));

        game.setDictionary(new WordleDictionary(consoleWriter));
        game.getDictionary().addAll(words);
        game.getDictionary().prepare();
    }

    @Test
    public void guessedWordShouldBeValid() {
        String notSpecifiedLengthWord1 = "каша";
        String notSpecifiedLengthWord2 = "солома";
        String nonCyrillicCharacters1 = "frame";
        String nonCyrillicCharacters2 = "78952";
        String isNotInDictionary = "колос";
        String rightWord = "цифра";

        assertFalse(game.isValid(notSpecifiedLengthWord1), "Отгадываемое слово должно быть длиной в 5 символов");
        assertFalse(game.isValid(notSpecifiedLengthWord2), "Отгадываемое слово должно быть длиной в 5 символов");
        assertFalse(game.isValid(nonCyrillicCharacters1),
                "Отгадываемое слово должно содержать только буквы кирриллицы");
        assertFalse(game.isValid(nonCyrillicCharacters2),
                "Отгадываемое слово должно содержать только буквы кирриллицы");
        assertFalse(game.isValid(isNotInDictionary), "Отгадываемое слово должно быть в словаре");
        assertTrue(game.isValid(rightWord), "Слово выбоано по всем правилам");
    }

    @Test
    public void dictionaryShouldContainOnly5CharactersWord() {
        List<String> rightWords = new ArrayList<>(List.of("цифра", "фокус"));

        assertEquals(rightWords, game.getDictionary().getWords());
    }
}
