package meysam.kafka_receiver_sparkml.repository;

import meysam.kafka_receiver_sparkml.model.NetFlow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetFlowRepository extends MongoRepository<NetFlow,String> {
}
