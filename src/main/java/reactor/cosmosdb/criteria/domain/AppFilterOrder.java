package reactor.cosmosdb.criteria.domain;

/**
 * Enum used to order the results dynamically retrieved from database.
 */
public enum AppFilterOrder {
    ASC("ASC"),
    DESC("DESC");

    private final String value;

    AppFilterOrder(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
