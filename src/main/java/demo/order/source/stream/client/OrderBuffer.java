package demo.order.source.stream.client;

import demo.order.source.stream.dto.DiffOrdersBatch;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static demo.order.source.stream.parser.DiffOrdersBatchParser.isDiffOrderBatch;
import static demo.order.source.stream.parser.DiffOrdersBatchParser.parseBatch;
import static demo.shared.parser.UtilParser.getStackTrace;

@Slf4j
public class OrderBuffer {

    public OrderBuffer(int bufferSize){
        this.queue = new ArrayBlockingQueue<>(bufferSize);
    }

    private final BlockingQueue<String> queue;

    public Try<List<DiffOrdersBatch>> drain(){

        val batches = new ArrayList<String>();
        queue.drainTo(batches);
        List<Try<DiffOrdersBatch>> collect = batches
            .stream()
            .map(this::unmarshallOrders)
            .filter(Option::isDefined)
            .map(Option::get)
            .collect(Collectors.toList());

        Try<List<DiffOrdersBatch>> sequence =
            Try.sequence(collect)
                .map(Seq::asJava);

        return sequence;
    }

    public boolean addMessage(String message){

        if(queue.remainingCapacity() < 0.1*queue.size()){
            queue.clear();
        }
        return queue.offer(message);
    }


    public Option<Try<DiffOrdersBatch>> consume(){

        try {
            return unmarshallOrders( queue.take());
        } catch (InterruptedException e) {
            log.error("Error consuming message: {}", getStackTrace(e));
            return Option.some(Try.failure(e));
        }

    }


    private Option<Try<DiffOrdersBatch>> unmarshallOrders(String message){

        try{
            JSONObject json = new JSONObject(message);
            if(!isDiffOrderBatch(json))
                return Option.none();

            return Option.some(parseBatch(json));

        }catch(Exception e){
            return Option.some(Try.failure(e));
        }
    }

}
