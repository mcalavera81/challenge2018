package demo;

import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class TestUtils {

    private static final Random rand = new Random();
    private static final float MIN_RATE = 150000;
    private static final float MIN_AMOUNT = 0;
    private static final float MAX_RATE = 160000;
    private static final float MAX_AMOUNT = 1;
    private final static ScheduledExecutorService executor =
        newSingleThreadScheduledExecutor();

    public static Try<String> loadString(String filename){

        return Try.of(()->{
            Path path = Paths.get(TestUtils.class.getClassLoader()
                .getResource(filename).toURI());
            val fileBytes = Files.readAllBytes(path);
            return new String(fileBytes);
        });
    }

    public static Try<JSONObject> loadJson(String filename){
        return loadString(filename).map(JSONObject::new);
    }



    private static float floatRange(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }

    public static double randPrice(){
        return floatRange(MIN_RATE, MAX_RATE);
    }

    public static double randAmount(){
        return floatRange(MIN_AMOUNT, MAX_AMOUNT);
    }


    public static Long randSeq(){
        return rand.nextLong();
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> withDefault(CompletableFuture<T> cf,
                                                T defaultValue,
                                                Duration timeout) {

        return (CompletableFuture<T>) CompletableFuture.anyOf(
            cf,
            delayedValue(defaultValue, timeout));
    }

    private static <T> CompletableFuture<T> delayedValue(
        T value,
        final Duration delay) {

        final CompletableFuture<T> result = new CompletableFuture<>();
        executor.schedule(() -> result.complete(value),
            delay.toMillis(), TimeUnit.MILLISECONDS);
        return result;
    }
}
