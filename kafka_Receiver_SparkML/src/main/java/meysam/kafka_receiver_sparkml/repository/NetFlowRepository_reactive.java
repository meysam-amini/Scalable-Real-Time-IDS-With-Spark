package meysam.kafka_receiver_sparkml.repository;

import meysam.kafka_receiver_sparkml.model.FlowGroup;
import meysam.kafka_receiver_sparkml.model.NetFlow;
import org.apache.avro.data.Json;
import org.bson.json.JsonObject;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scala.util.parsing.json.JSON;

@Repository
public interface NetFlowRepository_reactive extends ReactiveCrudRepository<NetFlow,String> {

    @Tailable
    public Flux<NetFlow> findWithTailableCursorBy();

    @Tailable
    public Mono<NetFlow> findTopByTimestamp();


    @Tailable
    @Aggregation({
            // First sort all the docs by timestamp
            "{$sort: {timestamp: -1}}",

            //select by last 4 hours
            "{$match : {timestamp : {" +
                    "$gte : new Date(new Date().getTime() - 4*60*60*1000)" +
                    "} } }",
    })
    Flux<NetFlow> getLast_N_minutsData();


    @Tailable
    @Aggregation({
            // First sort all the docs by timestamp
            "{$sort: {timestamp: -1}}",

            //select by last 4 hours
            "{$match : {timestamp : {" +
                    "$gte : ?0" +
                    "} } }",

            //group by prediction and count them
            "{$group : {_id:$prediction, count:{$sum:1}}}",

            "{ $replaceWith: { prediction: $_id ,count:$count} }"

    })
    Flux<FlowGroup> getLast_N_minutsData_groupByPrediction(Long timeDiff);

}
