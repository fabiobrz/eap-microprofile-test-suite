package org.jboss.eap.qe.microprofile.openapi.legacy.rhoarqe.thorntailts;

import io.restassured.RestAssured;
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

import java.util.List;
import java.util.Map;

@RunWith(Arquillian.class)
public class MicroProfileSimpleOpenApi10Test {

    private final static String RESOURCE_ENDPOINT_PORT = "8080";
    private final static String DEPLOYMENT_NAME = "MicroProfileSimpleOpenApi10Test";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        WebArchive deployment = ShrinkWrap.create(
                WebArchive.class,
                OpenApiTestHelper.composeDefaultWarDeploymentName(DEPLOYMENT_NAME));
        deployment.addClass(RestApplication.class);
        deployment.addClass(RestSimpleResource.class);
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
        RestAssured.when().get(resourceUrl).then()
                .statusCode(200)
                .body(Matchers.comparesEqualTo("SimpleResource user1 null"));
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

        List<Object> parameters = (List<Object>) getMethod.get("parameters");
        Assert.assertTrue(parameters.size() == 2);

        Map<String, Object> pathParam = (Map<String, Object>) parameters.get(0);
        Assert.assertFalse(pathParam.isEmpty());
        Assert.assertTrue(pathParam.get("name").equals("pathParam"));
        Assert.assertTrue(pathParam.get("in").equals("path"));

        Map<String, Object> queryParam = (Map<String, Object>) parameters.get(1);
        Assert.assertFalse(queryParam.isEmpty());
        Assert.assertTrue(queryParam.get("name").equals("queryParam"));
        Assert.assertTrue(queryParam.get("in").equals("query"));
    }
}
