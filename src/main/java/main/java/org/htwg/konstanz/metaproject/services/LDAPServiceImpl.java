package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;

/**
 * This service makes an user authentication with LDAP.
 *
 * @author SiKelle
 */
@Service
public class LDAPServiceImpl implements LDAPService {

    private final static Logger log = LoggerFactory.getLogger(LDAPServiceImpl.class);

    private final String ldap_url = Constants.LDAP_URL;
    private final String baseDn = "ou=users,dc=fh-konstanz,dc=de";
    private final String returningAttributes[] = {"givenname", "sn", "mail", "uid"};
    private final int searchScope = SearchControls.SUBTREE_SCOPE;


    /* (non-Javadoc)
     * @see org.htwg.konstanz.metaproject.services.LDAPService#checkUserCredentials(java.lang.String, java.lang.String)
     */
    @Override
    public User checkUserCredentials(String userName, String userPass) {
        log.info("Verify user {} with LDAP", userName);

        if (userName == null || userPass == null) {
            log.error("Invalid credentials username: {}", userName);
            return null;
        }

        try {
            String filter = "uid=" + userName;
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldap_url);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=" + userName + ",ou=users,dc=fh-konstanz,dc=de");
            env.put(Context.SECURITY_CREDENTIALS, userPass);


            DirContext ldapContent = new InitialDirContext(env);
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(searchScope);
            ctls.setReturningAttributes(returningAttributes);
            NamingEnumeration<?> answer = ldapContent.search(baseDn, filter, ctls);
            while (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();

                String firstName = attrs.get("givenname").toString().substring(attrs.get("givenname").toString().indexOf(" ") + 1, attrs.get("givenname").toString().length());
                String lastName = attrs.get("sn").toString().substring(attrs.get("sn").toString().indexOf(" ") + 1, attrs.get("sn").toString().length());
                String email = attrs.get("mail").toString().substring(attrs.get("mail").toString().indexOf(" ") + 1, attrs.get("mail").toString().length());

                log.info("Found user, {} {} {}", firstName, lastName, email);

                // return User with new attributes
                User user = new User();
                user.setUserFirstName(firstName);
                user.setUserLastName(lastName);
                user.setUserEmail(email);
                user.setUserName(userName);
                user.setUserPassword(userPass);

                return user;
            }


            return null;
        } catch (NamingException e) {
            log.trace(e.getMessage(), e);
            log.error("LoginLDAP failed {}", e.getExplanation());
            return null;
        } catch (Exception e) {
            User user = new User();
            user.setError(2);
            log.trace(e.getMessage(), e);
            log.error("Connection failed");
            return null;
        }
    }
}
