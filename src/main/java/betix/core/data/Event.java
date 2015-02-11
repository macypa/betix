package betix.core.data;

import lombok.Data;

@Data
public class Event {

    private String name;
    private Team firstTeam;
    private Team secondTeam;

    public Event() {
    }

    public Event(String name) {
        this.name = name;
        this.firstTeam = new Team(name.substring(name.indexOf(" v ") + 3).trim());
        this.secondTeam = new Team(name.substring(0, name.indexOf(" v ")).trim());
    }

    public boolean isParticipant(String participant) {
        return firstTeam.isParticipant(participant) || secondTeam.isParticipant(participant);
    }
}
