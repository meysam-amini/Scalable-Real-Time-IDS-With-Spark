package meysam.kafka_producer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@SpringBootApplication
public class KafkaProducerApplication {

    private final TCPReceiver receiver;
    private final myKafkaProducer producer;

    @Value("${listen.on.tcp}")
    private boolean listenOnTcp;
    @Value("${receiver.tcp.port}")
    private int receiverTcpPort;
    @Value("${test.sent.data.file.name}")
    private String TEST_DATA_FILE_NAME;



    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
    }

    @Bean
    public CommandLineRunner startTcpSocket() throws IOException {
        return args -> {
            if (listenOnTcp)
                receiver.startTcpReceiver(receiverTcpPort);
            else
                sendJsonFromCsv();
        };
    }

    private void sendJsonFromCsv() throws IOException, InterruptedException {

        File input = new File(System.getProperty("user.dir") + "/csvfiles/" + TEST_DATA_FILE_NAME);

        //SrcAddr,DstAddr,SrcMac,DstMac,Sport,Dport,State,Proto,SrcRate,DstRate
        CsvSchema schema = CsvSchema.builder()
                .setUseHeader(true)
                .addColumn("srcAddr", CsvSchema.ColumnType.STRING)
                .addColumn("dstAddr", CsvSchema.ColumnType.STRING)
                .addColumn("srcMac", CsvSchema.ColumnType.STRING)
                .addColumn("dstMac", CsvSchema.ColumnType.STRING)
                .addColumn("sport", CsvSchema.ColumnType.STRING)
                .addColumn("dport", CsvSchema.ColumnType.STRING)
                .addColumn("state", CsvSchema.ColumnType.STRING)
                .addColumn("proto", CsvSchema.ColumnType.STRING)
                .addColumn("srcRate", CsvSchema.ColumnType.STRING)
                .addColumn("dstRate", CsvSchema.ColumnType.STRING)
                .build();

        CsvMapper mapper = new CsvMapper();
        MappingIterator readAll = mapper.readerFor(NetFlow.class).
                with(schema).readValues(input);

        int r;
        while ((readAll).hasNext()) {
            r = (int) (Math.random() * 10);
//            System.out.println("r:"+r);
//            System.err.println(readAll.next().toString());
            producer.sendMessage(readAll.next().toString());
            Thread.sleep((r));
        }
    }

}
