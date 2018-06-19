package demo.order.service;

import demo.order.domain.OrderBookSnapshot;
import io.vavr.control.Try;

interface OrderBookSource {

    Try<OrderBookSnapshot> getOrderBook();


}
