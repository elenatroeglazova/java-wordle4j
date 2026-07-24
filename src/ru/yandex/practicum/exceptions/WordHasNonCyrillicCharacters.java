package ru.yandex.practicum.exceptions;

public class WordHasNonCyrillicCharacters extends RuntimeException {
    public WordHasNonCyrillicCharacters() {
        super();
    }

    public WordHasNonCyrillicCharacters(String message) {
        super(message);
    }
}
