package reactor.cosmosdb.criteria.domain;

import java.util.Arrays;

public enum CriteriaOperator {
    EQUALS("EQUALS","="),
    EQUALS_STRICT("EQUALS_STRICT","="),
    NOT_EQUALS_STRICT("NOT_EQUALS_STRICT","!="),
    CONTAINS("CONTAINS","IN"),
    NOT_CONTAINS("NOT_CONTAINS","NOT IN"),
    NOT_EQUALS("NOT_EQUALS","!="),
    LIKE("LIKE","LIKE"),
    MORE_THAN("MORE_THAN",">"),
    MORE_OR_EQUALS_THAN("MORE_OR_EQUALS_THAN",">="),
    LESS_THAN("LESS_THAN","<"),
    LESS_OR_EQUALS_THAN("LESS_OR_EQUALS_THAN","<="),
    LIKE_STRICT("LIKE_STRICT","LIKE"),
    NOT_NULL("NOT_NULL","!= null"),
    NULL("NULL","= null");
    final String value;
    final String id;

    private CriteriaOperator(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getId(){return this.id;}

    public static CriteriaOperator getCriteriaOperator(String operator){
        return Arrays.stream(CriteriaOperator.values()).filter(op->op.getId().equals(operator)).findFirst().orElseThrow(()->new RuntimeException("Invalid operator"));
    }
}

