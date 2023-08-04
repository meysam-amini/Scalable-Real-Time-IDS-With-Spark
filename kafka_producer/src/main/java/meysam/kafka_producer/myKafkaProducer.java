package meysam.kafka_producer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public final class myKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "NetFlow";


    public myKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        try
        {
            this.kafkaTemplate.send(TOPIC, message);
        }catch (Exception e){
            log.error("Unable to send messages to topic :{} , exception is : {}", TOPIC, e);
        }
    }
}