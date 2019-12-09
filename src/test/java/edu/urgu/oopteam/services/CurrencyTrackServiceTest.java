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

import java.util.List;

import static org.junit.Assert.*;
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
                new CurrencyTrackRequest(20.0, "USD", -1,user),
                new CurrencyTrackRequest(10.0, "GBP", 15,user)
        ));

        assertEquals(2, currencyTrackService.findAllByUserId((long) 0).size());
    }
}