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

    Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> guessedLetters = new LinkedHashMap<>();

    Map<String, LinkedHashMap<String, LinkedHashSet<Integer>>> savedGuessedLetters = new LinkedHashMap<>();

    List<String> matchingWords = new ArrayList<>();

    public WordleGame(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setDictionary(WordleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void play() {
        pickSecretWord();

        while (steps != 0) {
            System.out.println("Загадайте слово из 5 букв");
            String word = scanner.nextLine();

            if (word.isBlank()) {
                prompt();
                continue;
            }

            while (true) {
                word = WordleDictionary.align(word);

                if (isValid(word)) {
                    break;
                }

                System.out.println("Попробуйте еще раз:");
                word = scanner.nextLine();
            }

            decrementSteps();

            if (isSuccess(word)) {
                System.out.println("Это верное слово.");
                System.out.println("Вы выиграли!");
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
    }

    private void pickSecretWord() {
        answer = dictionary.get();
    }

    private boolean isValid(String word) {
        if (!WordleDictionary.isCyrillic(word)) {
            System.out.println("Слово должно содержать только кирриллицу!");
            return false;
        } else if (word.length() != 5) {
            System.out.println("Слово должно быть из 5 букв!");
            return false;
        } else if (!dictionary.isInDictionary(word)) {
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

        if (matchingWords.size() == 1) {
            System.out.println(answer);
        } else {
            List<String> matchingWordsCopy = new ArrayList<>(matchingWords);
            matchingWordsCopy.remove(answer);
            Random random = new Random();
            int index = random.nextInt(matchingWordsCopy.size());
            System.out.println(matchingWordsCopy.get(index));
        }
    }
}
