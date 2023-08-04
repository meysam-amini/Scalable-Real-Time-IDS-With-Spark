package meysam.kafka_receiver_sparkml.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "AllApis")
@Data
public class Api {

    @Id
    private String id;
    private String url;
    private String name;

    public Api(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
