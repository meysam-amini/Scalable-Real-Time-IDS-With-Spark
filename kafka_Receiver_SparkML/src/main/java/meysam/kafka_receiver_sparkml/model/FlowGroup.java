package meysam.kafka_receiver_sparkml.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jsons")
@Data
public class FlowGroup {

    private int prediction;
    private int count;
}
