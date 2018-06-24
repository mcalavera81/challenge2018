package demo.order.source.stream.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DiffOrdersBatch {

    private long sequence;
    private DiffOrder[] orders;


}
