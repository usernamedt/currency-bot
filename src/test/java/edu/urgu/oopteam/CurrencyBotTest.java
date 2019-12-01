package edu.urgu.oopteam;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CurrencyBotTest {
    FileService fileService = new FileService();

    @MockBean
    ICurrencyTrackService currencyTrackService;
    @MockBean
    ITranslationService localizer;
    @MockBean
    IUserService userService;
    @MockBean
    ICurrencyCashExchangeService currencyCashExchangeService;
    @MockBean
    IMessenger messenger;
    @MockBean
    WebService webService;

    @Autowired
    CurrencyBot currencyBot;

    @Test
    public void testSendTrackCommand() {
        var user = new User(1, "ru");
        var response = currencyBot.handleTrackCommand("/track USD -1", user);

        assertEquals("New request added\n" +
                        "CurrencyTrackRequest [id= 0, userId= 0, baseRate= 64.082, currencyCode= usd, delta= -1",
                response);
    }


    @Before
    public void setUp() throws Exception {
        //new FieldSetter(currencyBot, CurrencyBot.class.getDeclaredField("currModel")).set(new Curr);
//        initMocks();


        // to load currency data from JSON instead of URL
        var jsonString = fileService.readResourceFileAsString("daily_json.json");
        var mapper = new ObjectMapper();
        currencyBot.currModel = mapper.readValue(jsonString, CurrenciesJsonModel.class);

        when(this.webService.getPageAsString("https://www.cbr-xml-daily.ru/daily_json.js", "UTF-8"))
                .thenReturn(jsonString);

        when(this.localizer.localize(anyString(), anyString()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(this.currencyTrackService.addTrackedCurrency(anyDouble(), anyString(), anyDouble(), any(User.class)))
                .thenAnswer(i -> new CurrencyTrackRequest(
                        (double) i.getArguments()[0],
                        (String) i.getArguments()[1],
                        (double) i.getArguments()[2],
                        (User) i.getArguments()[3]));


        // to disable notifyUsers()
        when(currencyTrackService.findAll()).thenReturn(new ArrayList<>());

    }

}
