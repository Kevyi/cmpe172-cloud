package edu.sjsu.cmpe172.starterdemo.model;

public class AppService {
    private int service_id;
    private String name;
    private String description;
    private String docker_image;

    public AppService() {}

    public AppService(int service_id, String name, String description, String docker_image) {
        this.service_id = service_id;
        this.name = name;
        this.description = description;
        this.docker_image = docker_image;
    }

    public int getService_id() { return service_id; }
    public void setService_id(int service_id) { this.service_id = service_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocker_image() { return docker_image; }
    public void setDocker_image(String docker_image) { this.docker_image = docker_image; }
}
