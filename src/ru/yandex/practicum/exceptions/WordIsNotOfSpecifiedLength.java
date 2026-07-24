package ru.yandex.practicum.exceptions;

public class WordIsNotOfSpecifiedLength extends RuntimeException {
    public WordIsNotOfSpecifiedLength() {
        super();
    }

    public WordIsNotOfSpecifiedLength(String message) {
        super(message);
    }
}
