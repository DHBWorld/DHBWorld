package dhbw.timetable.rapla.data.event;

import dhbw.timetable.rapla.date.DateUtilities;
import dhbw.timetable.rapla.data.time.TimelessDate;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Deprecated
public class BackportAppointment extends Lecture {

    private GregorianCalendar start, end;

    public BackportAppointment(String time, TimelessDate date, String title, String persons, String resources) {
        super(title, persons, resources);
        start = (GregorianCalendar) date.clone();
        end = (GregorianCalendar) date.clone();

        String[] times = time.split("-");
        start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0].split(":")[0]));
        start.set(Calendar.MINUTE, Integer.parseInt(times[0].split(":")[1]));
        end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[1].split(":")[0]));
        end.set(Calendar.MINUTE, Integer.parseInt(times[1].split(":")[1]));
    }

    public BackportAppointment(GregorianCalendar startDate, GregorianCalendar endDate, String title, String persons, String resources) {
        super(title, persons, resources);
        this.start = (GregorianCalendar) startDate.clone();
        this.end = (GregorianCalendar) endDate.clone();
    }

    public String getStartTime() {
        return DateUtilities.GERMAN_STD_STIMEFORMAT.format(start.getTime());
    }

    public String getEndTime() {
        return DateUtilities.GERMAN_STD_STIMEFORMAT.format(end.getTime());
    }

    public String getDate() {
        return DateUtilities.GERMAN_STD_SDATEFORMAT.format(start.getTime());
    }

    /**
     * Includes time
     * @return The internal field
     */
    public GregorianCalendar getStartDate() {
        return start;
    }

    /**
     * Includes time
     * @return The internal field
     */
    public GregorianCalendar getEndDate() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null) {
            if (o instanceof BackportAppointment) {
                BackportAppointment that = (BackportAppointment) o;
                return that.toString().equals(this.toString());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getDate() + "\t"+ getStartTime() + "-" + getEndTime() + "\t" + title + "\t" + persons + "\t" + resources;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
