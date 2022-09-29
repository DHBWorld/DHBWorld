package dhbw.timetable.rapla.date;

import dhbw.timetable.rapla.data.event.BackportAppointment;
import dhbw.timetable.rapla.data.time.TimelessDate;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Hendrik Ulbrich (C) 2017
 */
public final class DateUtilities {

    public static final SimpleDateFormat GERMAN_STD_SDATEFORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    public static final SimpleDateFormat GERMAN_STD_STIMEFORMAT = new SimpleDateFormat("HH:mm", Locale.GERMANY);

    private DateUtilities() {}

    public static DateTimeFormatter GERMAN_STD_DATEFORMATTER() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    }

    public static DateTimeFormatter GERMAN_STD_TIMEFORMATTER() {
        return DateTimeFormatter.ofPattern("HH:mm", Locale.GERMANY);
    }

    /**
     * Changes the day attribute to the last monday.
     * If the current day is already monday, nothing happens.
     *
     * @param src Date to normalize
     * @return Copy of the normalized date
     */
    public static LocalDate Normalize(LocalDate src) {
        return src.minusDays(src.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    /**
     * Clones the day, month and year parameter.
     *
     * @param src The date to clone
     * @return A new local date instance
     */
	public static LocalDate Clone(LocalDate src) {
		return LocalDate.of(src.getYear(), src.getMonth(), src.getDayOfMonth());
	}

    /**
     * Clones the minute, hour, day, month and year parameter.
     *
     * @param src The date to clone
     * @return A new local date instance
     */
	public static LocalDateTime Clone(LocalDateTime src) {
		return LocalDateTime.of(src.getYear(), src.getMonth(), src.getDayOfMonth(), src.getHour(), src.getMinute());
	}

    /**
     * Converts a event range and a day into two LocalDateTime elements.
     *
     * @param date The day of the two times
     * @param times A event range in form 12:00-14:00
     * @return LocalDateTime[] with two elements
     */
	public static LocalDateTime[] ConvertToTime(LocalDate date, String times) {
		String[] timesArray = times.split("-");
		String[] startTime = timesArray[0].split(":");
		String[] endTime = timesArray[1].split(":");
				
		final LocalDateTime start = date.atTime(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
		final LocalDateTime end = date.atTime(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));

        return new LocalDateTime[]{ start, end };
	}

	/**
	 * Converts java.event.LocalDateTime objects to java.util.Date objects
	 * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
	 *
	 * @param date the date to convert
	 * @return new instance of type Date
	 */
	public static Date ConvertToDate(LocalDateTime date) {
        return ConvertToCalendar(date).getTime();
    }

    /**
     * Converts java.event.LocalDate objects to java.util.Date objects
     * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
     *
     * @param date the date to convert
     * @return new instance of type Date
     */
    public static Date ConvertToDate(LocalDate date) {
        return ConvertToCalendar(date).getTime();
    }

    /**
     * Converts java.event.LocalDateTime objects to calendar objects
     * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
     *
     * @param date The date to convert
     * @return an instance of GregorianCalendar
     */
    public static GregorianCalendar ConvertToCalendar(LocalDateTime date) {
        GregorianCalendar tempCal = (GregorianCalendar) Calendar.getInstance(Locale.GERMANY);
        tempCal.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        tempCal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), date.getHour(), date.getMinute(), 0);
        return tempCal;
    }

    /**
     * Converts java.event.LocalDate objects to calendar objects
     * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
     *
     * @param date The date to convert
     * @return an instance of GregorianCalendar
     */
    public static TimelessDate ConvertToCalendar(LocalDate date) {
	    return new TimelessDate(ConvertToCalendar(LocalDateTime.of(date, LocalTime.MIN)));
    }

    /**
     * Converts calendar objects to java.event.LocalDateTime objects
     * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
     *
     * @param date The date to convert
     * @return an instance of LocalDate
     */
    public static LocalDate ConvertToLocalDate(TimelessDate date) {
        return date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate();
    }

    /**
     * Converts calendar objects to java.event.LocalDateTime objects
     * using TimeZone Europe/Berlin and Locale.Germany. (ignores seconds)
     *
     * @param date The date to convert
     * @return an instance of LocalDateTime
     */
    public static LocalDateTime ConvertToLocalDateTime(GregorianCalendar date) {
        return date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
    }

    @Deprecated
    public final static class Backport {

        private Backport() {}

        public static boolean IsDateOver(TimelessDate date, TimelessDate isOver) {
            if(date.get(Calendar.YEAR) == isOver.get(Calendar.YEAR)) {
                if(date.get(Calendar.MONTH) == isOver.get(Calendar.MONTH)) {
                    if(date.get(Calendar.DAY_OF_MONTH) == isOver.get(Calendar.DAY_OF_MONTH)) {
                        return false;
                    } else {
                        return date.get(Calendar.DAY_OF_MONTH) > isOver.get(Calendar.DAY_OF_MONTH);
                    }
                } else {
                    return date.get(Calendar.MONTH) > isOver.get(Calendar.MONTH);
                }
            } else {
                return date.get(Calendar.YEAR) > isOver.get(Calendar.YEAR);
            }
        }

        public static void NextWeek(TimelessDate g) {
            AddDays(g, 7);
            Normalize(g);
        }

        public static String GetCurrentDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            Calendar c = Calendar.getInstance();
            return sdf.format(c.getTime());
        }

        public static boolean IsSameWeek(GregorianCalendar g1, GregorianCalendar g2) {
            return g1.get(Calendar.WEEK_OF_YEAR) == g2.get(Calendar.WEEK_OF_YEAR)
                    && g1.get(Calendar.YEAR) == g2.get(Calendar.YEAR);
        }

        /**
         * Normalizes the day to a week starting with monday
         *
         * @param g GregorianCalendar
         */
        @Deprecated
        public static void Normalize_Native(TimelessDate g) {
            g.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        /**
         * Sets the week day to last monday before. If it already is monday it does nothing.
         *
         * @param g GregorianCalendar
         */
        public static void Normalize(TimelessDate g) {
            while(!new SimpleDateFormat("EEEE", Locale.GERMANY).format(g.getTime()).equals("Montag")){
                Backport.SubtractDays(g, 1);
            }
        }

        public static void AddDays(TimelessDate g, int i) {
            g.add(Calendar.DAY_OF_MONTH, i);
        }

        public static void SubtractDays(TimelessDate g, int i) {
            g.add(Calendar.DAY_OF_YEAR, -i);
        }

        public static BackportAppointment GetFirstAppointmentOfDay(ArrayList<BackportAppointment> appointments, TimelessDate day) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            for(BackportAppointment a : appointments) {
                // If same day
                if(sdf.format(a.getStartDate().getTime()).equals(sdf.format(day.getTime()))) {
                    // Since appointments are partially sorted -> first element is a match
                    return a;
                }
            }
            return null;
        }

        public static BackportAppointment GetLastAppointmentOfDay(ArrayList<BackportAppointment> appointments, TimelessDate day) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            BackportAppointment result = null;
            for(BackportAppointment a : appointments) {
                // If same day
                if(sdf.format(a.getStartDate().getTime()).equals(sdf.format(day.getTime()))) {
                    // Since appointments are sorted -> first element is a match
                    result = a;
                }
            }
            return result;
        }

        // FIXME Borders are not properly set on a FREE week
        public static Integer[] GetBorders(ArrayList<BackportAppointment> weekAppointments) {
            int startOnMin, endOnMin, max = 0, min = 1440;
            for(BackportAppointment a : weekAppointments) {
                startOnMin = a.getStartDate().get(Calendar.HOUR_OF_DAY) * 60
                        + a.getStartDate().get(Calendar.MINUTE);
                endOnMin = a.getEndDate().get(Calendar.HOUR_OF_DAY) * 60
                        + a.getEndDate().get(Calendar.MINUTE);
                if(startOnMin < min) {
                    min = startOnMin;
                }
                if(endOnMin > max) {
                    max = endOnMin;
                }
            }
            return new Integer[] { min, max };
        }

        public static ArrayList<BackportAppointment> GetWeekAppointments(TimelessDate week, ArrayList<BackportAppointment> superList) {
            ArrayList<BackportAppointment> weekAppointments = new ArrayList<>();
            if (superList != null) {
                for (BackportAppointment a : superList) {
                    if (Backport.IsSameWeek(a.getStartDate(), week)) {
                        weekAppointments.add(a);
                    }
                }
            }
            return weekAppointments;
        }

        public static ArrayList<BackportAppointment> GetAppointmentsOfDay(TimelessDate day, ArrayList<BackportAppointment> list) {
            ArrayList<BackportAppointment> dayAppointments = new ArrayList<>();
            if (list != null) {
                String currDate = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(day.getTime());
                for (BackportAppointment a : list) {
                    if (a.getDate().equals(currDate)) {
                        dayAppointments.add(a);
                    }
                }
            }
            return dayAppointments;
        }

        public static LinkedHashSet<BackportAppointment> GetAppointmentsOfDayAsSet(TimelessDate day, LinkedHashSet<BackportAppointment> list) {
            LinkedHashSet<BackportAppointment> dayAppointments = new LinkedHashSet<>();
            String currDate = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(day.getTime());
            for(BackportAppointment a : list) {
                if(a.getDate().equals(currDate)) {
                    dayAppointments.add(a);
                }
            }
            return dayAppointments;
        }
    }
}
