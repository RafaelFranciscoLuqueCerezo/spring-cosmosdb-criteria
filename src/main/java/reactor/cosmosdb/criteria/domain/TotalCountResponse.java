package reactor.cosmosdb.criteria.domain;

/**
 * Response class when you want to know the number of coincidences that your query obtained
 */
public class TotalCountResponse {

    private String id;
    private int totalCount;

    public TotalCountResponse(int totalCount){
        this.id = "null";
        this.totalCount = totalCount;
    }
    public TotalCountResponse(){}

    public int getTotalCount(){
        return this.totalCount;
    }
    public String getId(){
        return this.id;
    }
}
