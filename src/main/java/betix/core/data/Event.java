package betix.core.data;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"firstTeam", "secondTeam"})
public class Event {

    private String name;
    private Team firstTeam;
    private Team secondTeam;

    public Event() {
    }

    public Event(String name) {
        this.name = name;
        String teamNameSeparator = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.teamNameSeparator);
        this.firstTeam = new Team(name.substring(name.indexOf(teamNameSeparator) + teamNameSeparator.length()).trim());
        this.secondTeam = new Team(name.substring(0, name.indexOf(teamNameSeparator)).trim());
    }

    public boolean isParticipant(String participant) {
        return firstTeam.isSame(participant) || secondTeam.isSame(participant);
    }

    public Team getOpponent(String team) {
        if (firstTeam.isSame(team)) {
            return secondTeam;
        }
        return firstTeam;
    }
}
