package tn.sip.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.sip.user_service.entities.Token;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.enums.TokenType;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user = :user AND t.tokenType = :tokenType")
    void deleteByUserAndTokenType(@Param("user") User user, @Param("tokenType") TokenType tokenType);

    Token findByUserAndTokenType(User createdUser, TokenType tokenType);
}
