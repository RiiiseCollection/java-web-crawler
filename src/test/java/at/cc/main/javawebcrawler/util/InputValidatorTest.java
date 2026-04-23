package at.cc.main.javawebcrawler.util;

import at.cc.main.javawebcrawler.exception.InputValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void testEmptyArguments() {
        String[] arguments = new String[0];

        Exception e = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments));

        assertEquals("Usage: java -jar crawler.jar url depth domain1 domain2 ...", e.getMessage());
    }

    @Test
    void testToFewArguments() {
        String[] arguments1 = {"https://www.aau.at/"};
        String[] arguments2 = {"https://www.aau.at/", "1"};

        Exception e = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments1));
        Exception e2 = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments2));

        assertEquals("Usage: java -jar crawler.jar url depth domain1 domain2 ...", e.getMessage());
        assertEquals("Usage: java -jar crawler.jar url depth domain1 domain2 ...", e2.getMessage());
    }

    @Test
    void testUrlNotValid() {
        String[] arguments1 = {"hts://www.aau.at/", "1", "aau.at"};

        Exception e = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments1));

        assertEquals("Please provide a valid url as the 1st argument", e.getMessage());
    }

    @Test
    void testDepthNotAValidNumber() {
        String[] arguments = {"https://www.aau.at/", "b3", "aau.at"};

        Exception e = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments));

        assertEquals("Please provide a Number as the 2nd argument", e.getMessage());
    }

    @Test
    void testDomainNotValid() {
        String[] arguments = {"https://www.aau.at/", "1", "aau/.at"};

        Exception e = assertThrows(InputValidationException.class, () -> InputValidator.validateInput(arguments));

        assertEquals("Domain at position 2 of arguments is not valid", e.getMessage());
    }

    @Test
    void testInputIsValid() {
        String[] arguments = {"https://www.aau.at/", "1", "aau.at"};

        assertDoesNotThrow(() -> InputValidator.validateInput(arguments));
    }

}
