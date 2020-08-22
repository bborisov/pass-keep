package pass.keep.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
public class CredentialsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private String username;
    @ToString.Exclude
    private String password;
}
