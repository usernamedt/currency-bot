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
     * @return Newly created user
     */
    User createUser(long chatId);

    /**
     * Sets new language for a user
     *
     * @param chatId       User's chat ID
     * @param languageCode Code of the language
     */
    void setLanguage(long chatId, String languageCode);

    /**
     * Load existing user or create new and return it
     *
     * @param chatId       User's chat ID
     */
    default User getExistingOrNewUser(long chatId){
        var user = getFirstByChatId(chatId);
        if (user == null) {
            user = createUser(chatId);
        }
        return user;
    }

}
