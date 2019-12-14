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
import org.assertj.core.util.BigDecimalComparator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

        cleanDatabase();
    }

    @Test
    public void testExchangeCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/exchange usd moskva");
        var expectedResponse = new BuySellExchangeRates(
                new ExchangeData("Агророс", new BigDecimal("63.70")),
        new ExchangeData("Премьер БКС", new BigDecimal("63.89"))
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
        var expectedResponse = new CurrResponse(new BigDecimal("64.082"));

        var response = (CurrResponse) currencyBot.handleCurrCommand(message);

        Assert.assertTrue(new ReflectionEquals(expectedResponse).matches(response));
    }


    @Test
    public void testTrackCommand() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/track usd rub -10");
        var expectedRequest = new CurrencyTrackRequest(new BigDecimal("64.08"),
                "usd", "rub", new BigDecimal(-10), user);

        var response = (TrackResponse) currencyBot.handleTrackCommand(message);

        assertEquals(response.currencyTrackRequest.getDelta(), expectedRequest.getDelta());
        assertEquals(response.currencyTrackRequest.getFirstCurrencyCode(), expectedRequest.getFirstCurrencyCode());
        // С этой строчкой иногда работает, иногда нет wtf
        assertEquals(response.currencyTrackRequest.getUser().getChatId(), expectedRequest.getUser().getChatId());
    }

    @Test
    public void testUntrackCommand_NotFound() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/untrack usd rub");
        var expectedRequest = new StringResponse("No such currency in the tracked list");

        var response = (StringResponse) currencyBot.handleUntrackCommand(message);

        assertEquals(expectedRequest.getMessage(), response.getMessage());
    }


    @Test
    public void testUntrackCommand_Exists() {
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/untrack usd rub");
        var expectedRequest = new CurrencyTrackRequest(new BigDecimal("64.08"), "usd",
                "rub", new BigDecimal(-10), user);


        var trackMessage = new Message(user.getChatId(), "/track usd rub -10");

        currencyBot.handleTrackCommand(trackMessage);

        var response = (TrackResponse) currencyBot.handleUntrackCommand(message);

        assertEquals(response.currencyTrackRequest.getDelta().stripTrailingZeros(),
                expectedRequest.getDelta().stripTrailingZeros());
        assertEquals(response.currencyTrackRequest.getFirstCurrencyCode(), expectedRequest.getFirstCurrencyCode());
        assertEquals(response.currencyTrackRequest.getUser().getChatId(), expectedRequest.getUser().getChatId());
    }

    @Test
    public void testAllTrackedCommand() {
        cleanDatabase();
        var user = new User(1, Language.RUSSIAN);
        var message = new Message(user.getChatId(), "/allTracked");
        currencyBot.handleTrackCommand(new Message(user.getChatId(),  "/track usd rub 3"));
        currencyBot.handleTrackCommand(new Message(user.getChatId(),  "/track eur rub 5"));

        var expectedRequests = List.of(
                new CurrencyTrackRequest(new BigDecimal("64.08"), "usd", "rub",  new BigDecimal(3), user),
                new CurrencyTrackRequest(new BigDecimal("70.55"), "eur", "rub", new BigDecimal(5), user)
        );

        var actualResponse = (AllTrackedResponse) currencyBot.handleAllTrackedCommand(message);

        assertThat(expectedRequests).usingElementComparatorIgnoringFields("id", "user")
                .usingComparatorForType(new BigDecimalComparator(), BigDecimal.class)
                .containsAll(actualResponse.requests);
        assertThat(actualResponse.requests).usingElementComparatorIgnoringFields("id", "user")
                .usingComparatorForType(new BigDecimalComparator(), BigDecimal.class)
                .containsAll(expectedRequests);
    }

    @After
    public void cleanUp() {
        cleanDatabase();
    }

    @Transactional
    public void cleanDatabase() {
        userRepository.deleteAll();
        userRepository.flush();

        cashExchangeRateRepository.deleteAll();
        cashExchangeRateRepository.flush();

        currencyTrackRequestRepository.deleteAll();
        currencyTrackRequestRepository.flush();
    }
}
