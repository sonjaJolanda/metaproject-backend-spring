package main.java.org.htwg.konstanz.metaproject.services;

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordServiceImplTest {

    private final static Logger log = Logger.getLogger(PasswordServiceImpl.class.getName());

    private PasswordService classUnterTest;

    @Before
    public void setUp() throws Exception {
        classUnterTest = new PasswordServiceImpl();
    }

    @Test
    public void testValidSecurePassword() {
        log.info("Test password hash.");
        assertEquals("098f6bcd4621d373cade4e832627b4f6", classUnterTest.securePassword("test"));
    }

    @Test
    public void testInvalidSecurePassword() {
        log.info("Test password hash with null.");
        try {
            classUnterTest.securePassword(null);
            fail("Expected Exception wasn't thrown!");
        } catch (NullPointerException e) {
            log.info("Expected exception was thrown!");
        }
    }

}
