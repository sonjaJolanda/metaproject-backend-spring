package main.java.org.htwg.konstanz.metaproject.testing;

import main.java.org.htwg.konstanz.metaproject.entities.PrioTeamProject;
import org.junit.jupiter.api.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author MaWeissh
 * @version 1.0
 */
public class PrioTeamProjectTest {
    private static Validator validator;
    private static PrioTeamProject prioTeamProject;

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
        prioTeamProject = new PrioTeamProject();
    }

    @AfterEach
    public void tearDown() {
        prioTeamProject = null;
    }

//	@Test
//	public void testPrioTeamProject() {
//		
//	}
//
//	@Test
//	public void testProjectId() {
//		
//	}
//
//	@Test
//	public void testTeamId() {
//		
//	}

    @Test
    public void testPrioritisation() {
        Set<ConstraintViolation<PrioTeamProject>> constraintViolations;

        // Test: Not Null
        prioTeamProject.setPrioritisation(null);
        constraintViolations = validator.validate(prioTeamProject);
        assertEquals(2, constraintViolations.size());

        //Test: Size
        prioTeamProject.setPrioritisation("112345678911234567891123456");
        constraintViolations = validator.validate(prioTeamProject);
        assertEquals(2, constraintViolations.size());

        //Test: Contains Letter or 0
        prioTeamProject.setPrioritisation("1a4");
        constraintViolations = validator.validate(prioTeamProject);
        assertEquals(2, constraintViolations.size());

        prioTeamProject.setPrioritisation("104");
        constraintViolations = validator.validate(prioTeamProject);
        assertEquals(2, constraintViolations.size());

    }
}
