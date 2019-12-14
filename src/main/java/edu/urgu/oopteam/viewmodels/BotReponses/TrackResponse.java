package edu.urgu.oopteam.viewmodels.BotReponses;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;

public class TrackResponse implements IBotResponse {
    public final String responseBody;
    public final CurrencyTrackRequest currencyTrackRequest;

    public TrackResponse(CurrencyTrackRequest currencyTrackRequest, String responseBody) {
        this.currencyTrackRequest = currencyTrackRequest;
        this.responseBody = responseBody;
    }

    @Override
    public String getMessage() {
        return responseBody + "\n" + currencyTrackRequest.toString();
    }
}
