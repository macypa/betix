package betix.core.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(exclude = {"date", "state", "coefficient", "stake", "wining"})
public class MatchInfo implements Comparable {

    private MatchState state = MatchState.pending;
    private double coefficient;
    private double stake;
    private double wining;
    private Date date;
    private Event event;

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof MatchInfo)) return 1;
        final MatchInfo other = (MatchInfo) o;

        if (!this.getDate().equals(other.getDate())) {
            return getDate().compareTo(other.getDate());
        } else if (!getEvent().getName().equals(other.getEvent().getName())) {
            return getEvent().getName().compareTo(other.getEvent().getName());
        }
        return 0;
    }
}
