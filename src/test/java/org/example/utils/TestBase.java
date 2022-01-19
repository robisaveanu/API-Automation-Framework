package org.example.utils;

import com.thoughtworks.gauge.Step;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;

public class TestBase {

    protected static Configuration configuration;

    @Step("Load API basics")
    public static void beforeTests() {
        configuration = ConfigurationManager.getConfiguration();

        baseURI = configuration.baseURI();
        basePath = configuration.basePath();
    }
}
