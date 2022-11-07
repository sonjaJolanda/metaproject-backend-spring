package main.java.org.htwg.konstanz.metaproject.testing;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.junit.Ignore;
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
public class UserTest {
    private static Validator validator;
    private static User user;

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
        user = new User();
    }

    @AfterEach
    public void tearDown() {
        user = null;
    }

    @Test
    public void testUserId() {
        //User Id wird beim erstellen eines neuen Objekts nicht autogeneriert
    }

    @Test
    @Ignore
    public void testUserName() {
        user.setUserPassword("123");
        Set<ConstraintViolation<User>> constraintViolations;

        //Test: Not Null
        user.setUserName(null);
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("userName is null", constraintViolations.iterator().next().getMessage());

        //Test: Pattern
        user.setUserName("abcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-25 letters and spaces", constraintViolations.iterator().next().getMessage());
		
	/*	//Test: Pattern
		user.setUserName("A0B");
		constraintViolations = validator.validate(user);
		assertEquals(1, constraintViolations.size());
		assertEquals("Must not contain numbers", constraintViolations.iterator().next().getMessage());
		*/
    }

    @Test
    public void testUserPassword() {
        Set<ConstraintViolation<User>> constraintViolations;
        user.setUserName("Test");

        //Test: Not Null
        user.setUserPassword(null);
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("userPassword is null", constraintViolations.iterator().next().getMessage());

        //Test: Size
        user.setUserPassword("abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-100 letters and spaces", constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void testTokenJwt() {
        Set<ConstraintViolation<User>> constraintViolations;
        user.setUserName("Test");
        user.setUserPassword("123");

        //Test: Size
        user.setTokenJwt("abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-1000 letters and spaces", constraintViolations.iterator().next().getMessage());

    }

//	@Test
//	public void testRole() {
//		fail("Not yet implemented");
//	}

    @Test
    public void testUserFirstName() {
        Set<ConstraintViolation<User>> constraintViolations;
        user.setUserName("Test");
        user.setUserPassword("123");

        //Test: Size
        user.setUserFirstName("abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-50 letters and spaces", constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void testUserLastName() {
        Set<ConstraintViolation<User>> constraintViolations;
        user.setUserName("Test");
        user.setUserPassword("123");

        //Test: Size
        user.setUserLastName("abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-50 letters and spaces", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testUserEmail() {
        Set<ConstraintViolation<User>> constraintViolations;
        user.setUserName("Test");
        user.setUserPassword("123");

        //Test: Size
        user.setUserEmail("abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxyz");
        constraintViolations = validator.validate(user);
        assertEquals(1, constraintViolations.size());
        assertEquals("1-50 letters and spaces", constraintViolations.iterator().next().getMessage());
    }


}
