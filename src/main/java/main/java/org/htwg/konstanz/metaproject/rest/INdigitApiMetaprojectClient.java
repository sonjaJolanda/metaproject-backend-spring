package main.java.org.htwg.konstanz.metaproject.rest;

import com.google.gson.Gson;
import main.java.org.htwg.konstanz.metaproject.dtos.ProjectMembersINdigitDTO;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiMetaproject;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiService;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class INdigitApiMetaprojectClient extends INdigitApiService implements INdigitApiMetaproject {
    public static String URL_OAUTH = "/oauth/token";
    public static String URL_USER_SEARCH = "/metaproject/user/";
    public static String URL_STATUS_LIST = "/metaproject/status";
    public static String URL_STUDY_PROGRAM_LIST = "/metaproject/studyprogram";
    public static String URL_PROJECT_TRANSFER = "/metaproject/project";

    public static String URL_PROJECT_TRANSFER_MEMBERS = "/metaproject/project/members";

    private final String clientId;
    private final String clientSecret;
    private final String apiHost;
    private final Gson gson;

    /**
     * Constructor to allow testing.
     *
     * @param gson         json library
     * @param apiHost      host prefix for api url
     * @param clientId     authentication client id
     * @param clientSecret authentication client secret
     */
    public INdigitApiMetaprojectClient(Gson gson, String apiHost, String clientId, String clientSecret) {
        this.apiHost = apiHost;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.gson = gson;
    }

    /**
     * Use this to instantiate a default instance of this api client.
     *
     * @param apiHost      host prefix for api url
     * @param clientId     client id for authentication
     * @param clientSecret client auth secret
     * @return new instance
     */
    public static INdigitApiMetaproject getInstance(String apiHost, String clientId, String clientSecret) {
        return new INdigitApiMetaprojectClient(new Gson(), apiHost, clientId, clientSecret);
    }

    private static class UserSearchResult {
        private final List<INdigitApiMetaproject.User> items;

        public UserSearchResult(List<INdigitApiMetaproject.User> items) {
            this.items = items;
        }
    }

    @Override
    public List<User> searchUser(String username) throws IOException, HttpStatusCodeException {
        String jsonBody = getWithUrl(URL_USER_SEARCH + username);
        return gson.fromJson(jsonBody, UserSearchResult.class).items;
    }

    private static class StatusListResponse {
        private final List<Status> items;

        public StatusListResponse(List<Status> items) {
            this.items = items;
        }
    }

    @Override
    public List<Status> listStatus() throws IOException, HttpStatusCodeException {
        String jsonBody = getWithUrl(URL_STATUS_LIST);
        return gson.fromJson(jsonBody, StatusListResponse.class).items;
    }

    private static class StudyProgramListResponse {
        private final List<StudyProgram> items;

        public StudyProgramListResponse(List<StudyProgram> items) {
            this.items = items;
        }
    }

    @Override
    public List<StudyProgram> listStudyPrograms() throws IOException, HttpStatusCodeException {
        String jsonBody = getWithUrl(URL_STUDY_PROGRAM_LIST);
        return gson.fromJson(jsonBody, StudyProgramListResponse.class).items;
    }

    private final static Logger log = LoggerFactory.getLogger(INdigitApiMetaprojectClient.class);

    @Override
    public int transferProjectStatusChange(IndigitProject project) throws IOException, HttpStatusCodeException {
        return transferWithUrl(gson.toJson(project), URL_PROJECT_TRANSFER);
    }

    @Override
    public int transferProjectMembers(List<ProjectMembersINdigitDTO> members) throws IOException, HttpStatusCodeException {
        return transferWithUrl(gson.toJson(members), URL_PROJECT_TRANSFER_MEMBERS);
    }

    private int transferWithUrl(String json, String url) throws IOException, HttpStatusCodeException {
        String token = requestToken(apiHost + URL_OAUTH, clientId, clientSecret);

        HttpPost httpPost = new HttpPost(apiHost + url);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        log.info(json);
        httpPost.setEntity(new StringEntity(json));

        HttpResponse response = httpClient.execute(httpPost);

        Optional<Header> errorReason = Arrays.stream(response.getHeaders("Error-Reason")).findFirst(); // only for debugging purposes
        if (!errorReason.isEmpty()) log.error(errorReason.get().getValue()); // only for debugging purposes

        checkStatusCode(200, response);
        return response.getStatusLine().getStatusCode();
    }

    private String getWithUrl(String url) throws IOException, HttpStatusCodeException {
        String token = requestToken(apiHost + URL_OAUTH, clientId, clientSecret);

        HttpGet httpGet = new HttpGet(apiHost + url);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpResponse response = httpClient.execute(httpGet);
        checkStatusCode(200, response);
        String jsonBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return jsonBody;
    }

}
