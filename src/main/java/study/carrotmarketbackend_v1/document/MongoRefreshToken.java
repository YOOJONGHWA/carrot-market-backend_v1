package study.carrotmarketbackend_v1.document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refreshTokens")
public class MongoRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String  id;
    private String memberId;
    private String username;
    private String token;
    private Date expiration;

}
