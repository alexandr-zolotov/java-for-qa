package com.thomascook.jfq.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

/**
 * @author Alexandr Zolotov
 */
public class RestTemplateExamples {

    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateExamples.class);

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

    private ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
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
        HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        restTemplate.setMessageConverters(Collections.singletonList(converter));
        return restTemplate;
    }

    @Test
    public void useRestTemplate() {
        int port = wireMockRule.port();

        //set up a mock
        wireMockRule.stubFor(
                post(urlPathMatching("/"))
                        .withRequestBody(new EqualToJsonPattern("{\"field1\":\"value1\"}", false, false)).willReturn(
                        like(jsonResponse(new RequestBean("some text", 234)))
                ));

        //define target URL (URI to be more precise)
        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").path("/").port(port).build().toUri();

        RestTemplate restTemplate = buildCustomizedRestTemplate();

        /**
         * The nice thing about RestTemplate is that it tries to handle HTTP stuff Content-Type both sending the
         * request and receiving response. It is very convenient for some kinds of testing and for prototyping when you
         * don't need to handle http specific or IO errors. You can use any of {@link RestTemplate#postForObject(String, Object, Class, Map)}
         * methods then. If you need to analyze http response codes you can use a bunch of methods returning {@link ResponseEntity}.
         * Keep in mind, that 4xx and 5xx HTTP response codes are considered client and server errors.
         */
        Map<String, String[]> payload = new HashMap<>();
        payload.put("field1", new String[]{"value1"});

        ResponseBean justObject = restTemplate.postForObject(uri, payload, ResponseBean.class);
        ResponseEntity<ResponseBean> responseEntity = restTemplate.postForEntity(uri, payload, ResponseBean.class);

        LOG.info("Just object: {}", justObject);
        LOG.info("ResponseEntity: {}", responseEntity);

        wireMockRule.verify(2, allRequests());
    }

    @Test
    public void useApacheHttpClient() throws Exception {

        URI uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").path("/").port(wireMockRule.port()).build().toUri();

        //set up a mock
        wireMockRule.stubFor(
                post(urlPathMatching("/"))
                        .withRequestBody(new EqualToJsonPattern("{\"field1\":\"value1\"}", false, false)).willReturn(
                        like(jsonResponse(new RequestBean("some text", 234)))
                ));

        Map<String, String[]> payload = new HashMap<>();
        payload.put("field1", new String[]{"value1"});

        String requestString = objectMapper.writer().writeValueAsString(payload);
        StringEntity entity = new StringEntity(requestString);
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));

        try (CloseableHttpClient client = HttpClients.createDefault()){

            try (CloseableHttpResponse response = client.execute(httpPost)){
                String responseString = EntityUtils.toString(response.getEntity());
                LOG.info("HttpClient response code: {}", response.getStatusLine().getStatusCode());
                LOG.info("HttpClient response body: {}", responseString);

            }
        }

    }
}
