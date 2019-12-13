package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getFirstByChatId(long chatId);
    @Override
    void deleteAll();
}
