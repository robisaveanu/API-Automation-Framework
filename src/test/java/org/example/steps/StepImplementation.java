package org.example.steps;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.Table;
import com.thoughtworks.gauge.TableRow;

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

    @Step("Check contract for the planets endpoint")
    public void getAllPlanets() {
        Gauge.writeMessage("fokjgoedfjghoirmog rhkhpr kphjkrpykh poyrtkpjh ktyphrjkpothykjptoykjpotkjpktypojktypkj tkjpktuypjk typokjp oktjpktjpk pjtyk pkjypokytp kyjpotyk jpktyjpokjt");
        given().
                when().
                get("/planets").
                then().
                body(matchesJsonSchemaInClasspath("schemas/planets_schema.json"));
    }
}
