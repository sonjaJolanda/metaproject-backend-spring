package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/test")
public class TestEndpoint {

    @GetMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity getProjectFieldAccess() {
        return ResponseEntity.ok("Hallo2");
    }
}
