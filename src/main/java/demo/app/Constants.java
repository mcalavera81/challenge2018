package demo.app;

import demo.support.helpers.TransformHelpers;

import java.math.BigDecimal;

public class Constants {

    public static final BigDecimal SATOSHI_ERROR = TransformHelpers.bd(0.00000001);
    public static final BigDecimal MXN_ERROR = TransformHelpers.bd(1);

    public enum BitsoBook{
        BTC_MXN("btc_mxn");

        private final String id;

        BitsoBook(String id){
            this.id = id;
        }

        public String id() {
            return id;
        }
    }
}
