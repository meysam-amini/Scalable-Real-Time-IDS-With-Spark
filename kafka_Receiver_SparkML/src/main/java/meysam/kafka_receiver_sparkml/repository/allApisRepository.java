package meysam.kafka_receiver_sparkml.repository;

import meysam.kafka_receiver_sparkml.model.Api;
import meysam.kafka_receiver_sparkml.model.NetFlow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface allApisRepository extends MongoRepository<Api,String> {

}
