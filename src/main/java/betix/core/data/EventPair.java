package betix.core.data;

import lombok.Data;

@Data
public class EventPair {

    private String name;
    private Team firstTeam;
    private Team secondTeam;
}
