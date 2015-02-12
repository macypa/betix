package betix.core.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * ===Текущ===
 * <p>
 * Потвърждение на Залога  - CP2900590410I  - Интернет   Час на залога:  09/02/2015 20:19:33 	Принтирай
 * Селекции
 * Ho. 	Селекции 	Събитие 	Дата на Събитието 	Условия на Подсигурен Залог 	Коефициент 	Резултат
 * 1 	Равен 	Бъртън Албиън v Оксфорд Юн
 * (Краен Резултат) 	14/02/2015 	Никой 	3.60 	Текущ
 * Залог:  0,50   Печели:  1,80
 * <p>
 * <p>
 * ===Загубен===
 * <p>
 * Потвърждение на Залога  - LA6214935010I  - Интернет   Час на залога:  03/02/2015 16:42:41 	Принтирай
 * Селекции
 * Ho. 	Селекции 	Събитие 	Дата на Събитието 	Условия на Подсигурен Залог 	Коефициент 	Резултат
 * 1 	Равен 	Фулъм v Съндърланд
 * (Краен Резултат) 	03/02/2015 	Никой 	3.40 	Загубен
 * Залог:  0,50   Печалби:  0,00
 * <p>
 * <p>
 * ===Печеливш===
 * <p>
 * Потвърждение на Залога  - AA6012942710I  - Интернет   Час на залога:  03/02/2015 11:55:45 	Принтирай
 * Селекции
 * Ho. 	Селекции 	Събитие 	Дата на Събитието 	Условия на Подсигурен Залог 	Коефициент 	Резултат
 * 1 	Равен 	Лион v ПСЖ
 * (Краен Резултат) 	08/02/2015 	Никой 	3.30 	Печеливш
 * Залог:  0,50   Печалби:  1,65
 */
@Data
@EqualsAndHashCode(exclude = {"state", "coefficient", "stake", "wining"})
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
