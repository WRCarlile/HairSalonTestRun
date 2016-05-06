import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ClientTest {

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteClientsQuery = "DELETE FROM clients *;";
      String deleteStylistsQuery = "DELETE FROM stylists *;";
      con.createQuery(deleteClientsQuery).executeUpdate();
      con.createQuery(deleteStylistsQuery).executeUpdate();
    }
  }

  @Test
  public void Client_instantiatesCorrectly_true() {
    Client myClient = new Client("Mow the lawn", 1);
    assertEquals(true, myClient instanceof Client);
  }

  @Test
  public void getName_clientInstantiatesWithName_String() {
    Client myClient = new Client("Mow the lawn", 1);
    assertEquals("Mow the lawn", myClient.getName());
  }

  @Test
  public void all_emptyAtFirst() {
    assertEquals(Client.all().size(), 0);
  }

  @Test
  public void equals_returnsTrueIfNamesAretheSame() {
    Client firstClient = new Client("Mow the lawn", 1);
    Client secondClient = new Client("Mow the lawn", 1);
    assertTrue(firstClient.equals(secondClient));
  }

  @Test
  public void save_assignsIdToObject() {
    Client myClient = new Client("Mow the lawn", 1);
    myClient.save();
    Client savedClient = Client.all().get(0);
    assertEquals(myClient.getId(), savedClient.getId());
  }

  @Test
  public void find_findsClientInDatabase_true() {
    Client myClient = new Client("Mow the lawn", 1);
    myClient.save();
    Client savedClient = Client.find(myClient.getId());
    assertTrue(myClient.equals(savedClient));
  }

  @Test
  public void save_savesStylistIdIntoDB_true() {
    Stylist myStylist = new Stylist("Household chores");
    myStylist.save();
    Client myClient = new Client("Mow the lawn", myStylist.getId());
    myClient.save();
    Client savedClient = Client.find(myClient.getId());
    assertEquals(savedClient.getStylistId(), myStylist.getId());
  }
  @Test
  public void update_updatesClientName_true() {
    Client myClient = new Client("Mow the lawn", 1);
    myClient.save();
    myClient.update("Take a nap");
    assertEquals("Take a nap", Client.find(myClient.getId()).getName());
  }

  @Test
  public void delete_deletesClient_true() {
    Client myClient = new Client("Mow the lawn", 1);
    myClient.save();
    int myClientId = myClient.getId();
    myClient.delete();
    assertEquals(null, Client.find(myClientId));
  }

}
