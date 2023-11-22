package com.example.todire;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskUtil {


    public static void performComparison(String dateTimeString, DateTimeListener dateTimeListener) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateTimeString);
            Calendar storedDateTime = Calendar.getInstance();
            storedDateTime.setTime(date);
            Calendar currentDateTime = Calendar.getInstance();

            if (currentDateTime.before(storedDateTime)) {
                dateTimeListener.onDateBeforeCurrent();
            } else {
                dateTimeListener.onDateAfterCurrent();
            }
        } catch (ParseException e) {
            dateTimeListener.onDateComparisonError();
        }
    }


    public interface DateTimeListener {
        void onDateBeforeCurrent();

        void onDateAfterCurrent();

        void onDateComparisonError();
    }


}




