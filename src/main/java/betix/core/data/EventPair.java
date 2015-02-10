package betix.core.data;

import lombok.Data;

@Data
public class EventPair {

    private String name;
    private Team firstTeam;
    private Team secondTeam;

    public EventPair() {
    }

    public EventPair(String name) {
        this.name = name;
        this.firstTeam = new Team(name.substring(name.indexOf(" v ") + 3).trim());
        this.secondTeam = new Team(name.substring(0, name.indexOf(" v ")).trim());
    }

}
