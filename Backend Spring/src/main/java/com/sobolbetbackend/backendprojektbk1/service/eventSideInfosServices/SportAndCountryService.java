package com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SportAndCountryService {
    private static final Logger log = LoggerFactory.getLogger(SportAndCountryService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public SportAndCountryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> void updateInfo(CrudRepository<T,String> repo, String apiUrl,
                               String node, String el, Class<T> tClass) throws ApiProblemException {
        String infos = restTemplate.getForObject(apiUrl, String.class);
        if(infos==null|| infos.isEmpty()) throw new ApiProblemException("Api reference is invalid");
        List<T> listOfInfos = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(infos);

            if (jsonNode.has(node)) {
                JsonNode infosNode = jsonNode.get(node);

                if (infosNode.isArray()) {
                    for (JsonNode infoNode : infosNode) {
                        if (infoNode.has(el)) {
                            String strInfo = infoNode.get(el).asText();
                            T infoInstance = tClass.getDeclaredConstructor(String.class).newInstance(strInfo);
                            listOfInfos.add(infoInstance);
                        } else{
                            throw new ApiProblemException("Api reference problems. Problems with elements of Node");
                        }
                    }
                } else{
                    throw new ApiProblemException("Api reference problems. Node is not array");
                }
            }else{
                throw new ApiProblemException("Api reference problems. Node doesn't exist");
            }
        } catch (Exception e) {
            log.error("Exception happened: {}", e.getMessage());
            throw new ApiProblemException("Error occurred during update: " + e.getMessage());
        }
        List<T> existingInfos = (List<T>) repo.findAll();
        List<T> newInfos = listOfInfos.stream()
                .filter(info -> !existingInfos.contains(info))
                .toList();
        // Сохранить только новые страны в базу данных
        repo.saveAll(newInfos);
    }
}
