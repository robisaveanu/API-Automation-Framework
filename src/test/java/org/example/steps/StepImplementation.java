package org.example.steps;

import com.thoughtworks.gauge.*;
import lombok.SneakyThrows;
import org.example.exceptions.NoStackTraceException;

import java.net.URL;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {

    private HashSet<Character> vowels;

    @Step("Vowels in English language are <vowelString>.")
    public void setLanguageVowels(String vowelString) {
        vowels = new HashSet<>();
        for (char ch : vowelString.toCharArray()) {
            vowels.add(ch);
        }
    }

    @Step("The word <word> has <expectedCount> vowels.")
    public void verifyVowelsCountInWord(String word, int expectedCount) {
        int actualCount = countVowels(word);
        assertThat(expectedCount).isEqualTo(actualCount);
    }

    @Step("Almost all words have vowels <wordsTable>")
    public void verifyVowelsCountInMultipleWords(Table wordsTable) {
        for (TableRow row : wordsTable.getTableRows()) {
            String word = row.getCell("Word");
            int expectedCount = Integer.parseInt(row.getCell("Vowel Count"));
            int actualCount = countVowels(word);

            assertThat(expectedCount).isEqualTo(actualCount);
        }
    }

    private int countVowels(String word) {
        int count = 0;
        for (char ch : word.toCharArray()) {
            if (vowels.contains(ch)) {
                count++;
            }
        }
        return count;
    }

    @Step("Check contract for the <object> endpoint")
    @ContinueOnFailure
    @SneakyThrows
    public void checkContract(String object) {
        URL schema = getClass().getResource("/schemas/" + object + "_schema.json");
        if (schema==null) {
            throw new NoStackTraceException("There is no available schema for the endpoint provided.");
        }
        try {
            given().
                    when().
                    get("/" + object).
                    then().
                    body(matchesJsonSchemaInClasspath("schemas/" + object + "_schema.json"));
        }
        catch (AssertionError e) {
            Gauge.writeMessage("Checking the response against the schema failed.");
            throw e;
        }
    }
}
