package main.java.org.htwg.konstanz.metaproject.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Simple example INdigit api client with an implementation for the OAuth2 token mechanism.
 * Use this in combination with Apache HttpClient and Gson json parser.
 */
@Service
public class INdigitApiService {
    private static final String JSON_RESPONSE_ACCESS_TOKEN = "access_token";
    private final static Logger log = LoggerFactory.getLogger(INdigitApiService.class);

    protected final HttpClient httpClient = HttpClients.createDefault();
    protected final JsonParser jsonParser = new JsonParser();

    private String token;
    private Date timeLastTokenRequest;

    /**
     * Request an authentication token for the INdigit api using OAuth2 with the client identifier and secret.
     * The token can be used to pass with the Authorization header (don't forget the Bearer prefix) of every
     * following request. The token is valid for 24h by default, but if the token is older than 20h then
     * this method requests a new token, otherwise it returnes the current token.
     *
     * @param url      the oauth url to INdigit
     * @param clientId identifier of client application
     * @param secret   client's secret
     * @return token as string
     * @throws IOException is thrown if any exception occurs while executing the http request
     */
    public String requestToken(String url, String clientId, String secret) throws IOException, HttpStatusCodeException {

        // if the token is older than 55 minutes than a new token is requested and then returned, otherwise the current token is returned
        Date now = new Date();
        var fiftyFiveMinutes = 55 * 60 * 1000;
        if (this.timeLastTokenRequest != null && this.token != null && ((now.getTime() - this.timeLastTokenRequest.getTime()) < fiftyFiveMinutes))
            return this.token;

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // add authentication header
        String auth = String.format("%s:%s", clientId, secret);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = String.format("Basic %s", new String(encodedAuth));
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpResponse response = httpClient.execute(httpPost);
        checkStatusCode(HttpStatus.SC_OK, response);
        String jsonBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonObject body = jsonParser.parse(jsonBody).getAsJsonObject();
        this.token = body.get(JSON_RESPONSE_ACCESS_TOKEN).getAsString();
        this.timeLastTokenRequest = new Date();
        return token;
    }

    /**
     * Simple exception which is thrown caused by an error HTTP status code e.g. 400.
     */
    public static class HttpStatusCodeException extends Exception {
        public final int statusCode;

        public HttpStatusCodeException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }
    }

    /**
     * Ensure that a specific status code was returned. This method throws an {@link HttpStatusCodeException} in case
     * the result isn't the expected status code.
     *
     * @param expectedStatusCode code to be ensured
     * @param response           http response object
     */
    protected void checkStatusCode(int expectedStatusCode, HttpResponse response) {

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != expectedStatusCode) {
                throw new HttpStatusCodeException(statusCode, String.format("Error code: %d, An error occurred during the transmission of the data to Indigit.", statusCode));
            }
        } catch (HttpStatusCodeException e) {
            log.error(e.getMessage());
        }
    }

}
