package demo;


import demo.shared.config.AppConfiguration;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class InMemoryBackendTest {


    @Test
    public void test_backend_build() {

        val conf = AppConfiguration.tryBuild();
        val backend = InMemoryBackend.build(conf.getBackendConfig());

        Assert.assertNotNull(backend);
        Assert.assertNotNull(backend.getOrderBook());
        Assert.assertNotNull(backend.getRecentTradesLog());

        Assert.assertFalse(backend.isRunning());
        backend.start();
        Assert.assertTrue(backend.isRunning());
        backend.stop();
        Assert.assertFalse(backend.isRunning());

    }

    @Test
    public void test_backend_builder() {
        Assertions.assertThrows(NullPointerException.class,
            ()-> InMemoryBackend.build(null));
    }
}