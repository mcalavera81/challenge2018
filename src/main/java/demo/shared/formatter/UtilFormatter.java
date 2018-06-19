package demo.shared.formatter;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class UtilFormatter {

    public static final DateTimeFormatter dateTimeFormatterZOffset = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZ");

    public static final DateTimeFormatter dateTimeFormatterXOffset = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");


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
}
