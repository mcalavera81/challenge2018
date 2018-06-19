package demo.order.service.websocket;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiffOrdersBatch {

    private long sequence;
    private DiffOrder[] orders;


}
