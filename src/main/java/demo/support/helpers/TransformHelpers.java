package demo.support.helpers;

import demo.app.config.AppConfiguration;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

public class TransformHelpers {

    private static ReentrantLock lock = new ReentrantLock();
    private static DecimalFormat minorFormat, majorFormat;
    static {
        val fc = AppConfiguration.getInstance().getFrontendConfig();
        minorFormat = new DecimalFormat(fc.getMinorFormat());
        majorFormat = new DecimalFormat(fc.getMajorFormat());
    }

    public static <T> Validation<String,T> getValidation(Try<T> tryObject){
        return Validation.fromEither(tryObject.toEither()).mapError(Throwable::toString);
    }

    public static <T> Try<T> toTry(final Validation<? extends Throwable,T> v) {
        return v.isValid() ? v.toTry() : Try.failure(v.getError());
    }

    public static BigDecimal bd(double value){
        return BigDecimal.valueOf(value);
    }

    public static String getStackTrace(Throwable throwable){
        StringWriter s= new StringWriter();
        throwable.printStackTrace(new PrintWriter(s));
        return  s.toString();
    }

    public static String formatMinor(BigDecimal value){
        lock.lock();
        try {
            return minorFormat.format(value);
        } finally {
            lock.unlock();
        }
    }


    public static String formatMajor(BigDecimal value){
        lock.lock();
        try {
            return majorFormat.format(value);
        } finally {
            lock.unlock();
        }
    }

}
