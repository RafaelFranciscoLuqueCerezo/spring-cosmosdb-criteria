package reactor.cosmosdb.criteria.domain;

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
