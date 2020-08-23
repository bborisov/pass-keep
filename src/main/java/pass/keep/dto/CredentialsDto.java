package pass.keep.dto;

import lombok.Getter;
import lombok.Setter;
import pass.keep.entities.CredentialsEntity;
import pass.keep.utils.CipherUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Getter
@Setter
public class CredentialsDto {

    private String description;
    private String username;
    private String password;
    private byte[] initVector;
    private SecretKey secretKey;

    public static CredentialsDto of(CredentialsEntity entity) {
        byte[] encryptedPassword = Base64.getDecoder().decode(entity.getPassword());
        byte[] initVector = Base64.getDecoder().decode(entity.getInitVector());
        byte[] secretKeyBytes = Base64.getDecoder().decode(entity.getSecretKey());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, CipherUtil.ALGORITHM);
        byte[] decryptedPassword = CipherUtil.decrypt(encryptedPassword, initVector, secretKey);

        CredentialsDto dto = new CredentialsDto();
        dto.setDescription(entity.getDescription());
        dto.setUsername(entity.getUsername());
        dto.setPassword(new String(decryptedPassword));
        dto.setInitVector(initVector);
        dto.setSecretKey(secretKey);
        return dto;
    }
}
