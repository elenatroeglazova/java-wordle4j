package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter logWriter;

    public WordleDictionaryLoader(PrintWriter writer) {
        logWriter = writer;
    }

    public WordleDictionary load() {
        WordleDictionary dictionary = new WordleDictionary();
        try(BufferedReader reader = new BufferedReader(new FileReader("words_ru.txt", UTF_8))) {
            List<String> loadedWords = new ArrayList<>();

            while (reader.ready()) {
                loadedWords.add(reader.readLine());
            }

            dictionary.addAll(loadedWords);
            dictionary.prepare();
        } catch (IOException ioException) {
            logWriter.println("Не удалось прочесть файл со словарем\n" + ioException.getMessage());
        }

        return dictionary;
    }
}
