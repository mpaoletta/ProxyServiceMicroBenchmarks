package io.redbee;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * Created by martinpaoletta on 13/11/16.
 */
@RestController
public class DelayResource {

    @Value("${backend.url}")
    private String backendUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/delay/{millis}", method = RequestMethod.GET)
    public String delayedEcho(@PathVariable String millis) {
        return restTemplate.getForObject(backendUrl + millis, String.class);
    }

}
