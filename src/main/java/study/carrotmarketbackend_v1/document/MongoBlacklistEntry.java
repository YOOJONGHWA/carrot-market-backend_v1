package study.carrotmarketbackend_v1.document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "blacklist")
@Getter
@AllArgsConstructor
public class MongoBlacklistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String userId;

    private String reason;

    private Date createDate;

    private Date expiryDate;

    private int banDurationDays;
}
