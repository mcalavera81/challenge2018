package demo.shared.parser;

import org.json.JSONObject;

import java.util.function.Supplier;

import static demo.shared.parser.UtilParser.RestResponseField.PAYLOAD;
import static demo.shared.parser.UtilParser.RestResponseField.SUCCESS;

public class TestParserUtils {
    public static JSONObject responseWithPayload(Supplier<Object> payload){
        return new JSONObject().
                put(SUCCESS.id(),true).
                put(PAYLOAD.id(),payload.get());
    }
}
