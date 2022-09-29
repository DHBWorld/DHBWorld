package dhbw.timetable.rapla.data.time;

import dhbw.timetable.rapla.date.DateUtilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Hendrik Ulbrich (C) 2017
 *
 * This class is intended to override the inconsistency of the GregorianCalendar class.
 * It provides productive equals methods and reliable hashing functions via ignoring all
 * attributes except dayOfMonth, month and year.
 */
public class TimelessDate extends GregorianCalendar {

    public TimelessDate() {
        this((GregorianCalendar) Calendar.getInstance());
    }

    public TimelessDate(GregorianCalendar cal) {
        super();
        this.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof  GregorianCalendar) {
            GregorianCalendar otherObj = (GregorianCalendar) obj;
            return otherObj.get(Calendar.DAY_OF_MONTH) == this.get(Calendar.DAY_OF_MONTH)
                    && otherObj.get(Calendar.MONTH) == this.get(Calendar.MONTH)
                    && otherObj.get(Calendar.YEAR) == this.get(Calendar.YEAR);

        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return DateUtilities.GERMAN_STD_SDATEFORMAT.format(this.getTime());
    }

}
