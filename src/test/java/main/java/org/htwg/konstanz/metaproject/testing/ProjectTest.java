package main.java.org.htwg.konstanz.metaproject.testing;

import main.java.org.htwg.konstanz.metaproject.entities.Project;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Stefano
 * @version 1.0
 */
public class ProjectTest {
    private static Validator validator;
    private static Project project;


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
        project = new Project();
    }

    @AfterEach
    public void tearDown() {
        project = null;
    }

    @Test
    public void testProjectTitle() {

        // Test: Size (under min)
        project.setProjectTitle("");
        Set<ConstraintViolation<Project>> titleViolation = validator.validateProperty(project, "projectTitle");
        if (!titleViolation.isEmpty())
            assertEquals("1-100 letters and spaces", new ArrayList<>(titleViolation).get(0).getMessage());

        // Test: Size (between min and max)
        project.setProjectTitle("abcdefghijklmnopqrstuvwxyz");
        titleViolation = validator.validateProperty(project, "projectTitle");
        if (!titleViolation.isEmpty())
            assertEquals(0, new ArrayList<>(titleViolation).get(0).getMessage());

        // Test: Size (over max)
        project.setProjectTitle("abcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz"
                + "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz");
        titleViolation = validator.validateProperty(project, "projectTitle");
        if (!titleViolation.isEmpty())
            assertEquals("1-100 letters and spaces", new ArrayList<>(titleViolation).get(0).getMessage());

        //Test: Not Null
        project.setProjectTitle(null);
        titleViolation = validator.validateProperty(project, "projectTitle");
        if (!titleViolation.isEmpty()) {
            assertEquals("projectTitle is null", new ArrayList<>(titleViolation).get(0).getMessage());
        }


    }

    @Test
    public void testKickOffDate() {
        // Test: Size (under min)
        project.setKickOffDate("");
        Set<ConstraintViolation<Project>> kickViolation = validator.validateProperty(project, "kickOffDate");
        if (!kickViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(kickViolation).get(0).getMessage());

        // Test: Not Null
        project.setKickOffDate(null);
        kickViolation = validator.validateProperty(project, "kickOffDate");
        if (!kickViolation.isEmpty())
            assertEquals("kickOffDate is null", new ArrayList<>(kickViolation).get(0).getMessage());
    }

    @Test
    public void testSpecialisation() {

        // Test: Not Null
        project.setSpecialisation(null);
        Set<ConstraintViolation<Project>> specialisationViolation = validator.validateProperty(project, "specialisation");
        if (!specialisationViolation.isEmpty())
            assertEquals("specialisation is null", new ArrayList<>(specialisationViolation).get(0).getMessage());
    }

    @Test
    public void testProjectDescription() {
        // Test: Size (over max) zu viel Text nötig
        project.setProjectDescription("abcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz");
        Set<ConstraintViolation<Project>> projectDescriptionViolation = validator.validateProperty(project, "projectDescription");
        if (!projectDescriptionViolation.isEmpty())
            assertEquals("0", new ArrayList<>(projectDescriptionViolation).get(0).getMessage());
    }
}
