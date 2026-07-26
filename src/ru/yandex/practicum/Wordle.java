package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {
    private static final int MAX_ATTEMPTS = 6;

    public static void main(String[] args) {
        try {
            Path filePath = Paths.get("log.txt");

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            Path logFile = Files.createFile(filePath);
            try (PrintWriter writer = new PrintWriter(logFile.toFile())) {
                try {
                    writer.println("\nНачинаем игру!\n".toUpperCase(Locale.ROOT));
                    writer.println("*".repeat(80));
                    writer.println("*".repeat(80));
                    WordleDictionaryLoader loader = new WordleDictionaryLoader(writer);
                    WordleDictionary dictionary = loader.load();
                    WordleGame game = new WordleGame(writer);

                    game.setDictionary(dictionary);
                    game.setSteps(MAX_ATTEMPTS);
                    game.play();
                } catch (IOException ioException) {
                    writer.println(ioException.getMessage());
                    ioException.printStackTrace(writer);
                }
            }
        } catch (IOException ioException) {
            System.out.println("Не удалось создать log-файл\n" + ioException.getMessage());
        }
    }
}
