package betix.core.data;

import lombok.Data;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
public class AccountInfo {

    private double balance;
    private Set<MatchInfo> matchInfoPending = new TreeSet<>(new MatchInfoComparator());
    private Set<MatchInfo> matchInfoFinished = new TreeSet<>(new MatchInfoComparator());

    class MatchInfoComparator implements Comparator<MatchInfo> {
        @Override
        public int compare(MatchInfo match1, MatchInfo match2) {
            if (!match1.getDate().equals(match2.getDate())) {
                return match1.getDate().compareTo(match2.getDate());
            } else if (!match1.getEvent().getName().equals(match2.getEvent().getName())) {
                return match1.getEvent().getName().compareTo(match2.getEvent().getName());
            }
            return 0;
        }
    }
}
