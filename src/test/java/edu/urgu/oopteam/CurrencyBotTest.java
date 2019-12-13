package edu.urgu.oopteam;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CashExchangeRateRepository;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import edu.urgu.oopteam.crud.repository.UserRepository;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.*;
import edu.urgu.oopteam.viewmodels.BotReponses.*;
import edu.urgu.oopteam.viewmodels.BuySellExchangeRates;
import edu.urgu.oopteam.viewmodels.ExchangeData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CurrencyBotTest {
    @MockBean
    private ITranslationService localizer;
    @MockBean
    private IMessenger messenger;
    @MockBean
    private WebService webService;

    @Autowired
    private FileService fileService;
    @Autowired
    private CurrencyBot currencyBot;

    @Autowired
    private CashExchangeRateRepository cashExchangeRateRepository;
    @Autowired
    private CurrencyTrackRequestRepository currencyTrackRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    @Transactional
    public void setUp() throws Exception {
        // to load currency data from JSON instead of URL
        var jsonString = fileService.readResourceFileAsString("daily_json.json");
        var mapper = new ObjectMapper();
        currencyBot.currModel = mapper.readValue(jsonString, CurrenciesJsonModel.class);

        when(this.webService.getPageAsString("https://www.cbr-xml-daily.ru/daily_json.js","UTF-8"))
                .thenReturn(jsonString);

        var usdMoscowSample = fileService.readResourceFileAsString("usd_moscow.html");

        when(this.webService.getPageAsString(
                eq("https://banki.ru/products/currency/best_rates_summary/bank/usd/moskva/"),
                eq("UTF-8"), any()))
                .thenReturn(usdMoscowSample);

        when(this.localizer.localize(anyString(), any(Language.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        cashExchangeRateRepository.deleteAll();
//        currencyTrackRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testExchangeCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/exchange usd moskva");
        var expectedResponse = new BuySellExchangeRates(
                new ExchangeData("Агророс", 63.70),
        new ExchangeData("Премьер БКС", 63.89)
                );

        var response = (ExchangeResponse) currencyBot.handleExchangeCommand(message);


        Assert.assertTrue(new ReflectionEquals(expectedResponse.getBuyData())
                .matches(response.getBuySellExchangeRates().getBuyData()));
        Assert.assertTrue(new ReflectionEquals(expectedResponse.getSellData())
                .matches(response.getBuySellExchangeRates().getSellData()));
    }


    @Test
    public void testCurrCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/curr usd");
        var expectedResponse = new CurrResponse(64.0817);

        var response = (CurrResponse) currencyBot.handleCurrCommand(message);

        Assert.assertTrue(new ReflectionEquals(expectedResponse).matches(response));
    }


    @Test
    @Transactional
    public void testTrackCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/track usd -10");
        var expectedRequest = new CurrencyTrackRequest(64.0817, "usd", -10, user);

        var response = (TrackResponse) currencyBot.handleTrackCommand(message);

        assertEquals(response.currencyTrackRequest.getDelta(), expectedRequest.getDelta(), 0.001);
        assertEquals(response.currencyTrackRequest.getCurrencyCode(), expectedRequest.getCurrencyCode());
        // С этой строчкой иногда работает, иногда нет wtf
        assertEquals(response.currencyTrackRequest.getUser().getChatId(), expectedRequest.getUser().getChatId());
    }

    @Test
    public void testUntrackCommand_NotFound() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/untrack usd");
        var expectedRequest = new StringResponse("No such currency in the tracked list");

        var response = (StringResponse) currencyBot.handleUntrackCommand(message);

        assertEquals(expectedRequest.getMessage(), response.getMessage());
    }


    @Test
    @Transactional
    public void testUntrackCommand_Exists() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/untrack usd");
        var expectedRequest = new CurrencyTrackRequest(64.0817, "usd", -10, user);


        var trackMessage = new Message(user.getChatId(), "/track usd -10");

        currencyBot.handleTrackCommand(trackMessage);

        var response = (TrackResponse) currencyBot.handleUntrackCommand(message);

        assertEquals(response.currencyTrackRequest.getDelta(), expectedRequest.getDelta(), 0.001);
        assertEquals(response.currencyTrackRequest.getCurrencyCode(), expectedRequest.getCurrencyCode());
        assertEquals(response.currencyTrackRequest.getUser().getChatId(), expectedRequest.getUser().getChatId());
    }

    @Test
    public void testAllTrackedCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/allTracked");
        currencyBot.handleTrackCommand(new Message(user.getChatId(),  "/track usd 3"));
        currencyBot.handleTrackCommand(new Message(user.getChatId(),  "/track eur 5"));

        var expectedRequests = List.of(
                new CurrencyTrackRequest(64.0817, "usd", 3, user),
                new CurrencyTrackRequest(70.5475, "eur", 5, user)
        );

        var actualResponse = (AllTrackedResponse) currencyBot.handleAllTrackedCommand(message);

        assertThat(expectedRequests).usingElementComparatorIgnoringFields("id", "user").containsAll(actualResponse.requests);
        assertThat(actualResponse.requests).usingElementComparatorIgnoringFields("id", "user").containsAll(expectedRequests);
    }
}
