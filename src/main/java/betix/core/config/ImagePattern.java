package betix.core.config;

import org.sikuli.script.Pattern;

import java.io.File;
import java.util.regex.Matcher;

public enum ImagePattern {
    PATTERN_FOOTBALL_LINK("football"),
    PATTERN_FOOTBALL_DRAW_BET_LINK("footballDrawBetLink"),
    PATTERN_FOOTBALL_END_RESULT_COLUMN("footballEndResultColumn"),
    PATTERN_FOOTBALL_MY_TEAMS_LINK("footballMyTeams"),
    PATTERN_FOOTBALL_STAKE_FIELD("footballStakeField"),
    PATTERN_FOOTBALL_TEAM_LINK("footballTeamLink"),
    PATTERN_HISTORY_LINK("historyLink"),
    PATTERN_HISTORY_TITLE("historyTitle"),
    PATTERN_LIVE_TV_TITLE("liveTVTitle"),
    PATTERN_LIVE_TV_STOP_BUTTON("liveTVStopButton"),
    PATTERN_LOGIN_FIELD("loginField"),
    PATTERN_LOGO("logo"),
    PATTERN_LOGO_IN_TAB("logoInBrowserTab"),
    PATTERN_PASSWORD_FIELD("passwordField"),
    PATTERN_PLACE_BET_BUTTON("placeBetButton");

    public static final String TEAM_DIR_NAME = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageDir)
            + File.separator + Configuration.getDefaultConfig().getConfigAsString(ConfigKey.siteName) + File.separator
            + File.separator + Configuration.getDefaultConfig().getConfigAsString(ConfigKey.teamImageDir) + File.separator;

    String imageName = "";
    public final Pattern pattern;

    private ImagePattern(String imageName) {
        String imageExt = Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageExt);
        this.imageName = imageName + imageExt;

        float similarity = Configuration.getDefaultConfig().getConfigAsDouble(ConfigKey.sikuliMinSimilarity).floatValue();

        File defaultDir = new File(Configuration.getDefaultConfig().getConfigAsString(ConfigKey.imageDir),
                Configuration.getDefaultConfig().getConfigAsString(ConfigKey.siteName));

        for (File file : defaultDir.listFiles()) {
            if (file.isDirectory())
                continue;

            String fileName = file.getName();
            String similarityString = searchRegEx(fileName, imageName + "_(\\d*\\.\\d*)" + imageExt);
            if (!similarityString.isEmpty()) {
                this.imageName = fileName;
                similarity = Double.valueOf(similarityString).floatValue();
                break;
            }
        }

        this.pattern = new Pattern(new File(defaultDir, this.imageName).getPath()).similar(similarity);
    }

    private String searchRegEx(String info, String regex) {
        java.util.regex.Pattern MY_PATTERN = java.util.regex.Pattern.compile(regex);
        Matcher m = MY_PATTERN.matcher(info);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }
}
