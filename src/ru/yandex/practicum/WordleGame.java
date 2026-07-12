package ru.yandex.practicum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private final Map<String, Integer> pickedWords = new LinkedHashMap<>();

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
            String word;

            while (true) {
                word = scanner.nextLine();
                word = WordleDictionary.align(word);

                if (isValid(word)) {
                    break;
                }

                System.out.println("Попробуйте еще раз");
            }

            decrementSteps();

            if (isSuccess(word)) {
                System.out.println("Вы выиграли!");
                return;
            } else {
                System.out.println(compare(word));
                System.out.println("Вы не угадали!");
            }

            savePickedWord(word);
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
        if (word.isBlank()) {
            System.out.println("Слово не введено!");
            return false;
        } else if (!WordleDictionary.isCyrillic(word)) {
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

        return result.toString();
    }

    private void savePickedWord(String word) {
        if (pickedWords.containsKey(word)) {
            Integer count = pickedWords.get(word);
            count++;
            pickedWords.put(word, count);
        } else {
            pickedWords.put(word, 1);
        }
    }
}
