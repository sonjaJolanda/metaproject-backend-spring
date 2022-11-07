package main.java.org.htwg.konstanz.metaproject.testing;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetaprojectTest {
    private static Validator validator;
    private static Metaproject metaproject;

    @BeforeAll
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() {
        metaproject = new Metaproject();
    }

    @AfterEach
    public void tearDown() {
        metaproject = null;
    }

    @Test
    public void testMetaprojectId() {
        //Metaproject Id wird beim erstellen eines neuen Objekts nicht autogeneriert
    }

    @Test
    public void testDescription() {

    }

    @Test
    public void testMetaprojectTitle() {

        // Test: Size (under min)
        metaproject.setMetaprojectTitle("");
        Set<ConstraintViolation<Metaproject>> metaTitleViolation = validator.validateProperty(metaproject, "metaProjectTitle");
        if (!metaTitleViolation.isEmpty())
            assertEquals("1-50 letters and spaces", new ArrayList<>(metaTitleViolation).get(0).getMessage());

        // Test: Size (between min and max)
        metaproject.setMetaprojectTitle("abcdefghijklmnopqrstuvwxyz");
        metaTitleViolation = validator.validateProperty(metaproject, "metaProjectTitle");
        if (!metaTitleViolation.isEmpty())
            assertEquals(0, new ArrayList<>(metaTitleViolation).get(0).getMessage());

        // Test: Size (over max)
        metaproject.setMetaprojectTitle("abcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz"
                + "AbcdefghijklmnopqrstuvwxyzAbcdefghijklmnopqrstuvwxyz");
        metaTitleViolation = validator.validateProperty(metaproject, "metaProjectTitle");
        if (!metaTitleViolation.isEmpty())
            assertEquals("1-50 letters and spaces", new ArrayList<>(metaTitleViolation).get(0).getMessage());

        //Test: Not Null
        metaproject.setMetaprojectTitle(null);
        metaTitleViolation = validator.validateProperty(metaproject, "metaProjectTitle");
        if (!metaTitleViolation.isEmpty())
            assertEquals("metaProjectTitle is null", new ArrayList<>(metaTitleViolation).get(0).getMessage());

        //Test: Pattern - No Numbers
        metaproject.setMetaprojectTitle("A0b");
        metaTitleViolation = validator.validateProperty(metaproject, "metaProjectTitle");
        if (!metaTitleViolation.isEmpty())
            assertEquals("metaProjectTitle must not contain numbers", new ArrayList<>(metaTitleViolation).get(0).getMessage());
    }

    @Test
    public void testProjectRegStart() {
        //Test: Not Null
        metaproject.setProjectRegStart(null);
        Set<ConstraintViolation<Metaproject>> projectRegStartViolation = validator.validateProperty(metaproject, "projectRegStart");
        if (!projectRegStartViolation.isEmpty())
            assertEquals("projectRegStart is null", new ArrayList<>(projectRegStartViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setProjectRegStart("afaf");
        projectRegStartViolation = validator.validateProperty(metaproject, "projectRegStart");
        if (!projectRegStartViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(projectRegStartViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setProjectRegStart("24.12.2015");
        projectRegStartViolation = validator.validateProperty(metaproject, "projectRegStart");
        if (!projectRegStartViolation.isEmpty())
            assertEquals(0, new ArrayList<>(projectRegStartViolation).get(0).getMessage());
    }

    @Test
    public void testProjectRegEnd() {
        //Test: Not Null
        metaproject.setProjectRegEnd(null);
        Set<ConstraintViolation<Metaproject>> projectRegEndViolation = validator.validateProperty(metaproject, "projectRegEnd");
        if (!projectRegEndViolation.isEmpty())
            assertEquals("projectRegEnd is null", new ArrayList<>(projectRegEndViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setProjectRegEnd("afaf");
        projectRegEndViolation = validator.validateProperty(metaproject, "projectRegEnd");
        if (!projectRegEndViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(projectRegEndViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setProjectRegEnd("24.12.2015");
        projectRegEndViolation = validator.validateProperty(metaproject, "projectRegEnd");
        if (!projectRegEndViolation.isEmpty())
            assertEquals(0, new ArrayList<>(projectRegEndViolation).get(0).getMessage());
    }

    @Test
    public void testStudentRegStart() {
        //Test: Not Null
        metaproject.setStudentRegStart(null);
        Set<ConstraintViolation<Metaproject>> studentRegStartViolation = validator.validateProperty(metaproject, "studentRegStart");
        if (!studentRegStartViolation.isEmpty())
            assertEquals("studentRegStart is null", new ArrayList<>(studentRegStartViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setStudentRegStart("afaf");
        studentRegStartViolation = validator.validateProperty(metaproject, "studentRegStart");
        if (!studentRegStartViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(studentRegStartViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setStudentRegStart("24.12.2015");
        studentRegStartViolation = validator.validateProperty(metaproject, "studentRegStart");
        if (!studentRegStartViolation.isEmpty())
            assertEquals(0, new ArrayList<>(studentRegStartViolation).get(0).getMessage());
    }

    @Test
    public void testStudentRegEnd() {
        //Test: Not Null
        metaproject.setStudentRegEnd(null);
        Set<ConstraintViolation<Metaproject>> studentRegEndViolation = validator.validateProperty(metaproject, "studentRegEnd");
        if (!studentRegEndViolation.isEmpty())
            assertEquals("studentRegEnd is null", new ArrayList<>(studentRegEndViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setStudentRegEnd("afaf");
        studentRegEndViolation = validator.validateProperty(metaproject, "studentRegEnd");
        if (!studentRegEndViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(studentRegEndViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setStudentRegEnd("24.12.2015");
        studentRegEndViolation = validator.validateProperty(metaproject, "studentRegEnd");
        if (!studentRegEndViolation.isEmpty())
            assertEquals(0, new ArrayList<>(studentRegEndViolation).get(0).getMessage());
    }

    @Test
    public void testTeamRegStart() {
        //Test: Not Null
        metaproject.setTeamRegStart(null);
        Set<ConstraintViolation<Metaproject>> teamRegStartViolation = validator.validateProperty(metaproject, "teamRegStart");
        if (!teamRegStartViolation.isEmpty())
            assertEquals("teamRegStart is null", new ArrayList<>(teamRegStartViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setTeamRegStart("afaf");
        teamRegStartViolation = validator.validateProperty(metaproject, "teamRegStart");
        if (!teamRegStartViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(teamRegStartViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setTeamRegStart("24.12.2015");
        teamRegStartViolation = validator.validateProperty(metaproject, "teamRegStart");
        if (!teamRegStartViolation.isEmpty())
            assertEquals(0, new ArrayList<>(teamRegStartViolation).get(0).getMessage());
    }

    @Test
    public void testTeamRegEnd() {
        //Test: Not Null
        metaproject.setTeamRegEnd(null);
        Set<ConstraintViolation<Metaproject>> teamRegEndViolation = validator.validateProperty(metaproject, "teamRegEnd");
        if (!teamRegEndViolation.isEmpty())
            assertEquals("teamRegEnd is null", new ArrayList<>(teamRegEndViolation).get(0).getMessage());

        //Test: Pattern
        metaproject.setTeamRegEnd("afaf");
        teamRegEndViolation = validator.validateProperty(metaproject, "teamRegEnd");
        if (!teamRegEndViolation.isEmpty())
            assertEquals("Validate date formats: dd/mm/yyyy or dd-mm-yy or dd.mm.yyyy with separators: . - / Valid dates only! d (1-31)/ m (1-12)/ y (0..)", new ArrayList<>(teamRegEndViolation).get(0).getMessage());

        //Test: Pattern valid Date
        metaproject.setTeamRegEnd("24.12.2015");
        teamRegEndViolation = validator.validateProperty(metaproject, "teamRegEnd");
        if (!teamRegEndViolation.isEmpty())
            assertEquals(0, new ArrayList<>(teamRegEndViolation).get(0).getMessage());
    }

    @Test
    public void testCourseOfStudies() {

        // Test: Size (under min)
        metaproject.setCourseOfStudies("");
        Set<ConstraintViolation<Metaproject>> courseOfStudiesViolation = validator.validateProperty(metaproject, "courseOfStudies");
        if (!courseOfStudiesViolation.isEmpty())
            assertEquals("1-25 letters and spaces", new ArrayList<>(courseOfStudiesViolation).get(0).getMessage());

        // Test: Size (over max)
        metaproject.setCourseOfStudies("abcdefghijklmnopqrstuvwxyz");
        courseOfStudiesViolation = validator.validateProperty(metaproject, "courseOfStudies");
        if (!courseOfStudiesViolation.isEmpty())
            assertEquals("1-25 letters and spaces", new ArrayList<>(courseOfStudiesViolation).get(0).getMessage());

        // Test: Size (between min and max)
        metaproject.setCourseOfStudies("abcdefghijklmnopqrstuvwx");
        courseOfStudiesViolation = validator.validateProperty(metaproject, "courseOfStudies");
        if (!courseOfStudiesViolation.isEmpty())
            assertEquals(0, new ArrayList<>(courseOfStudiesViolation).get(0).getMessage());

        //Test: Pattern - No Numbers
        metaproject.setCourseOfStudies("A0b");
        courseOfStudiesViolation = validator.validateProperty(metaproject, "courseOfStudies");
        if (!courseOfStudiesViolation.isEmpty())
            assertEquals("Must not contain numbers", new ArrayList<>(courseOfStudiesViolation).get(0).getMessage());
    }

    @Test
    public void testSemester() {

        // Test: Size (over max)
        metaproject.setSemester(26);
        Set<ConstraintViolation<Metaproject>> semesterViolation = validator.validateProperty(metaproject, "semester");
        if (!semesterViolation.isEmpty())
            assertEquals("semester must consist of max 25 digits", new ArrayList<>(semesterViolation).get(0).getMessage());

        // Test: Size (between min & max)
        metaproject.setSemester(20);
        semesterViolation = validator.validateProperty(metaproject, "semester");
        if (!semesterViolation.isEmpty())
            assertEquals(0, new ArrayList<>(semesterViolation).get(0).getMessage());

    }

    @Test
    public void testTeamMinSize() {

        //Test: Size (under min)
        metaproject.setTeamMinSize(0);
        Set<ConstraintViolation<Metaproject>> teamMinSizeViolation = validator.validateProperty(metaproject, "teamMinSize");
        if (!teamMinSizeViolation.isEmpty())
            assertEquals("teamMinSize must consist of min 1 digit", new ArrayList<>(teamMinSizeViolation).get(0).getMessage());

        // Test: Size (over min)
        metaproject.setTeamMinSize(3);
        teamMinSizeViolation = validator.validateProperty(metaproject, "teamMinSize");
        if (!teamMinSizeViolation.isEmpty())
            assertEquals(0, new ArrayList<>(teamMinSizeViolation).get(0).getMessage());
    }

    @Test
    public void testTeamMaxSize() {

        //Test: Size (over max)
        metaproject.setTeamMaxSize(26);
        Set<ConstraintViolation<Metaproject>> teamMaxSizeViolation = validator.validateProperty(metaproject, "teamMaxSize");
        if (!teamMaxSizeViolation.isEmpty())
            assertEquals("teamMaxSize must consist of max 25 digits", new ArrayList<>(teamMaxSizeViolation).get(0).getMessage());

        // Test: Size (under max)
        metaproject.setTeamMaxSize(3);
        teamMaxSizeViolation = validator.validateProperty(metaproject, "teamMaxSize");
        if (!teamMaxSizeViolation.isEmpty())
            assertEquals(0, new ArrayList<>(teamMaxSizeViolation).get(0).getMessage());
    }

    @Test
    public void testSpecialisation() {
        // Test: Not Null
        metaproject.setSpecialisation(null);
        Set<ConstraintViolation<Metaproject>> specialisationViolation = validator.validateProperty(metaproject, "specialisation");
        if (!specialisationViolation.isEmpty())
            assertEquals("specialisation is null", new ArrayList<>(specialisationViolation).get(0).getMessage());
    }
}
