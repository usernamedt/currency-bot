package edu.urgu.oopteam.viewmodels.BotReponses;

public class StringResponse implements IBotResponse {
    private String responseBody;

    public StringResponse(String responseBody){
        this.responseBody = responseBody;
    }
    @Override
    public String getMessage() {
        return responseBody;
    }
}
