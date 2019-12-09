package edu.urgu.oopteam.services;

import edu.urgu.oopteam.Language;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User getFirstByChatId(long chatId) {
        return userRepository.getFirstByChatId(chatId);
    }

    @Override
    public User createUser(long chatId) {
        var user = new User(chatId, Language.ENGLISH);
        userRepository.save(user);
        return user;
    }

    @Override
    public void setLanguage(long chatId, Language language) {
        var user = userRepository.getFirstByChatId(chatId);
        user.setLanguage(language);
        userRepository.save(user);
    }
}
