package org.jboss.eap.qe.microprofile.openapi.legacy.rhoarqe.thorntailts;

import java.io.Serializable;

public class MyResponse implements Serializable {
    private String attribute1;
    private String attribute2;

    public MyResponse(String attribute1, String attribute2) {
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }
}
