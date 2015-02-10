package betix.core.data;

import betix.core.ConfigKey;
import betix.core.Configuration;
import org.sikuli.script.Pattern;

import java.io.File;

public enum ImagePattern {
    PATTERN_UNMAXIMIZE("unmaximise.png", ConfigKey.imageDir, 0.5f),
    PATTERN_FOOTBALL_LINK("football.png", 0.5f),
    PATTERN_FOOTBALL_DRAW_BET_LINK("footballDrawBetLink.png"),
    PATTERN_FOOTBALL_MY_TEAMS_LINK("footballMyTeams.png"),
    PATTERN_FOOTBALL_STAKE_FIELD("footballStakeField.png"),
    PATTERN_FOOTBALL_TEAM_LINK("footballTeamLink.png"),
    PATTERN_HISTORY_TITLE("historyTitle.png", 0.5f),
    PATTERN_HISTORY_LINK("historyLink.png"),
    PATTERN_LOGIN_FIELD("loginField.png", 0.5f),
    PATTERN_LOGO("logo.png", 0.5f),
    PATTERN_LOGO_IN_TAB("logoInBrowserTab.png", 0.5f),
    PATTERN_LOGOUT_LINK("logoutLink.png"),
    PATTERN_PASSWORD_FIELD("passwordField.png", 0.5f);


    public final Configuration config = new Configuration();
    private final File DEFAULT_DIR = new File(config.getConfigAsString(ConfigKey.imageDir),
            config.getConfigAsString(ConfigKey.siteName));

    String imageName = "";
    public Pattern pattern;

    private ImagePattern(String imageName) {
        this(imageName, 0.7f);
    }

    private ImagePattern(String imageName, float similarity) {
        this.imageName = imageName;
        this.pattern = new Pattern(new File(DEFAULT_DIR, imageName).getPath()).similar(similarity);
    }

    private ImagePattern(String imageName, ConfigKey imageDir, float similarity) {
        this.imageName = imageName;
        File directory = new File(config.getConfigAsString(imageDir));
        this.pattern = new Pattern(new File(directory, imageName).getPath()).similar(similarity);
    }

}
