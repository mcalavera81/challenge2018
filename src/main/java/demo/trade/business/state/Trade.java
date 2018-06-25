package demo.trade.business.state;

import demo.trade.source.parser.TradeParser;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static java.lang.String.format;

@Getter
@Builder
@ToString
public class Trade {

    public static Try<Trade> build(JSONObject jsonObject) {
            return TradeParser.parse(jsonObject);
    }

    public enum TradeSource{
        REAL, SIMULATED
    }

    public enum TradeType{

        BUY("buy"),SELL("sell");

        private final String name;

        TradeType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Try<TradeType> fromText(String text) {
            return Arrays.stream(values())
                .filter(bl -> bl.name.equalsIgnoreCase(text))
                .findFirst()
                .map(Try::success)
                .orElse(
                    Try.failure(
                        new RuntimeException(format("Not valid trade type: %s", text))));
        }
    }
    @NonNull
    private Long id;
    @NonNull
    private BigDecimal price;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private ZonedDateTime timestamp;
    @NonNull
    private TradeType type;
    @NonNull
    private String book;

    public BigDecimal getValue(){
        return price.multiply(amount);
    }

    @NonNull
    @Builder.Default
    private TradeSource source = TradeSource.REAL;

    public boolean isSimulated(){
        return source == TradeSource.SIMULATED;
    }
}
