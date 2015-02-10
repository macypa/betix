package betix.core.data;

import betix.core.ConfigKey;
import betix.core.Configuration;
import lombok.Data;
import org.sikuli.script.Pattern;

import java.io.File;

@Data
public class Team {

    public final Configuration config = new Configuration();
    private final String TEAM_DIR_NAME = config.getConfigAsString(ConfigKey.imageDir)
            + File.separator + config.getConfigAsString(ConfigKey.siteName) + File.separator
            + File.separator + config.getConfigAsString(ConfigKey.teamImageDir) + File.separator;

    private String name;
    private File image;
    private Pattern pattern;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
        this.image = new File(TEAM_DIR_NAME, name + config.getConfigAsString(ConfigKey.imageExt));
        this.pattern = new Pattern(image.getPath());
    }

}
