package ru.ak.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    @XmlElementWrapper(name="infoFiles")
    @XmlElement(name = "InfoFile")
    private List<InfoFile> infoFiles;
	private Object object;
	private boolean error;
	private String description;

    public Response() {
    }

    public Response(Object object, boolean error, String description) {
        this.object = object;
        this.error = error;
        this.description = description;
    }

    public Object getResult() {
        return object;
    }

    public void setResult(Object object) {
        this.object = object;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InfoFile> getInfoFiles() {
        return infoFiles;
    }

    public void setInfoFiles(List<InfoFile> objects) {
        this.infoFiles = objects;
    }
}
