package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author FaAmbros
 */

@Entity
@Table(name = "TokenKey")
public class TokenKey {

    @Id
    @Column(name = "TokenKeyId")
    @GeneratedValue
    private Long tokenKeyId;

    @Lob
    @Size(min = 1, max = 10000, message = "1-10000 letters and spaces")
    @Column(name = "KeyValue")
    private String keyValue;

    public Long getTokenKeyId() {
        return tokenKeyId;
    }

    public void setTokenKeyId(Long tokenKeyId) {
        this.tokenKeyId = tokenKeyId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

}
