package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.User;

import java.util.List;

public interface IUserService {
    List<User> findAll();

    User getFirstByChatId(long chatId);

    /**
     * Creates user in database with specified chat ID
     *
     * @param chatId
     * @return Just created user
     */
    User createUser(long chatId);

    /**
     * Sets new language for a user
     *
     * @param chatId       User's chat ID
     * @param languageCode Code of the language
     */
    void setLanguage(long chatId, String languageCode);
}
