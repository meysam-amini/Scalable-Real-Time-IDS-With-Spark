package meysam.kafka_receiver_sparkml.repository;

import meysam.kafka_receiver_sparkml.model.Api;
import meysam.kafka_receiver_sparkml.model.FlowGroup;
import meysam.kafka_receiver_sparkml.model.NetFlow;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface allApisRepository_reactive extends ReactiveCrudRepository<Api,String> {

    @Tailable
    public Flux<Api> findAllBy();
}
