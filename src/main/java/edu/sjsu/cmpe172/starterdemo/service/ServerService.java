package edu.sjsu.cmpe172.starterdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.sjsu.cmpe172.starterdemo.mapper.ServerMapper;
import edu.sjsu.cmpe172.starterdemo.model.Server;

@Service
public class ServerService {

    private final ServerMapper serverMapper;

    public ServerService(ServerMapper serverMapper) {
        this.serverMapper = serverMapper;
    }

    public List<Server> getAll() { return serverMapper.findAll(); }

    public Optional<Server> getById(String serverId) { return serverMapper.findById(serverId); }

    public int insert(Server server) { return serverMapper.insert(server); }

    public int updateStatus(String serverId, String status) { return serverMapper.updateStatus(serverId, status); }
}
