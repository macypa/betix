package betix.core.data;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import betix.core.config.Stake;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sikuli.script.Pattern;

import java.io.File;

@Data
@EqualsAndHashCode(exclude = {"image", "pattern", "stake", "nextStake"})
public class Team implements Comparable {

    private String name;
    private File image;
    private Pattern pattern;
    private Stake stake;
    private Stake nextStake;

    public Team() {
    }

    public Team(String name) {
        if (name == null) return;

        String imageExt = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageExt);
        if (name.contains(imageExt)) {
            this.name = name.replace(imageExt, "");
        } else {
            this.name = name;
        }

        this.image = new File(ImagePattern.TEAM_DIR_NAME, this.name + imageExt);
        this.pattern = new Pattern(image.getPath());
        this.stake = Stake.stake1;
        this.stake = Stake.stake2;
    }

    public boolean isParticipant(String participant) {
        if (participant == null || participant.isEmpty() || name == null || name.isEmpty()) return false;
        return name.toLowerCase().replaceAll("_| ", "").equals(participant.toLowerCase().replaceAll("_| ", ""));
    }

    void calculateStakes(Stake stake) {
        this.stake = stake;
        this.nextStake = stake.next();
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Team)) return 1;
        final Team other = (Team) o;

        if (!this.getName().equals(other.getName())) {
            return this.getName().compareTo(other.getName());
        }
        return 0;
    }
}
