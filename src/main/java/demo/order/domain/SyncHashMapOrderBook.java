package demo.order.domain;

import demo.order.domain.Order.Ask;
import demo.order.domain.Order.Bid;
import demo.order.service.websocket.DiffOrder;
import demo.order.service.websocket.DiffOrdersBatch;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@Slf4j
public class SyncHashMapOrderBook implements OrderBook {

    private final OrdersLive<Bid> bids;
    private final OrdersLive<Ask> asks;
    private long latestSequenceId = Long.MIN_VALUE;

    public SyncHashMapOrderBook(){
        this.bids = new OrdersLive<>(Comparator.comparing(Order::getPrice)
            .reversed());
        this.asks = new OrdersLive<>(Comparator.comparing(Order::getPrice));
    }
    public long getCurrentSequenceId(){
        return latestSequenceId;
    }


    public List<Ask> getAsks() {
        return asks.getSorted();
    }

    public List<Bid> getBids() {
        return bids.getSorted();
    }

    @Override
    public List<Ask> getAsks(int... n) {
        return asks.getSorted(n);
    }

    @Override
    public List<Bid> getBids(int... n) {
        return bids.getSorted(n);
    }

    public void clearAllAndLoad(OrderBookSnapshot snapshot){

        bids.clearAllAndLoad(snapshot.getBids());
        asks.clearAllAndLoad(snapshot.getAsks());
        latestSequenceId = snapshot.getSequence();
        log.info("Updated book. Sequence: {}", snapshot.getSequence());
    }

    public void update(DiffOrdersBatch batch){

        DiffOrder[] orders = batch.getOrders();
        long batchSequenceId = batch.getSequence();

        Stream.of(orders).forEach(order->{
            if(batchSequenceId > latestSequenceId){
                latestSequenceId = batchSequenceId;
                switch (order.getType()){
                    case BUY:
                        bids.update(order);
                        break;
                    case SELL:
                        asks.update(order);
                        break;
                }
            }
        });
    }


    static class OrdersLive<T extends Order>{

        final Comparator<? super Order> comparator;
        private final Map<String,T> ordersById;

        OrdersLive(Comparator<? super Order> comparator){
            this.comparator = comparator;
            this.ordersById = new ConcurrentHashMap<>();
        }


        void clearAllAndLoad(T[] entries){
            ordersById.clear();
            Stream.of(entries).forEach(e -> ordersById.put(e.getId(), e));
        }

        void  update(DiffOrder<T> order){
            if(order.isCancelledOrCompleted()){
                ordersById.remove(order.getId());
            }else{
                ordersById.put(order.getId(), order.getOrder());
            }

        }

        List<T> getSorted(int... n){

            List<T> sorted = getSorted(comparator);

            if(n!=null && n.length ==1){
                sorted = sorted.stream().limit(n[0]).collect(Collectors.toList());
            }

            return sorted;

        }


        List<T> getSorted(Comparator<? super Order> comparator){
            List<T> orders = new ArrayList<>(ordersById.values());
            orders.sort(comparator);
            return orders;
        }

    }

}


