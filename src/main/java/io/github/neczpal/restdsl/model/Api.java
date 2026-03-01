package io.github.neczpal.restdsl.model;

public class Api {
    private String name;
    private String title;
    private String version;
    private String base;

    public Api(String name, String title, String version, String base) {
        this.name = name;
        this.title = title;
        this.version = version;
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public String getBase() {
        return base;
    }
}
