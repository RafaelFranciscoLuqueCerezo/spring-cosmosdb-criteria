package reactor.cosmosdb.criteria.domain;

public interface PaginatedCriteria {
    String getQuerySentence();
    boolean isCountQuery();
}
