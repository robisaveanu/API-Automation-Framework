package org.example.steps;

import com.thoughtworks.gauge.*;
import com.thoughtworks.gauge.datastore.ScenarioDataStore;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.example.exceptions.NoStackTraceException;

import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;

public class StepImplementation {

    @Step("Check contract for the <object> endpoint")
    @ContinueOnFailure
    @SneakyThrows
    public void checkContract(String object) {
        URL schema = getClass().getResource("/schemas/" + object + "_schema.json");
        if (schema == null) {
            throw new NoStackTraceException("There is no available schema for the endpoint provided.");
        }
        try {
            given().
                    when().
                    get("/" + object).
                    then().
                    body(matchesJsonSchemaInClasspath("schemas/" + object + "_schema.json"));
        } catch (AssertionError e) {
            Gauge.writeMessage("Checking the response against the schema failed.");
            throw e;
        }
    }

    @Step("Given the endpoint <endpoint>")
    public void setEndpoint(String endpoint) {
        ScenarioDataStore.put("endpoint", endpoint);
    }

    @Step("The result should not arrive later than <5> seconds")
    public void checkTime(Long i) {
        when().get(ScenarioDataStore.get("endpoint").toString()).then().time(lessThan(i * 1000));
    }

    @Step("Check the status code")
    public void checkStatus() {
        get(ScenarioDataStore.get("endpoint").toString()).then().statusCode(200);
    }

    @Step("Check that the following movies have the correct number of characters <films>")
    public void checkNumberCharacters(Table films) {
        //status first
        Response response = get("/films").andReturn();
        get("/films").then().statusCode(200);

        for (TableRow row : films.getTableRows()) {
            String title = row.getCell("Movie");
            int expected = Integer.parseInt(row.getCell("Characters Count"));
            assertThat(expected).isEqualTo(response.body().jsonPath().getList("results.findAll{ it.title=='" + title + "' }.characters[0]").size());
        }
    }

    @Step("Cross-check the characters in movie <i>")
    public void crossCheckCh(Integer i) {
        //status first
        Response response = get("/films").andReturn();
        get("/films").then().statusCode(200);

        String title = response.body().jsonPath().get("results.title[" + (i - 1) + "]");
        List<String> characters = response.body().jsonPath().getList("results[" + (i - 1) + "].characters");

        //call every endpoint in the list and check the presence of this title
        for (String s : characters) {
            when().get(s).then().statusCode(200).assertThat().body("films", hasItems(baseURI + basePath + "/films/" + i + "/"));
        }
    }
}
