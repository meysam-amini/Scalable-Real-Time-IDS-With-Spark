package meysam.kafka_receiver_sparkml.controller;

import meysam.kafka_receiver_sparkml.model.Api;
import meysam.kafka_receiver_sparkml.model.FlowGroup;
import meysam.kafka_receiver_sparkml.model.NetFlow;
import meysam.kafka_receiver_sparkml.repository.NetFlowRepository_reactive;
import meysam.kafka_receiver_sparkml.repository.allApisRepository;
import meysam.kafka_receiver_sparkml.repository.allApisRepository_reactive;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Controller
@CrossOrigin(origins = { "*" })
public class Rest_Controller {

    private final NetFlowRepository_reactive repository;
    private final allApisRepository_reactive apisRepository_reactive;
    private final allApisRepository apisRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Rest_Controller(NetFlowRepository_reactive repository,
                           allApisRepository_reactive allApisRepository_reactive,
                           allApisRepository apisRepository,
                           ReactiveMongoTemplate reactiveMongoTemplate) {

        this.repository = repository;
        this.apisRepository_reactive = allApisRepository_reactive;
        this.apisRepository = apisRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    @RequestMapping({"","/","/index","/index.html"})
    public String listFlows(Model model){
        return "index";
    }

    @RequestMapping({"/groups"})
    public String listGroups(Model model){
        return "traffic_groups";

    }

    @RequestMapping({"/lastmins"})
    public String last_N_Minutes(Model model){
        return "last_N_minuts";

    }


    @GetMapping(value = "/allApis",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Api> allApis(){
        return apisRepository.findAll();
    }

    @GetMapping(value = "/modem1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<NetFlow> modem1(){
        return repository.findWithTailableCursorBy();
    }

    @GetMapping(value = "/modem2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<NetFlow> modem2() {
        return repository.findWithTailableCursorBy();
    }

    @GetMapping(value = "/modem3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<NetFlow> modem3(){
        return repository.findWithTailableCursorBy() ;
    }

    @GetMapping(value = "/NetFlow_R", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<NetFlow> getAllNetFlows() {
//        return reactiveMongoTemplate.tail(
//                new Query(), NetFlow.class).share();
        return repository.findWithTailableCursorBy();
    }

//    @CrossOrigin(origins = { "http://localhost:8080" })
//    @GetMapping(value = "/NetFlow_last", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<NetFlow> getLast_N_minutsData(){
//        Long timeDiff=System.currentTimeMillis()-(5*60*60*1000);
//
//        repository.getLast_N_minutsData(timeDiff).subscribe(System.err::println);
//        return repository.getLast_N_minutsData(timeDiff);
//
//    }


//    @CrossOrigin(origins = { "http://localhost:8080/" })
//    @GetMapping(value = "/NetFlow_last_group", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<FlowGroup> getLast_N_minutsData_groupByPrediction(){
//        Long timeDiff=System.currentTimeMillis()-(5*60*60*1000);
//
//        repository.getLast_N_minutsData_groupByPrediction(timeDiff).subscribe(System.err::println);
//        return repository.getLast_N_minutsData_groupByPrediction(timeDiff);
//                        //new Date(new Date().getTime() - 5*60*60*1000)
//    }

    @GetMapping(value = "/NetFlow_last_group/{min}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<FlowGroup> getLast_N_minutsData_groupByPrediction(@PathVariable int min){

//        //sort
//        SortOperation sortByIdDesc = Aggregation.sort(Sort.by(Sort.Direction.DESC, "timestamp"));

        //limit
//      LimitOperation limitOperation=Aggregation.limit(50);

        Long timeDiff=System.currentTimeMillis()-(min*60*1000);
        System.out.println("min: "+min+" now: "+new Date(System.currentTimeMillis())
                +" after:"+new Date(timeDiff));
        MatchOperation filterStates = match(new Criteria("timestamp").gt(new Date(timeDiff)));
        // group
        GroupOperation groupOperation = Aggregation.group("_id:$prediction").count().as("count");
        // project
        ProjectionOperation projectToMatchModel = project()
                .andExpression("_id").as("prediction")
                .andExpression("count").as("count");
        //add all the stages
        TypedAggregation<NetFlow> FlowGroupAggregation = Aggregation.newAggregation(
                NetFlow.class,
//                sortByIdDesc,
                filterStates,
                groupOperation,
                projectToMatchModel
        );
        // execute
        return reactiveMongoTemplate.aggregate(
                        FlowGroupAggregation,
                        FlowGroup.class
                );

    }

    @GetMapping(value = "/NetFlow_last/{min}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<NetFlow> getLast_N_minutsData(@PathVariable int min){

        //sort
//        SortOperation sortByIdDesc = Aggregation.sort(Sort.by(Sort.Direction.DESC, "timestamp"));

        //limit
//      LimitOperation limitOperation=Aggregation.limit(50);

        Long timeDiff=System.currentTimeMillis()-( min*60*1000);
        System.out.println(new Date(timeDiff));
        MatchOperation filterStates = match(new Criteria("timestamp").gt(new Date(timeDiff)));

        //add all the stages
        TypedAggregation<NetFlow> FlowGroupAggregation = Aggregation.newAggregation(
                NetFlow.class,
//                sortByIdDesc,
                filterStates
        );
        // execute
        return reactiveMongoTemplate.aggregate(
                FlowGroupAggregation,
                NetFlow.class
        );

    }

    @GetMapping(value = "/NetFlow_last")
    public Flux<NetFlow> getLast_N_minuteData(){
//        Query query = new BasicQuery("{'timestamp':$gte: new Date((new Date().getTime() - (10800000)))}}.sort({'timestamp': -1 })");
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("timestamp").gt(System.currentTimeMillis()-14400000));
        // you can add all your fields here as above
        query.with(Sort.by(Sort.Direction.ASC, "timestamp"));// you can add all your fields here as above

        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
        return reactiveMongoTemplate.find(query.with(Sort.by(Sort.Direction.ASC, "timestamp")), NetFlow.class);
    }



   /* public Flux<NetFlow> subscribe(String postId) {
        Aggregation fluxAggregation = newAggregation(match(where("fullDocument._id").is(new ObjectId(postId))));


        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .filter(fluxAggregation)
                .build();

        return reactiveTemplate.changeStream("post",
                options,
                Post.class)
                .map(ChangeStreamEvent::getBody);
    }*/


}
