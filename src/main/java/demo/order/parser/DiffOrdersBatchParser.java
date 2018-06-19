package demo.order.parser;


import demo.order.service.websocket.DiffOrder;
import demo.order.service.websocket.DiffOrdersBatch;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static demo.order.parser.DiffOrdersBatchParser.DiffOrdersBatchField.*;
import static demo.order.parser.UtilParser.getLong;
import static demo.order.parser.UtilParser.getValidation;


@Slf4j
public class DiffOrdersBatchParser {

    public static final String DIFF_ORDERS = "diff-orders";


    enum DiffOrdersBatchField implements WithId{
        CHANNEL("type"),
        BOOK("book"),
        PAYLOAD("payload"),
        UPDATE_SEQUENCE("sequence");

        @Override
        public String id() {
            return id;
        }

        private final String id;
        DiffOrdersBatchField(String id) { this.id = id; }
    }


    public static Try<DiffOrdersBatch> parseBatch(JSONObject o){

        try{

            String messageType = o.getString(CHANNEL.id());
            if(DIFF_ORDERS.equals(messageType)){
                JSONArray payload = o.getJSONArray(PAYLOAD.id());
                Validation<DiffOrderParserException, DiffOrdersBatch> diffOrdersUpdates =
                        getValidation(getLong(o, UPDATE_SEQUENCE.id()))
                        .combine(getDiffOrderEntries(payload))
                        .ap(DiffOrdersBatch::new)
                        .mapError(error -> DiffOrderParserException.build(error.asJava()));

                return UtilParser.toTry(diffOrdersUpdates);

            }else{
                log.error(String.format("Wrong type %s", messageType));
                return Try.failure(new DiffOrderParserException(String.format("Wrong type %s", messageType)));
            }


        }catch (Exception e){
            log.warn(String.format("Exception unmarshalling the order book %s",
                    UtilParser.getStackTrace(e)));
            return Try.failure(new OrderBookSnaphsotParser.OrderBookParserException(e));
        }
    }

    private static Validation<String,DiffOrder[]> getDiffOrderEntries(JSONArray payload){
        DiffOrder[] diffOrders;
        int total = payload.length();
        diffOrders = new DiffOrder[total];
        for(int i=0;i<total;i++){
            Try<DiffOrder> order = DiffOrder.build(payload.getJSONObject(i));
            if(order.isFailure()){
                return Validation.invalid(order.getCause().toString());
            }else{
                diffOrders[i] = order.get();
            }
        }

        return Validation.valid(diffOrders);
    }

    public static boolean isDiffOrderBatch(JSONObject json) {
        return UtilParser.getString(json, CHANNEL).map(DIFF_ORDERS::equals).getOrElse(false)
                && json.has(PAYLOAD.id());
    }

    public static class DiffOrderParserException extends Exception{
        DiffOrderParserException(String message){
            super(message);
        }

        static DiffOrderParserException build(List<String> messages){
            return new DiffOrderParserException(String.join(",", messages));
        }

    }
}
