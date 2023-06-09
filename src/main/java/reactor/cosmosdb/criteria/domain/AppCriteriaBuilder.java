package reactor.cosmosdb.criteria.domain;

import reactor.cosmosdb.criteria.domain.exception.AppCriteriaBuilderException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for generating the sql statement that will be executed in cosmosdb
 * Created by Rafael Francisco Luque Cerezo on 31/01/2023
 * @param <T>: Container criteria builder
 */
public abstract class AppCriteriaBuilder<T> {
    private final String SELECT_SENTENCE = "SELECT VALUE c FROM c ";

    private final StringBuilder querySentence;
    private final StringBuilder queryWheres;
    private String orderBySentence;

    private boolean isOrActive;
    private boolean isParenthesisOpen;

    private boolean isOrJustStarted;

    private boolean isEmpty;

    private boolean ignoreAutoGeneratedFirstConditional;


    /**
     *
     * @return the query sentence that can be launched against cosmosdb
     */
    protected StringBuilder getQuerySentence(){return this.querySentence;}

    /**
     * @param condition: a piece of where condition that is going to be added to the query
     */
    protected void addWhereCondition(String condition){
        this.queryWheres.append(condition);
    }

    /**
     * constructor of the class that initializes all necessaries attributes
     */
    protected AppCriteriaBuilder(){
        this.ignoreAutoGeneratedFirstConditional = false;
        this.querySentence = new StringBuilder();
        this.queryWheres = new StringBuilder();
        this.isEmpty = true;
        this.querySentence.append(SELECT_SENTENCE);
        this.orderBySentence = "";
    }

    /**
     * BE CAREFUL! when you activate this flag, you are responsible to concat all operators that you need
     * operators such as OR AND would not being auto generated
     * @return container criteria builder with ignore auto generated first conditional flag DISABLED
     */
    public T ignoreAutoGeneratedFirstConditional(){
        this.ignoreAutoGeneratedFirstConditional = true;
        this.queryWheres.append("AND ");
        return (T) this;
    }

    /**
     * @return container criteria builder with ignore auto generated first conditional flag ENABLED
     */
    public T enableAutoGeneratedFirstConditional(){
        this.ignoreAutoGeneratedFirstConditional = false;
        return (T) this;
    }

    /**
     * @return container criteria builder with OR clause
     */
    public T or(){
        if(!this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag disabled");
        }
        this.queryWheres.append("OR ");
        return (T) this;
    }

    /**
     * @return container criteria builder with AND clause
     */
    public T and(){
        if(!this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag disabled");
        }
        this.queryWheres.append("AND ");
        return (T) this;
    }

    /**
     * @return container criteria builder with openParenthesis
     */
    public T openParenthesis(){
        if(!this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag disabled");
        }
        if(this.isParenthesisOpen) {
            throw new AppCriteriaBuilderException("malformed sentence, closeParenthesis forgotten before adding another openParenthesis");
        }
        this.isParenthesisOpen = true;
        this.queryWheres.append("( ");
        return (T) this;
    }

    /**
     * @return container criteria builder with closeParenthesis
     */
    public T closeParenthesis(){
        if(!this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag disabled");
        }
        if(!this.isParenthesisOpen) {
            throw new AppCriteriaBuilderException("malformed sentence, openParenthesis forgotten before closeParenthesis");
        }
        this.isParenthesisOpen = false;
        this.queryWheres.append(") ");
        return (T) this;
    }

    /**
     * @return container criteria builder with initialization of the Or clause
     */
    public T or_start(){
        if(this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag enabled");
        }
        this.isOrActive = true;
        this.isOrJustStarted = true;
        this.queryWheres.append("AND ( ");
        return (T) this;
    }

    /**
     * @return container criteria builder with end of the Or clause
     */
    public T or_end(){
        if(this.ignoreAutoGeneratedFirstConditional){
            throw new AppCriteriaBuilderException("Invalid operation with ignoreAutoGeneratedFirstConditional flag enabled");
        }
        if(!this.isOrActive) {
            throw new AppCriteriaBuilderException("malformed sentence, or_start forgotten before or_end");
        }
        this.isOrActive = false;
        this.queryWheres.append(") ");
        return (T) this;
    }

    /**
     * @return true if the container criteria builder does not have any where condition
     */
    public boolean isEmpty(){
        return this.isEmpty;
    }

    /**
     * @return OR AND operator due to or start or end methods
     */
    private String getFirstConditional(){
        String firstConditional = "";
        if(ignoreAutoGeneratedFirstConditional){
            return firstConditional;
        }
        if (!isOrJustStarted) {
            firstConditional = isOrActive ? "OR" : "AND";
        }
        return firstConditional;
    }

    /**
     * @param criteriaType: criteria operator to compare with null operators
     * @return true if criteria operator is from null type
     */
    private boolean isOperatorNullType(CriteriaOperator criteriaType){
        return CriteriaOperator.NOT_NULL.getId().equals(criteriaType.getId()) || CriteriaOperator.NULL.getId().equals(criteriaType.getId());
    }

    /**
     * @param criteriaType: criteria operator to compare with like operators
     * @return true if criteria operator is from like type
     */
    private boolean isOperatorLikeType(CriteriaOperator criteriaType){
        return CriteriaOperator.LIKE.getId().equals(criteriaType.getId()) || CriteriaOperator.LIKE_STRICT.getId().equals(criteriaType.getId());
    }

    /**
     * @param criteriaType: criteria operator to compare
     * @param value: value of comparison to check if only compare against one unique value
     * @return true if you are comparison against one unique value and operator that is from detailed below on method
     */
    private boolean isOperatorEqualsTypeAndValueLengthEqualsOne(CriteriaOperator criteriaType,List<String> value) {
        return value.size() <= 1 && (
                CriteriaOperator.EQUALS.getId().equals(criteriaType.getId())
                        || CriteriaOperator.NOT_EQUALS.getId().equals(criteriaType.getId())
                        || CriteriaOperator.MORE_THAN.getId().equals(criteriaType.getId())
                        || CriteriaOperator.MORE_OR_EQUALS_THAN.getId().equals(criteriaType.getId())
                        || CriteriaOperator.LESS_THAN.getId().equals(criteriaType.getId())
                        || CriteriaOperator.LESS_OR_EQUALS_THAN.getId().equals(criteriaType.getId())
        );
    }

    /**
     * Function that sets orderBySentence attribute
     * @param targetColumn : attribute from cosmosdb container
     * @param appFilterOrder: object that represents the order by sentence of the query
     * @return container criteria builder with order sentence set
     */
    public T setOrderBySentence(String targetColumn,AppFilterOrder appFilterOrder){
        String targetColumnParsed = "c."+targetColumn;
        this.orderBySentence = String.format("ORDER BY %s %s",targetColumnParsed,appFilterOrder.getValue());
        return (T) this;
    }

    /**
     * @param targetColumn : attribute from cosmosdb container
     * @return a sub query sentence
     * @see <a href= "https://learn.microsoft.com/en-us/azure/cosmos-db/nosql/query/subquery">cosmosdb subqueries</a>
     */
    private String generateSubQueryExists(String targetColumn){
        String [] targetColumnSplit = targetColumn.split("\\.");
        return String.format("EXISTS(SELECT VALUE %s FROM %s IN c.%s WHERE ",targetColumnSplit[0],targetColumnSplit[0],targetColumnSplit[0]);
    }


    /**
     * @param criteriaType : criteria operator to compare
     * @param targetColumn : attribute from cosmosdb container
     * @param value : value to compare with
     * @return conditional sentence against an array attribute from container
     */
    protected String conditionalCreatorAgainstArray(CriteriaOperator criteriaType, String targetColumn, List<String> value){
        return this.conditionalCreatorGenerator(criteriaType,targetColumn,value,true);
    }


    /**
     * @param criteriaType : criteria operator to compare
     * @param targetColumn : attribute from cosmosdb container
     * @param value : value to compare with
     * @return conditional sentence against an attribute from container
     */
    protected String conditionalCreator(CriteriaOperator criteriaType, String targetColumn, List<String> value) {
        return this.conditionalCreatorGenerator(criteriaType,targetColumn,value,false);
    }

    /**
     * @param criteriaType : criteria operator to compare
     * @param targetColumn : attribute from cosmosdb container
     * @return conditional sentence against an attribute from container
     */
    protected String nullConditionCreator(CriteriaOperator criteriaType,String targetColumn){
        return this.conditionalCreatorGenerator(criteriaType,targetColumn,List.of("nullValue"),false);
    }


    private String getTargetColumn(boolean againstArray, String targetColumn){
        return againstArray ? targetColumn : "c."+targetColumn;
    }

    /**
     * @param criteriaType : criteria operator to compare
     * @param targetColumn : attribute from cosmosdb container
     * @param value : value to compare with
     * @param againstArray : boolean that indicates if you are comparing against an array attribute or a simple attribute from cosmosdb container
     * @return conditional sentence against any attribute from container
     */
    private String conditionalCreatorGenerator(CriteriaOperator criteriaType, String targetColumn, List<String> value, boolean againstArray){
        if(value.isEmpty()) {
            return "";
        }

        this.isEmpty = false;

        StringBuilder firstPart = new StringBuilder(getFirstConditional());
        String closeSentence = "";
        if(againstArray){
            firstPart.append(" ").append(generateSubQueryExists(targetColumn));
            closeSentence = ")";
        }


        isOrJustStarted = false;

        if(isOperatorNullType(criteriaType)){
            return String.format("%s %s %s %s ",
                    firstPart,
                    getTargetColumn(againstArray,targetColumn),
                    criteriaType.getValue(),
                    closeSentence
            );
        }

        if(isOperatorLikeType(criteriaType)){
            List<String> valueLikeParsed = CriteriaOperator.LIKE_STRICT.getId().equals(criteriaType.getId())
                    ? value
                    : value.stream()
                    .map(x->Arrays.stream(x.split(" ")).reduce("",(acc, element)->acc + "%" + element))
                    .collect(Collectors.toList());
            StringBuilder query = new StringBuilder();
            query.append(String.format("%s ( ",firstPart));
            for(int i =0 ;i<valueLikeParsed.size();i++){
                query.append(String.format("UPPER(%s) %s UPPER('%%%s%%') ",getTargetColumn(againstArray,targetColumn),criteriaType.getValue(),valueLikeParsed.get(i)));
                query.append(i<valueLikeParsed.size() -1 ? "OR " : ") ");
            }
            return query.append(closeSentence).append(" ").toString();
        }

        if (isOperatorEqualsTypeAndValueLengthEqualsOne(criteriaType,value)) {
            return String.format("%s %s %s '%s' %s ",
                    firstPart,
                    getTargetColumn(againstArray,targetColumn),
                    criteriaType.getValue(),
                    value.get(0),
                    closeSentence
            );
        }

        if (CriteriaOperator.EQUALS_STRICT.getId().equals(criteriaType.getId())) {
            return String.format("%s ARRAY_LENGTH(%s) = %d AND %s IN %s %s ",
                    firstPart,
                    getParentTargetColumnForLengthChecking(targetColumn),
                    value.size(),
                    getTargetColumn(againstArray,targetColumn),
                    this.mapListToSqlArray(value),
                    closeSentence
            );
        }
        if (CriteriaOperator.EQUALS.getId().equals(criteriaType.getId())) {
            return String.format("%s %s IN %s %s ",
                    firstPart,
                    getTargetColumn(againstArray,targetColumn),
                    this.mapListToSqlArray(value),
                    closeSentence
            );
        }

        if (CriteriaOperator.NOT_EQUALS_STRICT.getId().equals(criteriaType.getId())) {
            return String.format("%s ( ARRAY_LENGTH(%s) != %d OR ( ARRAY_LENGTH(%s) = %d AND %s NOT IN %s ) ) %s ",
                    firstPart,
                    getParentTargetColumnForLengthChecking(targetColumn),
                    value.size(),
                    getParentTargetColumnForLengthChecking(targetColumn),
                    value.size(),
                    getTargetColumn(againstArray,targetColumn),
                    this.mapListToSqlArray(value),
                    closeSentence
            );
        }
        if (CriteriaOperator.NOT_EQUALS.getId().equals(criteriaType.getId())) {
            return String.format("%s %s NOT IN %s ) ) %s ",
                    firstPart,
                    getTargetColumn(againstArray,targetColumn),
                    this.mapListToSqlArray(value),
                    closeSentence
            );
        }

        return String.format("%s %s %s %s %s ", firstPart,getTargetColumn(againstArray,targetColumn), criteriaType.getValue(), this.mapListToSqlArray(value),closeSentence);
    }

    /**
     * This function generate de sql sentence needed to launch a query against cosmosdb
     */
    protected void generateSqlSentence(){
        if(this.isOrActive) {
            throw new AppCriteriaBuilderException("malformed query sentence, or_end forgotten");
        }
        this.querySentence.append("WHERE 1=1 ");
        this.querySentence.append(this.queryWheres);
        this.querySentence.append(this.orderBySentence);
    }

    /**
     * @param array : array of values that you want to compare with
     * @return an array from correctly cosmosdb sql interpretation
     */
    private String mapListToSqlArray(List<String> array){
        StringBuilder stb = new StringBuilder();
        array.forEach(x->stb.append(String.format("'%s',",x)));
        stb.deleteCharAt(stb.length() -1);
        return String.format("(%s)",stb);
    }

    /**
     * @param targetColumn : attribute from cosmosdb container
     * @return properly sentence to put inside ARRAY_LENGTH() , wicht is a cosmos db sql function
     */
    private String getParentTargetColumnForLengthChecking(String targetColumn){
        String[] split = targetColumn.split("\\.");
        return "c."+split[0];
    }
}