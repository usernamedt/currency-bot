package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.User;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    User getFirstByChatId(long chatId);
    User createUser(long chatId);
    void setLanguage(long chatId, String languageCode);
}
