package edu.sjsu.cmpe172.starterdemo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.sjsu.cmpe172.starterdemo.mapper.AppServiceMapper;
import edu.sjsu.cmpe172.starterdemo.model.AppService;

@Service
public class AppServiceService {

    private final AppServiceMapper appServiceMapper;

    public AppServiceService(AppServiceMapper appServiceMapper) {
        this.appServiceMapper = appServiceMapper;
    }

    public List<AppService> getAll() { return appServiceMapper.findAll(); }

    public Optional<AppService> getById(int serviceId) { return appServiceMapper.findById(serviceId); }
}
