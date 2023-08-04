package meysam.kafka_producer;

public class NetFlow {

    private String SrcAddr;
    private String DstAddr;
    private String SrcMac;
    private String DstMac;
    private String Sport;
    private String Dport;
    private String State;
    private String Proto;
    private String SrcRate;
    private String DstRate;

    public String getSrcAddr() {
        return SrcAddr;
    }

    public void setSrcAddr(String srcAddr) {
        SrcAddr = srcAddr;
    }

    public String getDstAddr() {
        return DstAddr;
    }

    public void setDstAddr(String dstAddr) {
        DstAddr = dstAddr;
    }

    public String getSrcMac() {
        return SrcMac;
    }

    public void setSrcMac(String srcMac) {
        SrcMac = srcMac;
    }

    public String getDstMac() {
        return DstMac;
    }

    public void setDstMac(String dstMac) {
        DstMac = dstMac;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }

    public String getDport() {
        return Dport;
    }

    public void setDport(String dport) {
        Dport = dport;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getProto() {
        return Proto;
    }

    public void setProto(String proto) {
        Proto = proto;
    }

    public String getSrcRate() {
        return SrcRate;
    }

    public void setSrcRate(String srcRate) {
        SrcRate = srcRate;
    }

    public String getDstRate() {
        return DstRate;
    }

    public void setDstRate(String dstRate) {
        DstRate = dstRate;
    }

    public NetFlow(){

    }

    @Override
    public String toString() {


        return "{" +
                " \"SrcAddr\":" +"\""+SrcAddr+"\","+
                "  \"DstAddr\":" +"\""+DstAddr+"\","+
                "  \"SrcMac\":" +"\""+SrcMac+"\","+
                "  \"DstMac\":" +"\""+DstMac+"\","+
                "  \"Sport\":" +"\""+Sport+"\","+
                "  \"Dport\":" +"\""+Dport+"\","+
                "  \"State\":" +"\""+State+"\","+
                "  \"Proto\":" +"\""+Proto+"\","+
                "  \"SrcRate\":" +"\""+SrcRate+"\","+
                "  \"DstRate\":" +"\""+DstRate+"\""+
                "}";
    }


}

