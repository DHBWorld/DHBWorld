package com.main.dhbworld;

import android.os.Build;

import androidx.annotation.RequiresApi;

import dhbw.timetable.rapla.date.DateUtilities;
import dhbw.timetable.rapla.data.event.Appointment;
import dhbw.timetable.rapla.data.event.BackportAppointment;
import dhbw.timetable.rapla.data.time.TimelessDate;
import dhbw.timetable.rapla.exceptions.NoConnectionException;
import dhbw.timetable.rapla.parser.DataImporter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Map;

public class raplaAPI {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        unit_test(LocalDate.of(2022, 4, 13), LocalDate.of(2022, 4, 20));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void unit_test(LocalDate start, LocalDate end) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                Map<LocalDate, ArrayList<Appointment>> data = DataImporter.ImportWeekRange(start, end, "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4");
                Map<TimelessDate, ArrayList<BackportAppointment>> backportData = DataImporter.Backport.ImportWeekRange(
                        DateUtilities.ConvertToCalendar(start), DateUtilities.ConvertToCalendar(end),"https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=eisenbiegler&file=TINF20B4");
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
}
