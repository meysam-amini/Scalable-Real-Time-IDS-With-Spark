package meysam.kafka_receiver_sparkml.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "NetFlow")
@Data
public class NetFlow {
	

    @Id
    private String id;
    private String SrcAddr;
    private String DstAddr;
    private String SrcMac;
    private String DstMac;
    private String Sport;
    private String Dport;
    private String Proto;
    private String state;
    private Double SrcRate;
    private Double DstRate;
    private Double probability;
    private int prediction;
    private Date timestamp;

}
