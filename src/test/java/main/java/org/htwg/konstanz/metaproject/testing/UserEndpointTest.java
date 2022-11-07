package main.java.org.htwg.konstanz.metaproject.testing;

import com.google.gson.Gson;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.junit.Ignore;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Stefano
 * @version 1.0
 */
public class UserEndpointTest {

    static Client client;
    //static final String TESTDATA_FILE = "C:\\Metaproject\\Metaprojekt\\Metaproject\\src\\main\\resources\\Datasets\\UserEndpoint-dataset.xml";
    Gson gson = new Gson();

    // @PersistenceContext(unitName = "Metaproject-persistence-unit")
    /*@PersistenceContext()
    private EntityManager em;*/

    @BeforeAll
    public static void setUpBeforeClass() {

        // URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        // HttpServer server = HttpServer.create(new
        // InetSocketAddress(uri.getPort()), 0);
        // HttpHandler handler =
        // RuntimeDelegate.getInstance().createEndpoint(new Application(),
        // HttpHandler.class);
        // server.createContext(uri.getPath(), handler);
        // server.start();
        // System.out.println(server);


        /*
         *WebTarget target =
         *client.target("http://localhost:8080/Metaproject/app/user");
         *Invocation invocation = target.request().buildGet();
        Response
                * response = invocation.invoke();
         */
        // System.out.println(response.getStatus());

    }

    @AfterAll
    public static void tearDownAfterClass() {
    }

   /* protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream(TESTDATA_FILE));
    }*/

    @BeforeEach
    public void setUp() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void tearDown() {
        client = null;
    }

    @Test
    @Ignore
    public void testUserById() {
        Response response = client.target("http://localhost:8080/Metaproject/app/user/1").request("application/json").get();
        String json = response.readEntity(String.class);
        System.out.println(json);
        String[] jsonUser = json.split("role");
        String jsonUserNeu = jsonUser[0].substring(0, jsonUser[0].length() - 2);
        System.out.println(jsonUserNeu);
        // User user = em.find(User.class, 1);

        User user = gson.fromJson(jsonUserNeu + "}", User.class);
        // System.out.println(user.getRole());
    }

    @Test
    @Ignore
    public void validateURLs() {
        assertEquals(200, client.target("http://localhost:8080/Metaproject/app/user").request().get().getStatus());
    }

    @Test
    @Ignore
    public void validateURLUserById() {
        assertEquals(200, client.target("http://localhost:8080/Metaproject/app/user/1").request().get().getStatus());
    }

}
