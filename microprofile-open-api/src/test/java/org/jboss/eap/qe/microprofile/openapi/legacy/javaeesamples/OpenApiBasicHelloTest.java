package org.jboss.eap.qe.microprofile.openapi.legacy.javaeesamples;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.eap.qe.microprofile.openapi.OpenApiTestHelper;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class OpenApiBasicHelloTest {

    private final static String DEPLOYMENT_NAME = "MicroProfileOpenApiJavaEESamplesMigratedTest";

    String openApiUrl;

    @Before
    public void composeEndpointsUrl() {
        openApiUrl = OpenApiTestHelper.composeDefaultOpenApiUrl();
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(
                WebArchive.class,
                OpenApiTestHelper.composeDefaultWarDeploymentName(DEPLOYMENT_NAME)
        ).addClasses(RestApplication.class,
                RestHelloResource.class,
                OpenApiModelReader.class,
                OpenApiOperationIdFilter.class
        ).addAsResource(
                "META-INF/microprofile-config.properties"
        ).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println("************************************************************");
        System.out.println(archive.toString(true));
        System.out.println("************************************************************");

        return archive;
    }

    @Test
    @RunAsClient
    public void testServerInternal() {
        Response responseContent = RestAssured.when().get(openApiUrl);

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Response: " + responseContent.then().extract().asString());
        System.out.println("-------------------------------------------------------------------------");

        responseContent.then()
                .statusCode(200)
                .contentType(Matchers.equalToIgnoringCase("application/yaml"))
                .body(Matchers.containsString("hello-world"))
                .extract().asString();
    }
}
