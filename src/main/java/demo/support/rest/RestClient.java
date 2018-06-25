package demo.support.rest;

import demo.app.Constants.BitsoBook;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.json.JSONObject;

import java.util.function.Function;

@Slf4j
public abstract class RestClient<T> {


    protected final BitsoBook book;
    protected final String baseUri;
    private final AsyncHttpClient httpClient;
    private RateLimiter limiter;


    protected RestClient(
        BitsoBook book,
        String baseUri,
        AsyncHttpClient httpClient) {

        this.book = book;
        this.baseUri = baseUri;
        this.httpClient = httpClient;
    }

    protected RestClient(
         BitsoBook book,
         String baseUri,
         AsyncHttpClient rest,
         RateLimiter limiter) {

        this(book, baseUri, rest);
        this.limiter = limiter;

    }

    protected Try<T> get(
        Function<JSONObject, Try<T>> mapper,
        int... limit) {

        String URL = getURL(limit);

        if(limiter!=null) limiter.tryAcquire();
        log.info("Rest call. URL: {}", URL);

        return Try.of(()->URL)
            .mapTry(url->
                httpClient.prepareGet(url)
                    .setHeader("User-Agent", "Android")
                    .execute().get().getResponseBody()
            )
            .map(JSONObject::new)
            .flatMap(mapper);

    }


    protected abstract String getURL(int... limitParam);

}
