package ru.ak.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author a.kakushin
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Build {
    private String version;
    private String description;

    public Build() {}

    public Build(String version, String description) {
        this();
        this.version = version;
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }
}