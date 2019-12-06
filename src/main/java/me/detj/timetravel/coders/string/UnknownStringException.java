package me.detj.timetravel.coders.string;

public class UnknownStringException extends Exception {

    public UnknownStringException(String message) {
        super(message + " is not in the dictionary!");
    }
}
