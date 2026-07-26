package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordHasNonCyrillicCharacters;
import ru.yandex.practicum.exceptions.WordIsNotOfSpecifiedLength;
import ru.yandex.practicum.exceptions.WordNotFoundInDictionary;

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

    Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> guessedLetters = new LinkedHashMap<>();

    Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> savedGuessedLetters = new LinkedHashMap<>();

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
                prompt();
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
            WordHasNonCyrillicCharacters exception =
                    new WordHasNonCyrillicCharacters("Слово содержит некиррилические буквы");
            logWriter.println("");
            exception.printStackTrace(logWriter);
            System.out.println("Слово должно содержать только кирриллицу!");
            return false;
        } else if (word.length() != 5) {
            WordIsNotOfSpecifiedLength exception =
                    new WordIsNotOfSpecifiedLength("Слово не соответствует заданной длине");
            logWriter.println("");
            exception.printStackTrace(logWriter);
            System.out.println("Слово должно быть из 5 букв!");
            return false;
        } else if (!dictionary.isInDictionary(word)) {
            WordNotFoundInDictionary exception = new WordNotFoundInDictionary("Слово не найдено в словаре");
            logWriter.println("");
            exception.printStackTrace(logWriter);
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

    private void savePickedWord(String word, String comparisonResult) {
        guessedLetters = new LinkedHashMap<>();
        LinkedHashSet<Integer> resultIndexes;
        LinkedHashMap<String, LinkedHashSet<Integer>> letters;

        for (int i = 1; i <= word.length(); i++) {
            String pickedWordChar = word.substring(i - 1, i);
            String resultChar = comparisonResult.substring(i - 1, i);

            if (guessedLetters.containsKey(resultChar)) {
                letters = guessedLetters.get(resultChar);
                if (letters.containsKey(pickedWordChar)) {
                    if (resultChar.equals("-")) {
                        if (!letters.get(pickedWordChar).isEmpty()) {
                            continue;
                        }
                    }
                    letters.get(pickedWordChar).add(i - 1);
                } else {
                    resultIndexes = new LinkedHashSet<>();
                    resultIndexes.add(i - 1);
                    letters.put(pickedWordChar, resultIndexes);
                }
            } else {
                letters = new LinkedHashMap<>();
                resultIndexes = new LinkedHashSet<>();
                resultIndexes.add(i - 1);
                letters.put(pickedWordChar, resultIndexes);
                guessedLetters.put(resultChar, letters);
            }
        }

        findMatchedWords();
    }

    private void findMatchedWords() {
        saveDifferenceWithOldGuesses();
        matchingWords = dictionary.getWordsByLetters(guessedLetters, matchingWords);
        logWriter.println("\nПроизошел отбор слов с учетом сделанных попыток отгадывания");
        logWriter.println("Количество слов, подходящих под условия отбора: " + matchingWords.size());
    }

    private void saveDifferenceWithOldGuesses() {
        if (savedGuessedLetters.isEmpty()) {
            savedGuessedLetters.putAll(guessedLetters);
            return;
        }

        Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> copyGuessedLetters = new HashMap<>(guessedLetters);

        for (Map.Entry<String, LinkedHashMap<String, LinkedHashSet<Integer>>> entry : savedGuessedLetters.entrySet()) {
            String resultSymbol = entry.getKey();
            LinkedHashMap<String, LinkedHashSet<Integer>> newLetters = guessedLetters.get(resultSymbol);

            if (newLetters == null || newLetters.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, LinkedHashSet<Integer>> oldLetterSet : entry.getValue().entrySet()) {
                String letter = oldLetterSet.getKey();
                LinkedHashSet<Integer> newIndexes = newLetters.get(letter);
                LinkedHashSet<Integer> oldIndexes = oldLetterSet.getValue();

                if (newIndexes == null || newIndexes.isEmpty() || oldIndexes == null || oldIndexes.isEmpty()) {
                    continue;
                }

                if (!newLetters.containsKey(letter)) {
                    continue;
                }

                if (resultSymbol.equals("-")) {
                    newLetters.remove(letter);
                } else {
                    for (Integer index : oldIndexes) {
                        newIndexes.remove(index);
                        if (newIndexes.isEmpty()) {
                            newLetters.remove(letter);
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, LinkedHashMap<String, LinkedHashSet<Integer>>> entry : copyGuessedLetters.entrySet()) {
            savedGuessedLetters.merge(entry.getKey(), entry.getValue(), (val1, val2) -> {
                for (Map.Entry<String, LinkedHashSet<Integer>> en : val1.entrySet()) {
                    val2.merge(en.getKey(), en.getValue(), (v1, v2) -> {
                        v1.addAll(v2);
                        return v1;
                    });
                }
                val1.putAll(val2);
                return val1;
            });
        }
    }

    private void prompt() {
        if (matchingWords.isEmpty()) {
            findMatchedWords();
        }

        logWriter.println("\nБыла запрошена подсказка игроком");
        if (matchingWords.size() == 1) {
            System.out.println(answer);
            logWriter.println("Из подсказок осталось только загаданное слово");
        } else {
            List<String> matchingWordsCopy = new ArrayList<>(matchingWords);
            matchingWordsCopy.remove(answer);
            Random random = new Random();
            int index = random.nextInt(matchingWordsCopy.size());
            String prompt = matchingWordsCopy.get(index);
            logWriter.println("Пользователю выдана подсказка: " + prompt.toUpperCase(Locale.ROOT));
            System.out.println(prompt);
        }
    }
}
