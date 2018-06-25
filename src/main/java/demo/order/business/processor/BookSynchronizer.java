package demo.order.business.processor;

import demo.order.business.state.OrderBook;
import demo.order.source.poller.client.OrderBookSource;
import demo.order.source.stream.client.OrderBuffer;
import demo.order.source.stream.dto.DiffOrdersBatch;
import demo.support.thread.ThreadRunner;
import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static demo.support.helpers.TransformHelpers.getStackTrace;

@Slf4j
public class BookSynchronizer implements Runnable, ThreadRunner {


    private final OrderBuffer buffer;
    private final OrderBook book;
    private final OrderBookSource source;

    private volatile Thread synchThread;

    public BookSynchronizer(
        @NonNull OrderBuffer buffer,
        @NonNull OrderBook book,
        @NonNull OrderBookSource source) {

        this.buffer = Objects.requireNonNull(buffer);
        this.book = Objects.requireNonNull(book);
        this.source = Objects.requireNonNull(source);

    }

    @Override
    public void start() {
        log.info("Starting Book Synchronizer...");
        synchThread = new Thread(this);
        synchThread.setDaemon(true);
        synchThread.start();
    }

    @Override
    public void stop() {
        synchThread = null;
        log.info("Stopping Book Synchronizer...");
    }

    @Override
    public void run() {
        log.info("Start processing stream of orders!");
        Thread thisThread = Thread.currentThread();

        while (synchThread == thisThread) {
            processOrderStream();
        }

    }

    void processOrderStream() {
        buffer.consume().map(
            batchTry -> batchTry.andThenTry(batch -> {
                if (isStreamOutOfSync(book, batch)) {
                    resynchronizeStream(batch);

                } else {
                    consumeOrderBatch(batch);
                }
            }).onFailure(
                throwable ->
                    log.info("Error consuming orders {}",
                        getStackTrace(throwable)))
        );
    }

    private boolean resynchronizeStream(DiffOrdersBatch batch) {
        log.info("Order Stream out of synch (SeqId: {})", batch
            .getSequence());

        loadBookSnapshot();
        return replayQueuedOrders();
    }

    private void consumeOrderBatch(DiffOrdersBatch batch) {
        log.debug("Processing Orders (SeqId: {})", batch.getSequence());
        book.update(batch);
    }

    private void loadBookSnapshot() {
        source
            .getOrderBook()
            .andThen(book::clearAllAndLoad);
    }

    private boolean replayQueuedOrders() {


        return buffer.drain().map(batches ->
            Stream.ofAll(batches).foldLeft(
                Boolean.TRUE,
                (success, batch) -> success && replayBatch(batch)
            )).getOrElse(false);
    }

    private Boolean replayBatch(DiffOrdersBatch batch) {
        if (isBatchOld(batch)) return true;
        if (isStreamOutOfSync(book, batch)) return false;

        book.update(batch);
        return true;
    }

    private boolean isBatchOld(DiffOrdersBatch batch) {
        if (batch.getSequence() <= book.getCurrentSequenceId()) {
            log.info("--------------------");
            log.info("Old batch?? {}", batch);
            log.info("--------------------");

        }

        return batch.getSequence() <= book.getCurrentSequenceId();
    }

    private boolean isStreamOutOfSync(
        OrderBook book,
        DiffOrdersBatch batch) {

        return book.getCurrentSequenceId() + 1 != batch.getSequence();
    }


}
