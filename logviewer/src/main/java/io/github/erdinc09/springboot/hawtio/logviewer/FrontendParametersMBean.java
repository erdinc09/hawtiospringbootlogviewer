package io.github.erdinc09.springboot.hawtio.logviewer;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@ManagedResource(
        objectName="io.github.erdinc09:category=springboot,name=FrontendParameters",
        description="Frontend application gets the related parameter from this jmx endpoint")
@Component
public final class FrontendParametersMBean {

    @ManagedAttribute
    public String getWebSocketUrl(){
        //TODO:from properties.
        return "ws://localhost:8080/hawtio-spring-boot-log-viewer";
    }
}
