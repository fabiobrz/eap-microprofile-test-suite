package org.jboss.eap.qe.microprofile.openapi.legacy.rhoarqe.thorntailts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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

@RunAsClient
@RunWith(Arquillian.class)
public class MicroProfileOpenApi11Test {

    private final static String PATCH_ENDPOINT_PORT = "8080";
    private final static String DEPLOYMENT_NAME = "MicroProfileOpenApi11Test";

    String openApiUrl, patchTestUrl;
    @Before
    public void composeEndpointsUrl() {
        openApiUrl = OpenApiTestHelper.composeDefaultOpenApiUrl();
        patchTestUrl = String.format(
                "http://%s:%s/%s/rest/patch",
                OpenApiTestConstants.DEFAULT_HOST_NAME,
                PATCH_ENDPOINT_PORT,
                DEPLOYMENT_NAME);
    }

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        WebArchive war = ShrinkWrap.create(
                WebArchive.class,
                OpenApiTestHelper.composeDefaultWarDeploymentName(DEPLOYMENT_NAME)
        ).addClasses(
                RestApplication.class,
                RestOpenApi11AnnotatedResource.class
        ).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return war;
    }

    @Test
    @InSequence(1)
    public void hitEndpoint() {
        RestAssured.when().patch(patchTestUrl).then()
                .statusCode(200)
                .body(Matchers.comparesEqualTo("Patch OK"));
    }

    @Test
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
        Map<String, Object> patchPath = (Map<String, Object>) paths.get("/rest/patch");
        Assert.assertFalse(patchPath.isEmpty());
        // 1.1 the addition of the JAXRS 2.1 PATCH method
        Map<String, Object> patchMethod = (Map<String, Object>) patchPath.get("patch");
        Assert.assertFalse(patchMethod.isEmpty());
        Assert.assertTrue(patchMethod.keySet().size() == 7);
        // 1.1 @Content now supports a singular example field
        Map<String, Object> responses = (Map<String, Object>) patchMethod.get("responses");
        Assert.assertFalse(responses.isEmpty());
        Map<String, Object> defaultResponse = (Map<String, Object>) responses.get("default");
        Assert.assertFalse(defaultResponse.isEmpty());
        Map<String, Object> contentAnnotation = (Map<String, Object>) defaultResponse.get("content");
        Assert.assertFalse(contentAnnotation.isEmpty());
        Map<String, Object> mediaType = (Map<String, Object>) contentAnnotation.get("application/json");
        Assert.assertFalse(mediaType.isEmpty());
        Assert.assertTrue(mediaType.get("example").equals("content-example"));
        // 1.1 @Extension now has a parseValue field for complex values
        Assert.assertTrue(patchMethod.get("x-string-property").equals("string-value"));
        Assert.assertTrue(patchMethod.get("x-boolean-property").equals(true));
        Assert.assertTrue(patchMethod.get("x-number-property").equals(42));

        Map<String, Object> objectProperty = (Map<String, Object>) patchMethod.get("x-object-property");
        Assert.assertTrue(objectProperty.get("property-1").equals("value-1"));
        Assert.assertTrue(objectProperty.get("property-2").equals("value-2"));
        Assert.assertTrue(((Map<String, Object>) objectProperty.get("property-3")).get("prop-3-1").equals(42));
        Assert.assertTrue(((Map<String, Object>) objectProperty.get("property-3")).get("prop-3-2").equals(true));

        Assert.assertTrue(((List<String>) patchMethod.get("x-string-array-property")).containsAll(Arrays.asList("one", "two", "three")));

        List<Object> objectArrayProperty = (List<Object>) patchMethod.get("x-object-array-property");
        Assert.assertTrue(objectArrayProperty.size() == 2);
        Assert.assertTrue(((Map<String, String>) objectArrayProperty.get(0)).get("name").equals("item-1"));
        Assert.assertTrue(((Map<String, String>) objectArrayProperty.get(1)).get("name").equals("item-2"));
    }
}
