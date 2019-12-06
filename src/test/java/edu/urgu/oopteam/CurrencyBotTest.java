package edu.urgu.oopteam;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import edu.urgu.oopteam.crud.repository.UserRepository;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.*;
import edu.urgu.oopteam.viewmodels.BotReponses.ExchangeResponse;
import edu.urgu.oopteam.viewmodels.BotReponses.CurrResponse;
import edu.urgu.oopteam.viewmodels.BotReponses.StringResponse;
import edu.urgu.oopteam.viewmodels.BotReponses.TrackResponse;
import edu.urgu.oopteam.viewmodels.BuySellExchangeRates;
import edu.urgu.oopteam.viewmodels.ExchangeData;
import org.checkerframework.checker.units.qual.A;
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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class CurrencyBotTest {
    FileService fileService = new FileService();
    @MockBean
    ITranslationService localizer;
    @MockBean
    IMessenger messenger;
    @MockBean
    WebService webService;

    @Autowired
    CurrencyBot currencyBot;

    @Test
    public void testExchangeCommand() {
        var user = new User(1, "ru");
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
        var user = new User(1, "ru");
        var message = new Message(user.getChatId(), "/curr usd");
        var expectedResponse = new CurrResponse(64.0817);

        var response = (CurrResponse) currencyBot.handleCurrCommand(message);

        Assert.assertTrue(new ReflectionEquals(expectedResponse).matches(response));
    }


    @Test
    public void testTrackCommand() {
        var user = new User(1, "ru");
        var message = new Message(user.getChatId(), "/track usd -10");
        var expectedRequest = new CurrencyTrackRequest(64.0817, "usd", -10, user);

        var response = (TrackResponse) currencyBot.handleTrackCommand(message);

        assertEquals(response.currencyTrackRequest.getDelta(), expectedRequest.getDelta(), 0.001);
        assertEquals(response.currencyTrackRequest.getCurrencyCode(), expectedRequest.getCurrencyCode());
        assertEquals(response.currencyTrackRequest.getUser().getChatId(), expectedRequest.getUser().getChatId());
    }

    @Test
    public void testUntrackCommand_NotFound() {
        var user = new User(1, "ru");
        var message = new Message(user.getChatId(), "/untrack usd");
        var expectedRequest = new StringResponse("No such currency in the tracked list");

        var response = (StringResponse) currencyBot.handleUntrackCommand(message);

        assertEquals(expectedRequest.getMessage(), response.getMessage());
    }

    @Before
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

        when(this.localizer.localize(anyString(), anyString()))
                .thenAnswer(i -> i.getArguments()[0]);

    }
}
