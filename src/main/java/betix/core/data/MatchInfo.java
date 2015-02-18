package betix.core.data;

public class MatchInfo implements Comparable {

    private MatchState state = MatchState.pending;
    private double coefficient;
    private double stake;
    private double wining;
    private String date;
    private String dateOfBet;
    private Event event;

    public MatchInfo() {
    }

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

    public MatchState getState() {
        return this.state;
    }

    public double getCoefficient() {
        return this.coefficient;
    }

    public double getStake() {
        return this.stake;
    }

    public double getWining() {
        return this.wining;
    }

    public String getDate() {
        return this.date;
    }

    public String getDateOfBet() {
        return this.dateOfBet;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }

    public void setWining(double wining) {
        this.wining = wining;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDateOfBet(String dateOfBet) {
        this.dateOfBet = dateOfBet;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String toString() {
        return "betix.core.data.MatchInfo(state=" + this.state + ", coefficient=" + this.coefficient + ", stake=" + this.stake + ", wining=" + this.wining + ", date=" + this.date + ", dateOfBet=" + this.dateOfBet + ", event=" + this.event + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MatchInfo)) return false;
        final MatchInfo other = (MatchInfo) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$dateOfBet = this.getDateOfBet();
        final Object other$dateOfBet = other.getDateOfBet();
        if (this$dateOfBet == null ? other$dateOfBet != null : !this$dateOfBet.equals(other$dateOfBet)) return false;
        final Object this$event = this.getEvent();
        final Object other$event = other.getEvent();
        if (this$event == null ? other$event != null : !this$event.equals(other$event)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dateOfBet = this.getDateOfBet();
        result = result * PRIME + ($dateOfBet == null ? 0 : $dateOfBet.hashCode());
        final Object $event = this.getEvent();
        result = result * PRIME + ($event == null ? 0 : $event.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof MatchInfo;
    }
}
