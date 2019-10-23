package org.jboss.eap.qe.microprofile.openapi.legacy.rhoarqe.thorntailts;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.eap.qe.microprofile.openapi.OpenApiTestConstants;
import org.jboss.eap.qe.microprofile.openapi.OpenApiTestHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@RunWith(Arquillian.class)
public class MicroProfileAnnotationsOpenApi10Test {

    private final static String RESOURCE_ENDPOINT_PORT = "8080";
    private final static String DEPLOYMENT_NAME = "MicroProfileAnnotatedOpenApi10Test";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        WebArchive deployment = ShrinkWrap.create(
                WebArchive.class,
                OpenApiTestHelper.composeDefaultWarDeploymentName(DEPLOYMENT_NAME));
        deployment.addClass(RestApplication.class);
        deployment.addClass(MyResponse.class);
        deployment.addClass(RestOpenApi10AnnotatedResource.class);
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return deployment;
    }

    String openApiUrl, resourceUrl;
    @Before
    public void composeEndpointsUrl() {
        openApiUrl = OpenApiTestHelper.composeDefaultOpenApiUrl();
        resourceUrl = String.format(
                "http://%s:%s/%s/rest/user1",
                OpenApiTestConstants.DEFAULT_HOST_NAME,
                RESOURCE_ENDPOINT_PORT,
                DEPLOYMENT_NAME);
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void hitEndpoint() {
        RestAssured.given().header("Accept", "application/json").when().get(resourceUrl).then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("attribute1", Matchers.notNullValue(),
                        "attribute1", Matchers.is("user1"),
                        "attribute2", Matchers.nullValue());
    }

    @Test
    @RunAsClient
    @InSequence(2)
    @SuppressWarnings("unchecked")
    public void openApiDocument() {
        String responseContent = RestAssured.when().get(openApiUrl).then()
                .statusCode(200)
                .extract().asString();
        Yaml yaml = new Yaml();
        Object yamlObject = yaml.load(responseContent);
        Map<String, Object> yamlMap = (Map<String, Object>) yamlObject;

        Map<String, Object> paths = (Map<String, Object>) yamlMap.get("paths");
        Assert.assertFalse(paths.isEmpty());

        Map<String, Object> restPath = (Map<String, Object>) paths.get("/rest/{pathParam}");
        Assert.assertFalse(restPath.isEmpty());

        Map<String, Object> getMethod = (Map<String, Object>) restPath.get("get");
        Assert.assertFalse(getMethod.isEmpty());
        Assert.assertNotNull(getMethod.get("responses"));

        Map<String, Object> responses = (Map<String, Object>) getMethod.get("responses");
        Assert.assertNotNull(responses.get("default"));

        Map<String, Object> defaultResponse = (Map<String, Object>) responses.get("default");
        Assert.assertNotNull(defaultResponse.get("content"));

        Map<String, Object> defaultContent = (Map<String, Object>) defaultResponse.get("content");
        Assert.assertNotNull(defaultContent.get("application/json"));

        Map<String, Object> jsonContent = (Map<String, Object>) defaultContent.get("application/json");
        Assert.assertNotNull(jsonContent.get("schema"));
        Assert.assertNotNull(yamlMap.get("components"));

        Map<String, Object> components = (Map<String, Object>) yamlMap.get("components");
        Assert.assertNotNull(components.get("schemas"));

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        Assert.assertNotNull(schemas.get("MyResponse"));

        Map<String, Object> responsePOJO = (Map<String, Object>) schemas.get("MyResponse");
        Assert.assertNotNull(responsePOJO.get("properties"));

        Map<String, Object> properties = (Map<String, Object>) responsePOJO.get("properties");
        Assert.assertNotNull(properties.get("attribute1"));
        Assert.assertNotNull(properties.get("attribute2"));
    }
}