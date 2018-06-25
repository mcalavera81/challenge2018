package demo.support.helpers;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
public class DateTimeHelpers {

    public static final DateTimeFormatter dateTimeFormatterZOffset =
        ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZ");

    public static final DateTimeFormatter dateTimeFormatterXOffset =
        ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");


    public static String orderDateFormat(ZonedDateTime zdt) {
        return formatDate(zdt, dateTimeFormatterZOffset);
    }

    public static String tradeDateFormat(ZonedDateTime zdt) {
        return formatDate(zdt, dateTimeFormatterXOffset);

    }

    private static String formatDate(ZonedDateTime zdt, DateTimeFormatter formatter) {
        return Try.of(() -> zdt.format(formatter))
            .onFailure($ ->
                log.error("Error formatting ({}) date: {}", formatter, zdt)
            ).get();
    }

    public static ZonedDateTime now(){
        return ZonedDateTime.now(ZoneOffset.UTC);}

    public static ZonedDateTime parseDate(String date){
        try {
            return ZonedDateTime.parse(date, dateTimeFormatterXOffset)
                .withZoneSameInstant(ZoneOffset.UTC);
        }catch (Exception e){
            return ZonedDateTime.parse(date, dateTimeFormatterZOffset)
                .withZoneSameInstant(ZoneOffset.UTC);
        }
    }
}
