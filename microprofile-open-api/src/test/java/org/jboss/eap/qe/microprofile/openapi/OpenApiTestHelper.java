package org.jboss.eap.qe.microprofile.openapi;

public class OpenApiTestHelper {

    public static String composeDefaultOpenApiUrl() {
        return String.format(
                "http://%s:%s/openapi",
                OpenApiTestConstants.DEFAULT_HOST_NAME,
                OpenApiTestConstants.DEFAULT_ENDPOINT_PORT);
    }

    public static String composeDefaultWarDeploymentName(String deploymentName) {
        return String.format("%s.war", deploymentName);
    }
}