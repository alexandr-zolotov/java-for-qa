package com.thomascook.jfq.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.jsonResponse;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.like;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;

/**
 * @author Alexandr Zolotov
 */
public class RestTemplateExamples {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort());

    /**
     * RestTemplate is a convenient class for implementation of REST-like interaction over HTTP.
     * That being said, it fits well when you need to check bodies of responses but is a bad choice for
     * error response codes test. Even when you use RestTemplate's API methods that return {@link org.springframework.http.ResponseEntity}
     * HTTP codes from 4xx and 5xx ranges will cause an exception by default. This behavior can be altered to some extent
     * but requires a bit of low level interaction coding. This kind of kills the idea of abstraction provided by RestTemplate.
     */

    /**
     * In the simplest form RestTemplate creation is trivial
     */
    private RestTemplate simplest = new RestTemplate();

    /**
     * If you need, you can customize some parts
     */
    private RestTemplate buildCustomizedRestTemplate() {

        /**
         * ClientHttpRequestFactory allows to specify some a bit low level but important things
         */
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        /**
         * By default outgoing request is buffered because it is convenient to know the value for Content-Length header.
         * However when the request body can be huge it is wise to disable buffering to not run out of memory. Content
         * will be transferred as 'chunked' in this case.
         */
        factory.setBufferRequestBody(false);

        /**
         * You can set custom timeouts (in milliseconds).
         */
        factory.setConnectTimeout((int)TimeUnit.MINUTES.toMillis(1));
        factory.setReadTimeout((int)TimeUnit.MINUTES.toMillis(1));

        RestTemplate restTemplate = new RestTemplate(factory);

        /**
         * For example if we have some customized ObjectMapper we can use it to alter
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        restTemplate.setMessageConverters(Collections.singletonList(converter));
        return restTemplate;
    }

    @Test
    public void dummy() { //todo rename
        int port = wireMockRule.port();

        wireMockRule.stubFor(
                post(urlPathMatching("/"))
                        .withRequestBody(new EqualToJsonPattern("{\"field1\":\"value1\"}", false, false)).willReturn(
                        like(jsonResponse("{\"textField\":\"ololo\", \"number\":123}"))
                ));


        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").path("/").port(port).build().toUri();

        RestTemplate restTemplate = buildCustomizedRestTemplate();

        Map<String, String[]> payload = new HashMap<>();
        payload.put("field1", new String[]{"value1"});
        ResponseEntity<Bean> responseEntity = restTemplate.postForEntity(uri, payload, Bean.class);

        wireMockRule.verify(allRequests());

        System.out.println(responseEntity.getBody());

    }

    static class Bean {

        @JsonProperty("textField")
        String textField;
        @JsonProperty("number")
        int number;

        @Override
        public String toString() {
            return "TextField: " + textField + "\nNumber: " + number;
        }
    }
}
