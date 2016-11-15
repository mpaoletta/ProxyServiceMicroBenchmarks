package io.redbee;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    private RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

    @RequestMapping(value = "/delay/{millis}", method = RequestMethod.GET)
    public String delayedEcho(@PathVariable String millis) {
        return restTemplate.getForObject(backendUrl + millis, String.class);
    }

    @RequestMapping(value = "/delay/{millis}/{percentage}", method = RequestMethod.GET)
    public String delayedEcho(@PathVariable String millis, @PathVariable String percentage) {
        return restTemplate.getForObject(backendUrl + millis + "/" + percentage, String.class);
    }


    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(2000);
        factory.setConnectTimeout(2000);
        return factory;
    }


}
