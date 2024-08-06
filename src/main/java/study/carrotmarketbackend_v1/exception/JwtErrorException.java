package study.carrotmarketbackend_v1.exception;

import lombok.Getter;

@Getter
public class JwtErrorException extends RuntimeException{

    private final JwtErrorCode error;

    public JwtErrorException(JwtErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }

}
