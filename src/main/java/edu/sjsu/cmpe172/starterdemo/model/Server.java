package edu.sjsu.cmpe172.starterdemo.model;

public class Server {
    private String server_id;
    private String name;
    private String ip_address;
    private String status;

    public Server() {}

    public Server(String server_id, String name, String ip_address, String status) {
        this.server_id = server_id;
        this.name = name;
        this.ip_address = ip_address;
        this.status = status;
    }

    public String getServer_id() { return server_id; }
    public void setServer_id(String server_id) { this.server_id = server_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIp_address() { return ip_address; }
    public void setIp_address(String ip_address) { this.ip_address = ip_address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
