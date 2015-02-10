package betix.core.data;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class AccountInfo {

    private double balance;
    private List<MatchInfo> matchInfoPending = new LinkedList<>();
    private List<MatchInfo> matchInfoFinished = new LinkedList<>();

}
