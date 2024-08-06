package study.carrotmarketbackend_v1.document;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MongoRefreshTokenRepository extends MongoRepository<MongoRefreshToken,String> {

    Optional<MongoRefreshToken> findByToken(String refresh);

    // 사용자 이름으로 토큰을 찾습니다.
    Optional<MongoRefreshToken> findByUsername(String username);


    @Transactional
    void deleteByUsername(String username);

    @Transactional
    void deleteByToken(String refresh);

}
