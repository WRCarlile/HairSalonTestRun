import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }
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

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("To Do List!");
  }

  @Test
  public void stylistIsCreatedTest() {
    goTo("http://localhost:4567/");

    fill("#name").with("Household chores");
    submit("#catBtn");
    assertThat(pageSource()).contains("Your stylist has been saved.");
  }

  @Test
  public void stylistIsDisplayedTest() {
    Stylist myStylist = new Stylist("Household chores");
    myStylist.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    assertThat(pageSource()).contains("Household chores");
  }


   @Test
   public void stylistShowPageDiplayName() {
     goTo("http://localhost:4567/stylists/new");
     fill("#name").with("Household cheese");
     submit(".btn");
     click("a", withText("View stylists"));
     click("a", withText("Household cheese"));
     assertThat(pageSource()).contains("Household cheese");
   }

   @Test
   public void stylistClientsFormIsDisplayed() {
     goTo("http://localhost:4567/stylists/new");
     fill("#name").with("Shopping");
     submit(".btn");
     click("a", withText("View stylists"));
     click("a", withText("Shopping"));
     click("a", withText("Add a new client"));
     assertThat(pageSource()).contains("Add a client to Shopping");
   }

   @Test
   public void allClientsDisplayNameOnStylistPage() {
     Stylist myStylist = new Stylist ("Household chores");
     myStylist.save();
     Client firstClient = new Client ("Mow the lawn", myStylist.getId());
     firstClient.save();
     Client secondClient = new Client("Do the dishes", myStylist.getId());
     secondClient.save();
     String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
     goTo(stylistPath);
     assertThat(pageSource()).contains("Mow the lawn");
     assertThat(pageSource()).contains("Do the dishes");

   }

  @Test
  public void clientShowPage() {
    Stylist myStylist = new Stylist("Home");
    myStylist.save();
    Client myClient = new Client("Clean", myStylist.getId());
    myClient.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    click("a", withText("Clean"));
    assertThat(pageSource()).contains("Clean");
    assertThat(pageSource()).contains("Return to Home");
  }
  @Test
  public void clientUpdate() {
    Stylist myStylist = new Stylist("Home");
    myStylist.save();
    Client myClient = new Client("Clean", myStylist.getId());
    myClient.save();
    String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
    goTo(clientPath);
    fill("#name").with("Dance");
    submit("#update-client");
    assertThat(pageSource()).contains("Dance");
  }

  @Test
  public void clientDelete() {
    Stylist myStylist = new Stylist("Home");
    myStylist.save();
    Client myClient = new Client("Clean", myStylist.getId());
    myClient.save();
    String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
    goTo(clientPath);
    submit("#delete-client");
    assertEquals(0, Client.all().size());
  }

}
