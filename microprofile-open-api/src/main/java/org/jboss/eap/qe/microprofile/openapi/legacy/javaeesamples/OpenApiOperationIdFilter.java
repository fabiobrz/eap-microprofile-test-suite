package org.jboss.eap.qe.microprofile.openapi.legacy.javaeesamples;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;

/**
 * A filter to make final configuration changes to the produced OpenAPI document.
 */
public class OpenApiOperationIdFilter implements OASFilter {

    /**
     * Replaces all spaces in each operation id with a hyphen.
     */
    @Override
    public Operation filterOperation(Operation operation) {
        if (operation.getOperationId().contains((" "))) {
            operation.setOperationId(operation.getOperationId().replace(" ", "-"));
        }
        return operation;
    }
}