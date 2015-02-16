package betix.core.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"date", "state", "coefficient", "stake", "wining"})
public class MatchInfo implements Comparable {

    private MatchState state = MatchState.pending;
    private double coefficient;
    private double stake;
    private double wining;
    private String date;
    private String dateOfBet;
    private Event event;

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof MatchInfo)) return 1;
        final MatchInfo other = (MatchInfo) o;

        if (!this.getDateOfBet().equals(other.getDateOfBet())) {
            return getDateOfBet().compareTo(other.getDateOfBet());
        } else if (!getEvent().getName().equals(other.getEvent().getName())) {
            return getEvent().getName().compareTo(other.getEvent().getName());
        }
        return 0;
    }
}
