package demo.support.parser;

import demo.support.helpers.WithId;
import demo.support.helpers.TransformHelpers;
import demo.support.helpers.DateTimeHelpers;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException;

@Slf4j
public class JsonParser {

    public static Try<BigDecimal> getBigDecimal(JSONObject json, WithId key){
        return extractData(json, ()->new BigDecimal(json.getString(key.id())));
    }


    public static Try<Integer> getInt(JSONObject json, WithId key){
        return extractData(json, ()->json.getInt(key.id()));
    }

    public static Try<Long> getLong(JSONObject json, WithId key){
        return  extractData(json, ()->json.getLong(key.id()));
    }


    public static Try<ZonedDateTime> getDateFromMillis(JSONObject json, WithId key){
        return extractData(json, ()-> ZonedDateTime
            .ofInstant(Instant.ofEpochMilli(json.getLong(key.id())),ZoneOffset
                .UTC));
    }

    public static Try<ZonedDateTime> getDateFromString(JSONObject json, WithId key) {
        return extractData(json, ()-> DateTimeHelpers.parseDate(json.getString(key.id())));
    }

    public static Try<String> getString(JSONObject json, WithId key){
        return extractData(json, ()->json.getString(key.id()));
    }

    public static Try<JSONArray> getJsonArray(JSONObject json, WithId key){
        return extractData(json, ()->json.getJSONArray(key.id()));
    }

    private static <T> Try<T>  extractData(JSONObject json, Supplier<T> supplier){
        try{
            return Try.success(supplier.get());
        }catch(Exception e){
            log.warn(String.format("Exception %s parsing json: %s",json,
                    TransformHelpers.getStackTrace(e)));
            return Try.failure(e);
        }
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
                    "%s", TransformHelpers.getStackTrace(e)));
            return Try.failure(new ParserException(e));
        }
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

    public static class ParserException extends Exception{

        public ParserException(Throwable cause) {
            super(cause);
        }

    }
}
