package demo.order.service;

class AsyncRestMain {

    /*public static void main(String[] args) throws IOException,
        InterruptedException {
        Request getRequest = new RequestBuilder(HttpConstants.Methods.GET)
                .setUrl("https://api.bitso.com/v3/ticker/")
                .build();

        try (AsyncHttpClient asyncHttpClient = asyncHttpClient()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            asyncHttpClient
                    .prepareGet("https://api.bitso.com/v3/ticker/")
                    .execute()
                    .toCompletableFuture()
                    .handleAsync((response, throwable) -> response, executor).join();


            Thread.sleep(5000);
            executor.shutdown();
            System.out.println("The end");
        }
    }*/
}
