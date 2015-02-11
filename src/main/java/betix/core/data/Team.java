package betix.core.data;

import betix.core.config.ConfigKey;
import betix.core.config.Configuration;
import betix.core.config.ImagePattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sikuli.script.Pattern;

import java.io.File;

@Data
@EqualsAndHashCode(exclude = {"image", "pattern"})
public class Team {

    private String name;
    private File image;
    private Pattern pattern;

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
    }

    public boolean isParticipant(String participant) {
        if (participant == null || participant.isEmpty() || name == null || name.isEmpty()) return false;
        return name.toLowerCase().equals(participant.toLowerCase());
    }
}
