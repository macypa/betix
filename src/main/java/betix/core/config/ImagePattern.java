package betix.core.config;

import org.sikuli.script.Pattern;

import java.io.File;

public enum ImagePattern {
    PATTERN_UNMAXIMIZE("unmaximise.png", ConfigKey.imageDir, 0.5f),
    PATTERN_FOOTBALL_LINK("football.png", 0.5f),
    PATTERN_FOOTBALL_DRAW_BET_LINK("footballDrawBetLink.png"),
    PATTERN_FOOTBALL_END_RESULT_COLUMN("footballEndResultColumn.png"),
    PATTERN_FOOTBALL_MY_TEAMS_LINK("footballMyTeams.png"),
    PATTERN_FOOTBALL_STAKE_FIELD("footballStakeField.png", 0.6f),
    PATTERN_FOOTBALL_TEAM_LINK("footballTeamLink.png", 0.7f),
    PATTERN_HISTORY_TITLE("historyTitle.png", 0.5f),
    PATTERN_HISTORY_LINK("historyLink.png"),
    PATTERN_LOGIN_FIELD("loginField.png", 0.5f),
    PATTERN_LOGO("logo.png", 0.5f),
    PATTERN_LOGO_IN_TAB("logoInBrowserTab.png", 0.5f),
    PATTERN_PASSWORD_FIELD("passwordField.png", 0.5f);


    private final File DEFAULT_DIR = new File(Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageDir),
            Configuration.getDefaultConfig().getConfigAsString(ConfigKey.siteName));

    public static final String TEAM_DIR_NAME = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageDir)
            + File.separator + Configuration.getDefaultConfig().getConfigAsString(ConfigKey.siteName) + File.separator
            + File.separator + Configuration.getDefaultConfig().getConfigAsString(ConfigKey.teamImageDir) + File.separator;

    String imageName = "";
    public final Pattern pattern;

    private ImagePattern(String imageName) {
        this(imageName, 0.7f);
    }

    private ImagePattern(String imageName, float similarity) {
        this.imageName = imageName;
        this.pattern = new Pattern(new File(DEFAULT_DIR, imageName).getPath()).similar(similarity);
    }

    private ImagePattern(String imageName, ConfigKey imageDir, float similarity) {
        this.imageName = imageName;
        File directory = new File(Configuration.getDefaultConfig().getConfigAsString(imageDir));
        this.pattern = new Pattern(new File(directory, imageName).getPath()).similar(similarity);
    }

}
