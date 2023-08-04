package meysam.kafka_receiver_sparkml;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import meysam.kafka_receiver_sparkml.model.Api;
import meysam.kafka_receiver_sparkml.repository.NetFlowRepository_reactive;
import meysam.kafka_receiver_sparkml.repository.allApisRepository;
import meysam.kafka_receiver_sparkml.repository.allApisRepository_reactive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@SpringBootApplication
public class testWeb /*implements CommandLineRunner*/{

    private static MongoTemplate mongoTemplate;

    public static void main(String[] args)  {
        SpringApplication.run(meysam.kafka_receiver_sparkml.testWeb.class, args);

    }


 /*   @Override
    public void run(String... args) throws Exception {
        System.out.println("running CommandLineRunner...");

        MongoClient mongo = MongoClients.create();
        mongoTemplate = new MongoTemplate( mongo,"NetFlow");
        try {
            CollectionOptions options = CollectionOptions.empty()
                    .capped().size(2000000000L)
                    .maxDocuments(2000000000L);
//            template.createCollection("NetFlow",options);
            mongoTemplate.createCollection("AllApis",options);
        } catch (Exception e) {
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("Collection Exists");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");

        }

        mongoTemplate.save(new Api("http://localhost:8080/modem1","modem1"));
        mongoTemplate.save(new Api("http://localhost:8080/modem2","modem2"));
        mongoTemplate.save(new Api("http://localhost:8080/modem3","modem3"));

    }*/

}
