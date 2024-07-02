package com.kousenit.wordvocabulary.repository;

import com.kousenit.wordvocabulary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
}
