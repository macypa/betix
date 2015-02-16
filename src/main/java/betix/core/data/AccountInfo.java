package betix.core.data;

import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
public class AccountInfo {

    private double balance;
    private Set<MatchInfo> matchInfoPending = new TreeSet<>();
    private Set<MatchInfo> matchInfoFinished = new TreeSet<>();

}
