package demo.shared.parser;

import demo.order.helpers.WithId;
import demo.shared.formatter.UtilFormatter;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException;

@Slf4j
public class UtilParser {

    public static Try<BigDecimal> getBigDecimal(JSONObject json, WithId key){
        return getBigDecimal(json, key.id());
    }

    static Try<BigDecimal> getBigDecimal(JSONObject json, String key){
        return extractData(json, ()->new BigDecimal(json.getString(key)));
    }

    public static Try<Integer> getInt(JSONObject json, WithId key){
        return getInt(json, key.id());
    }

    private static Try<Integer> getInt(JSONObject json, String key){
        return extractData(json, ()->json.getInt(key));
    }


    public static Try<Long> getLong(JSONObject json, WithId key){
        return getLong(json, key.id());
    }

    static Try<Long> getLong(JSONObject json, String key){
        return extractData(json, ()->json.getLong(key));
    }

    public static Try<ZonedDateTime> getDateFromMillis(JSONObject json, WithId key){
        return getDateFromMillis(json, key.id());
    }

    private static Try<ZonedDateTime> getDateFromMillis(JSONObject json, String key){
        return extractData(json, ()-> ZonedDateTime
            .ofInstant(Instant.ofEpochMilli(json.getLong(key)),ZoneOffset.UTC));
    }

    public static Try<ZonedDateTime> getDateFromString(JSONObject json, WithId key) {
        return getDateFromString(json, key.id());
    }

    private static Try<ZonedDateTime> getDateFromString(JSONObject json, String key) {
        return extractData(json, ()-> parseDate(json.getString(key)));
    }

    public static Try<String> getString(JSONObject json, WithId key){
        return getString(json, key.id());
    }

    static Try<String> getString(JSONObject json, String key){
        return extractData(json, ()->json.getString(key));
    }

    public static Try<JSONArray> getJsonArray(JSONObject json, WithId key){
        return getJsonArray(json, key.id());
    }

    private static Try<JSONArray> getJsonArray(JSONObject json, String key){
        return extractData(json, ()->json.getJSONArray(key));
    }

    private static <T> Try<T>  extractData(JSONObject json, Supplier<T> supplier){
        try{
            return Try.success(supplier.get());
        }catch(Exception e){
            log.warn(String.format("Exception %s parsing json: %s",json,
                    getStackTrace(e)));
            return Try.failure(e);
        }
    }

    public static ZonedDateTime parseDate(String date){
        try {
            return ZonedDateTime.parse(date, UtilFormatter.dateTimeFormatterXOffset)
                .withZoneSameInstant(ZoneOffset.UTC);
        }catch (Exception e){
            return ZonedDateTime.parse(date, UtilFormatter.dateTimeFormatterZOffset)
                .withZoneSameInstant(ZoneOffset.UTC);
        }
    }


    public static <T> Validation<String,T> getValidation(Try<T> tryObject){
        return Validation.fromEither(tryObject.toEither()).mapError(Throwable::toString);
    }

    public static <T> Try<T> toTry(final Validation<? extends Throwable,T> v) {
        return v.isValid() ? v.toTry() : Try.failure(v.getError());
    }

    public static ZonedDateTime now(){
        return ZonedDateTime.now(ZoneOffset.UTC);}

    public static BigDecimal bd(double value){
        return BigDecimal.valueOf(value);
    }

    public enum BitsoBook{
        BTC_MXN("btc_mxn");

        private final String id;

        BitsoBook(String id){
            this.id = id;
        }

        public String id() {
            return id;
        }
    }


    public static String getStackTrace(Throwable throwable){
        StringWriter s= new StringWriter();
        throwable.printStackTrace(new PrintWriter(s));
        return  s.toString();
    }

    public enum RestResponseField implements WithId{
        SUCCESS("success"),
        PAYLOAD("payload"),
        ERROR("error");

        @Override
        public String id() {
            return id;
        }

        final String id;
        RestResponseField(String id) { this.id = id; }

    }

    public static <T,W> Try<T> parseRestJson(
            JSONObject o,Function<W,Try<T>> bodyParser){

        try{

            boolean success = o.getBoolean(RestResponseField.SUCCESS.id());
            if(success && o.has(RestResponseField.PAYLOAD.id())){
                return bodyParser.apply((W)o.get(RestResponseField.PAYLOAD.id
                    ()));
            }else {
                return Try.failure(
                        ResponseServerException.build(o.getJSONObject(RestResponseField.ERROR.id())));
            }

        }catch (Exception e){
            log.warn(String.format("Exception unmarshalling: " +
                    "%s", UtilParser.getStackTrace(e)));
            return Try.failure(new ParserException(e));
        }
    }

    public static class ParserException extends Exception{

        public ParserException(Throwable cause) {
            super(cause);
        }

    }
}
