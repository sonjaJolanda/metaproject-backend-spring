package main.java.org.htwg.konstanz.metaproject.testing;

import main.java.org.htwg.konstanz.metaproject.entities.Team;
import org.junit.Ignore;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Stefano
 * @version 1.0
 */
public class TeamTest {

    private static Validator validator;
    private static Team team;

    @BeforeAll
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDownAfterClass() {
    }

    @BeforeEach
    public void setUp() {
        team = new Team();
    }

    @AfterEach
    public void tearDown() {
        team = null;
    }

    @Test
    public void teamIdTest() {
        // teamId wird nicht autogeneriert
    }

    @Test
    @Ignore
    public void teamNameTest() {
        Set<ConstraintViolation<Team>> constraintViolations;

        // Test: not null
        team.setTeamName(null);
        constraintViolations = validator.validate(team);
        assertEquals(1, constraintViolations.size());
        assertEquals("teamName is null", constraintViolations.iterator().next().getMessage());

        // Test: Size
        team.setTeamName("abcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(team);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-25 letters and spaces", constraintViolations.iterator().next().getMessage());

        // Test: no Number in Name
        team.setTeamName("Number1");
        constraintViolations = validator.validate(team);
        assertEquals(1, constraintViolations.size());
        assertEquals("Must not contain numbers", constraintViolations.iterator().next().getMessage());

        // Test : ok
        team.setTeamName("BlueManGroup");
        constraintViolations = validator.validate(team);
        assertEquals(0, constraintViolations.size());
        assertEquals("BlueManGroup", team.getTeamName());
    }

}
