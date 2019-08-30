package ru.ak.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Сведения о файле (вывод команды ls)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoFile {

    private String name;
    private boolean isDir;

    public InfoFile(String name, boolean isDir) {
        this.name = name;
        this.isDir = isDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }
}
