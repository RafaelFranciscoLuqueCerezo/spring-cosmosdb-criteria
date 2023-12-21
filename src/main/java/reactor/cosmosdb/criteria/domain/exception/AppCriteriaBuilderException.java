package reactor.cosmosdb.criteria.domain.exception;

/**
 * Exception launched inside AppCriteriaBuilder
 * when something is malformed or any other validation that exists to create an appropriated query sentence
 */
public class AppCriteriaBuilderException extends RuntimeException{
    public AppCriteriaBuilderException(String message){
        super(message);
    }
}
