package demo.order.source.stream;

import demo.order.source.stream.parser.DiffOrderParser;
import demo.order.source.stream.parser.DiffOrderParser.DiffOrderException;
import demo.order.source.stream.dto.DiffOrder;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;

import static demo.order.source.TestParserOrderUtils.jsonDiffOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class DiffOrderTest {


    @Test
    public void test_build_DiffOrder_empty() {
        JSONObject jsonObject = new JSONObject("{}");
        Try<DiffOrder> build = DiffOrder.build(jsonObject);
        assertThat(build.isSuccess(), is(false));
        assertThat(build.getCause(), is(instanceOf(DiffOrderException.class)));
    }

    @Test
    public void test_build_DiffOrder_filled_ok() {

        Tuple2<JSONObject, DiffOrder> o = jsonDiffOrder();
        Try<DiffOrder> build = DiffOrder.build(o._1);
        assertThat(build.isSuccess(), is(true));
        assertThat(build.get().getAmount(), is(o._2.getAmount()));
        assertThat(build.get().getId(), is(o._2.getId()));
        assertThat(build.get().getRate(), is(o._2.getRate()));
        assertThat(build.get().getType(), is(o._2.getType()));
    }


    @Test
    public void test_build_DiffOrder_missing_type() {

        Tuple2<JSONObject, DiffOrder> o = jsonDiffOrder();
        o._1.remove(DiffOrderParser.DiffOrderField.TYPE.id());

        Try<DiffOrder> build = DiffOrder.build(o._1);
        assertThat(build.isSuccess(), is(false));

    }


    @Test
    public void test_build_DiffOrder_missing_rate() {

        Tuple2<JSONObject, DiffOrder> o = jsonDiffOrder();
        o._1.remove(DiffOrderParser.DiffOrderField.RATE.id());

        Try<DiffOrder> build = DiffOrder.build(o._1);
        assertThat(build.isSuccess(), is(false));

    }

    @Test
    public void test_build_DiffOrder_cancelled() {

        Tuple2<JSONObject, DiffOrder> o = jsonDiffOrder();
        o._1.put(DiffOrderParser.DiffOrderField.AMOUNT.id(),BigDecimal.ZERO);

        Try<DiffOrder> build = DiffOrder.build(o._1);
        assertThat(build.isSuccess(), is(true));
        assertThat(build.get().getAmount(), is(BigDecimal.ZERO));
        assertThat(build.get().getId(), is(o._2.getId()));
        assertThat(build.get().getRate(), is(o._2.getRate()));
        assertThat(build.get().getType(), is(o._2.getType()));
    }


}