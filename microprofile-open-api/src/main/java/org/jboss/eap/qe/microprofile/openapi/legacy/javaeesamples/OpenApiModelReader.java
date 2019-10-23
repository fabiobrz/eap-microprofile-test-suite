package org.jboss.eap.qe.microprofile.openapi.legacy.javaeesamples;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;

/**
 * Generates a base model to be used by the OpenAPI.
 */
public class OpenApiModelReader implements OASModelReader {

    @Override
    public OpenAPI buildModel() {
        return OASFactory.createObject(OpenAPI.class);
    }

}