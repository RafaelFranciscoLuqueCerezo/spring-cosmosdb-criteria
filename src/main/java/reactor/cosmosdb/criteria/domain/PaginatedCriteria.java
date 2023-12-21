package reactor.cosmosdb.criteria.domain;

/**
 * Interface used to declare all methods necessary to create a paginated query and his response
 */
public interface PaginatedCriteria {
    String getQuerySentence();
    boolean isCountQuery();
}
