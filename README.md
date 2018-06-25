# Overview

There are 4 main packages:
* **demo.app** : Configures, instantiates and puts together all the components
 of the application.
* **demo.trade** : Main logic pertaining to trades.
* **demo.order** : Main logic pertaining to asks and bids.
* **demo.support** : Support for the rest of the application. 

The Frontend runtime contains 2 Threads (OrderViewPopulator, TradeViewPopulator)
with periodically poll a data structure (OrderBook, RecentTradesLog). These data
structures contain the state of the application.

Because of the push nature of the order updates, there are 2 more threads on the
backend to deal with the orders stream: BookSynchronizer and 
DiffOrderStreamConsumer.

## Prerequisites
Java 8 or higher.
JavaFX.

## Build
- `./gradlew clean build` 
    * compiles the sources.
    * runs the tests.
    * runs the static analysis tools: **pmd** and **findbugs**.
    * runs the coverage tool **jacoco**.
    * generates the test, analysis and coverage reports under `build/reports`
     folder.
        

## Run
- `./gradlew run` launches the app.

    **demo.conf** defines runtime configuration properties. 
    Some of them can be overriden via environment variables:
    *   *BEST_TRADES*: How many recent trades to display.
    *   *BEST_ORDERS*: How many bids and asks to display.
    *   *UP_TICKS*: How many upticks before triggrering a sell order.
    *   *DOWN_TICKS*: How many downticks before triggrering a buy order.
    
    For example you can customize all of them with:
    
    `UP_TICKS=4 DOWN_TICKS=3 BEST_ORDERS=20 ./gradlew run`
   
    
| Feature                                   | File name | Method name   |
|   ------------------------------------------|-----------|-------------|
| Schedule the polling of trades over REST.| PollingRecentTradesLogViewPopulator|call|
| Request a book snapshot over REST.       | BookSynchronizer |  loadBookSnapshot |
| Listen for diff-orders over websocket.   |  BitsoWebsocketListener |   onTextFrame|
|Replay diff-orders.                       |BookSynchronizer|replayQueuedOrders|
|Use config option X to request  recent trades.| PollingRecentTradesLogViewPopulator|updateTradeRows|
|Use config option X to limit number of ASKs displayed in UI|PollingOrderBookViewPopulator|updateAskRows|
|The loop that causes the trading algorithm to reevaluate.|LatestTradesContainer|getRecentTrades|


[![](https://codescene.io/projects/2980/status.svg) Get more details at **codescene.io**.](https://codescene.io/projects/2980/jobs/latest-successful/results)