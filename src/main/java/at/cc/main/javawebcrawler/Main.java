package at.cc.main.javawebcrawler;

import at.cc.main.javawebcrawler.exception.InputValidationException;
import at.cc.main.javawebcrawler.util.InputValidator;

public class Main {

    public static void main(String[] args) {

        try {
            InputValidator.validateInput(args);
        } catch (InputValidationException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}