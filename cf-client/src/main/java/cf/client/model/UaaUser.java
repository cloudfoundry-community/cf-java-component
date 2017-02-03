package cf.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Created by forbushbl on 2/2/17.
 */
public class UaaUser {

    private Boolean active;
    private UUID id;
    private String source;
    private String username;

    public UaaUser(
            @JsonProperty("active") Boolean active,
            @JsonProperty("id") UUID id,
            @JsonProperty("source") String source,
            @JsonProperty("username") String username
    ) {
        this.active = active;
        this.id = id;
        this.source = source;
        this.username = username;
    }

    public Boolean getActive() {
        return active;
    }

    public UUID getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getUsername() {
        return username;
    }
}
