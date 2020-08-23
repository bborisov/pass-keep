package pass.keep.entities;

import lombok.Data;
import lombok.ToString;
import pass.keep.dto.CredentialsDto;
import pass.keep.utils.CipherUtil;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Base64;

@Data
public class CredentialsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private String username;
    @ToString.Exclude
    private String password;
    @ToString.Exclude
    private String initVector;
    @ToString.Exclude
    private String secretKey;

    public static CredentialsEntity of(CredentialsDto dto) {
        byte[] initVector = dto.getInitVector();
        SecretKey secretKey = dto.getSecretKey();
        if (dto.getInitVector() == null || dto.getSecretKey() == null) {
            initVector = CipherUtil.getInitVector();
            secretKey = CipherUtil.getSecretKey();
        }
        byte[] encryptedPassword = CipherUtil.encrypt(dto.getPassword().getBytes(), initVector, secretKey);

        CredentialsEntity entity = new CredentialsEntity();
        entity.setDescription(dto.getDescription());
        entity.setUsername(dto.getUsername());
        entity.setPassword(Base64.getEncoder().encodeToString(encryptedPassword));
        entity.setInitVector(Base64.getEncoder().encodeToString(initVector));
        entity.setSecretKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        return entity;
    }
}
