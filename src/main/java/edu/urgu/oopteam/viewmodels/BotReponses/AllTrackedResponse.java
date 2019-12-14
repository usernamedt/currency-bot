package edu.urgu.oopteam.viewmodels.BotReponses;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;

import java.util.List;

public class AllTrackedResponse implements IBotResponse {

    public final List<CurrencyTrackRequest> requests;
    public final String messageBody;

    public AllTrackedResponse(List<CurrencyTrackRequest> requests, String messageBody) {
        this.requests = requests;
        this.messageBody = messageBody;
    }

    @Override
    public String getMessage() {
        var output = new StringBuilder();
        for (var request : requests) {
            output.append(request.toString()).append("\r\n");
        }
        return messageBody + " " + output.toString();
    }
}
