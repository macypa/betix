package betix.core.data;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

@Data
public class AccountInfo {

    private double balance;
    private Set<MatchInfo> matchInfoPending = new TreeSet<>();
    private Set<MatchInfo> matchInfoFinished = new TreeSet<>();

    public String getFromDate(SimpleDateFormat format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date fromDate = calendar.getTime();

        for (MatchInfo info : matchInfoPending) {
            try {
                Date date = format.parse(info.getDate());
                if (date.before(fromDate)) {
                    fromDate = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (MatchInfo info : matchInfoFinished) {
            try {
                Date date = format.parse(info.getDate());
                if (date.before(fromDate)) {
                    fromDate = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return format.format(fromDate);
    }

    public String getToDate(SimpleDateFormat format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return format.format(calendar.getTime());
    }
}
