package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    private final Scanner scanner = new Scanner(System.in);

    private final PrintWriter logWriter;

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private static final int GAME_WORD_LENGTH = 5;

    Set<Character> missingCharacters;

    List<Character> presentCharacters;

    List<Character> exactMatches;

    List<String> matchingWords = new ArrayList<>();

    public WordleGame(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setSteps(int steps) {
        this.steps = steps;
        logWriter.println("\nУстановлено общее количество шагов: " + this.steps);
    }

    public void setDictionary(WordleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public WordleDictionary getDictionary() {
        return this.dictionary;
    }

    public List<String> getMatchingWords() {
        return matchingWords;
    }

    public void play() {
        pickSecretWord();

        while (steps != 0) {
            System.out.println("Загадайте слово из 5 букв");
            String word = scanner.nextLine();

            while (true) {
                word = WordleDictionary.align(word);

                if (isValid(word)) {
                    break;
                }

                System.out.println("Попробуйте еще раз:");
                word = scanner.nextLine();
            }

            if (word.isBlank()) {
                System.out.println(prompt());
                continue;
            }

            decrementSteps();

            if (isSuccess(word)) {
                System.out.println("Это верное слово.");
                System.out.println("Вы выиграли!");
                logWriter.println("\nИгроком введено загаданное слово - " + word.toUpperCase(Locale.ROOT));
                logWriter.println("");
                logWriter.println("*".repeat(80));
                logWriter.println("*".repeat(80));
                logWriter.println("Конец игры!".toUpperCase(Locale.ROOT));
                return;
            } else {
                System.out.println(compare(word));
                System.out.println("Вы не угадали!");
            }
        }

        System.out.println("Верное слово: " + answer);
        System.out.println("Вы проиграли! Конец игры!");
    }

    private void decrementSteps() {
        steps--;
        logWriter.println("\nЧисло шагов в игре уменьшилось");
        logWriter.println("Оставшиеся шаги: " + steps);
    }

    private void pickSecretWord() {
        answer = dictionary.get();
        logWriter.println("\nБыло загадано слово: " + answer.toUpperCase(Locale.ROOT));
    }

    public boolean isValid(String word) {
        if (word.isEmpty()) {
            return true;
        }

        if (!WordleDictionary.isCyrillic(word)) {
            logWriter.println("");
            logWriter.println("Некорректный ввод игрока. Слово содержит некиррилические буквы");
            System.out.println("Слово должно содержать только кирриллицу!");
            return false;
        } else if (word.length() != GAME_WORD_LENGTH) {
            logWriter.println("");
            logWriter.println("Некорректный ввод игрока. Слово не соответствует заданной длине");
            System.out.println("Слово должно быть из 5 букв!");
            return false;
        } else if (!dictionary.isInDictionary(word)) {
            logWriter.println("");
            logWriter.println("Некорректный ввод игрока. Слово не найдено в словаре");
            System.out.println("Такого слова нет в словаре");
            return false;
        }

        return true;
    }

    private boolean isSuccess(String word) {
        return answer.equals(word);
    }

    private String compare(String word) {
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= word.length(); i++) {
            String pickedWordChar = word.substring(i - 1, i);
            String answerChar = answer.substring(i - 1, i);
            if (pickedWordChar.equals(answerChar)) {
                result.append("+");
            } else if (answer.contains(pickedWordChar)) {
                result.append("^");
            } else {
                result.append("-");
            }
        }

        savePickedWord(word, result.toString());
        logWriter.println("\nВариант игрока: " + word.toUpperCase(Locale.ROOT));
        logWriter.println("Результат сравнения с загаданным словом: " + result);

        return result.toString();
    }

    public void savePickedWord(String word, String comparisonResult) {
        missingCharacters = new HashSet<>();
        presentCharacters = new ArrayList<>(Collections.nCopies(GAME_WORD_LENGTH, null));
        exactMatches = new ArrayList<>(Collections.nCopies(GAME_WORD_LENGTH, null));

        for (int i = 0; i < word.length(); i++) {
            Character pickedWordChar = word.charAt(i);
            char resultChar = comparisonResult.charAt(i);

            switch (resultChar) {
                case '+' -> exactMatches.set(i, pickedWordChar);
                case '-' -> missingCharacters.add(pickedWordChar);
                case '^' -> presentCharacters.set(i, pickedWordChar);
            }
        }

        getMatchedWords();
    }

    private void getMatchedWords() {
        if (matchingWords.isEmpty()) {
            matchingWords = new ArrayList<>(dictionary.getWords());
        }

        WordleDictionary.removeWordsWithMissingChars(missingCharacters, matchingWords);
        WordleDictionary.removeWordsWithoutMatchingChars(exactMatches, matchingWords);
        WordleDictionary.removeWordsWithoutPresentChars(presentCharacters, matchingWords);

        logWriter.println("\nПроизошел отбор слов с учетом сделанных попыток отгадывания");
        logWriter.println("Количество слов, подходящих под условия отбора: " + matchingWords.size());
    }

    public String prompt() {
        if (matchingWords.isEmpty()) {
            getMatchedWords();
        }

        logWriter.println("\nБыла запрошена подсказка игроком");
        if (matchingWords.size() == 1) {
            logWriter.println("Из подсказок осталось только загаданное слово");
            return answer;
        } else {
            List<String> matchingWordsCopy = new ArrayList<>(matchingWords);
            matchingWordsCopy.remove(answer);
            Random random = new Random();
            int index = random.nextInt(matchingWordsCopy.size());
            String prompt = matchingWordsCopy.get(index);
            matchingWords.remove(prompt);
            logWriter.println("Пользователю выдана подсказка: " + prompt.toUpperCase(Locale.ROOT));
            return prompt;
        }
    }
}
