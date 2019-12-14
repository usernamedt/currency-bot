package edu.urgu.oopteam.services;

import edu.urgu.oopteam.Language;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyTrackServiceTest {
    @Mock
    CurrencyTrackRequestRepository currencyTrackRequestRepository;

    @InjectMocks
    CurrencyTrackService currencyTrackService;

    @Test
    public void testFindAllByChatId() {
        var user = new User(1, Language.RUSSIAN);
        when(currencyTrackRequestRepository.getAllByUserId((long) 0)).thenReturn(List.of(
                new CurrencyTrackRequest(new BigDecimal(20.0),
                        "RUB", "USD", new BigDecimal(-1), user),
                new CurrencyTrackRequest(new BigDecimal(10.0),
                        "RUB", "GBP", new BigDecimal(15), user)
        ));

        assertEquals(2, currencyTrackService.findAllByUserId((long) 0).size());
    }
}