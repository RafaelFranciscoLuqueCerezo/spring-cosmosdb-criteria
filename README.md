# SPRING COSMOSDB CRITERIA QUERIES

## Description
This library allows you to create criteria queries against CosmosDb , also all logic neccessary to launch this queries against the db are already developed, so it will help you to avoid think about how to create and launch those special queries against the db.
Remember that CosmosDb does not have developed this feature, as other db has, such as MongoDb for example.

---

## Operators
Bellow two images that represent how some operator work:
![alt text](/docs/CriteriaOperatorFirstPart.PNG)

![alt text](/docs/CriteriaOperatorSecondPart.PNG)

In order to check all operator available , you must check [**CriteriaOperator**](src/main/java/reactor/cosmosdb/criteria/domain/CriteriaOperator.java)

---

## How to create a criteria class
Now you can see some example about how to do it correctly. 
Imagine that you have this container on cosmosdb:

```java
@Builder
@Data
public class Testing {
    private String id;
    private String code;
    private String state;
    private Contact contactOwner;
    private List<Contact> contactOwnerList;
}
```

```java
@Data
public class Contact {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
}
```

```java
@Container(containerName = "testing")
@Builder
public class TestingContainer {
    private String id;
    private String code;
    private String state;
    private Contact contactOwner;
    private List<Contact> contactOwnerList;
    
    public Testing toEntity(){
        return Testing.builder()
                .id(getId())
                .code(getCode())
                .state(getState())
                .contactOwner(getContactOwner())
                .contactOwnerList(getContactOwnerList())
                .build();
    }
    
    public static TestingContainer fromEntity(Testing entity){
        return TestingContainer.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .state(entity.getState())
                .contactOwner(entity.getContactOwner())
                .contactOwnerList(entity.getContactOwnerList())
                .build();
    }
    
    }
```
| AtributeType                                  | conditionalToUse                                                        |
|-----------------------------------------------|-------------------------------------------------------------------------|
| <span style="color:#EE502E">**List**</span>   | <span style="color:#EE502E">super.conditionalCreatorAgainstArray</span> |
| <span style="color:#6027EC">**Object**</span> | <span style="color:#6027EC">super.conditionalCreator</span>             |
| <span style="color:#18C84D">**Null**</span>   | <span style="color:#18C84D">super.nullConditionCreator</span>             |

**Be carefull!** If you want to compare against a <span style="color:#EE502E">**List**</span> attribute of Container, you must use <span style="color:#EE502E">super.conditionalCreatorAgainstArray</span> 
```java
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestingCriteria implements PaginatedCriteria {
    @EqualsAndHashCode.Include
    private final String querySentence;
    private final boolean countQuery;

    private TestingCriteria(String querySentence, boolean countQuery) {
        this.querySentence = querySentence;
        this.countQuery = countQuery;
    }

    public static class Builder extends AppCriteriaBuilder<TestingCriteria.Builder> {

        public static Builder builder(){
            return new Builder();
        }
        private Builder(){
            super();
        }

        public Builder id(CriteriaOperator criteriaType, List<String> id){
            this.addWhereCondition(super.conditionalCreator(criteriaType,"id",id));
            return this;
        }
        public Builder code(CriteriaOperator criteriaType, List<String> code){
            this.addWhereCondition(super.conditionalCreator(criteriaType,"code",code));
            return this;
        }
        public Builder codeNull(CriteriaOperator criteriaType){
            this.addWhereCondition(super.nullConditionalCreator(criteriaType,"code"));
            return this;
        }
        public Builder state(CriteriaOperator criteriaType, List<String> state){
            this.addWhereCondition(super.conditionalCreator(criteriaType,"state",state));
            return this;
        }
        public Builder contactOwner(CriteriaOperator criteriaType, List<String> contactOwnerId){
            this.addWhereCondition(super.conditionalCreator(criteriaType,"contactOwner",contactOwnerId));
            return this;
        }
        public Builder contactOwnerListId(CriteriaOperator criteriaType, List<String> contactOwnerListId){
            this.addWhereCondition(super.conditionalCreatorAgainstArray(criteriaType,"contactOwnerList.id",contactOwnerListId));
            return this;
        }

        public Builder contactOwnerListName(CriteriaOperator criteriaType, List<String> contactOwnerListName){
            this.addWhereCondition(super.conditionalCreatorAgainstArray(criteriaType,"contactOwnerList.name",contactOwnerListName));
            return this;
        }
        

        public TestingCriteria build(){
            super.generateSqlSentence();
            return new TestingCriteria(this.getQuerySentence().toString(),this.isCountQuery());
        }
    }

}
```
---

## How to create a criteria query
Once you have created a criteria class, you are able to build a criteria query. Here , some examples that may help you to understand
You can create Testing classes on your project and run this tests, there you can play with criteria and check is behaviour.
It is important to remind you that you have two ways to create criterias:
- using default behaviour, all parenthesis **(** **)** , **and** **or** are managed by appCriteriaBuilder
- using ignoreAutoGeneratedFirstConditional, in this case you are responsible of concatenate all condition with desiderable operator, and also to close a parenthesis if you open it.

### SelectCount
You are able to create a select count sentence, using `selectCount()` method. In case you use it, a `TotalCountResponse` will be returned from service.

### SelectCustom
You are able to create your own response object from a container using `selectCustom("c.xxxx as y, c.xxx as z")`
Be aware that you need to use always `c.` prefix with this library.

### GroupBy
You can create a groupBy expression like this `groupBy("c.xxxxx, c.xxxxxx")`.
Be aware that you need to use always `c.` prefix with this library.

Do not worry if you malformed a query criteria , because it is controlled by AppCriteriaBuilder, so in case you did something wrong, it will throw an exception

```java
import CriteriaOperator;

import java.util.List;

public class PlayGround {
    @Test
    void testSimpleCriteria() {
        TestingCriteria criteria = TestingCriteria.Builder
                .builder()
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .state(CriteriaOperators.EQUALS, List.of("state1"))
                .build();
        System.out.println(criteria);
    }

    @Test
    void testSimpleCriteriaWithOrClause() {
        TestingCriteria criteria = TestingCriteria.Builder
                .builder()
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .or_start()
                .state(CriteriaOperators.EQUALS, List.of("state1"))
                .contactOwner(CriteriaOperator.EQUALS, List.of("contactOwnerId1"))
                .or_end()
                .build();
        System.out.println(criteria);
    }
    
    @Test
    void testingSelectCountCriteria(){
        TestinCriteria criteria = TestingCriteria.Builder
                .builder()
                .selectCount()
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .build();
        System.out.println(criteria);
    }

    @Test
    void testingSelectCustomCriteria(){
        TestinCriteria criteria = TestingCriteria.Builder
                .builder()
                .selectCustom("c.code as code, c.id as id")
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .build();
        System.out.println(criteria);
    }

    @Test
    void testingGroupByCriteria(){
        TestinCriteria criteria = TestingCriteria.Builder
                .builder()
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .groupBy("c.code")
                .build();
        System.out.println(criteria);
    }
    
    @Test
    void testingComplexCriteria() {
        TestinCriteria criteria = TestingCriteria.Builder
                .builder()
                .ignoreAutoGeneratedFirstConditional()
                .code(CriteriaOperators.EQUALS, List.of("code1"))
                .and()
                .state(CriteriaOperators.EQUALS, List.of("state1"))
                .and()
                .openParenthesis()
                .contactOwner(CriteriaOperator.EQUALS, List.of("contactOwnerId1"))
                .or()
                .contactOwner(CriteriaOperator.EQUALS, List.of("contactOwnerId2"))
                .closeParenthesis()
                .build();
        System.out.println(criteria);
    }
}

```

## How to launch criteria queries against cosmosDb

You need to extend from two classes ( [**ReactorOperationsCosmosDbRepository**](src/main/java/reactor/cosmosdb/criteria/application/ReactorOperationsCosmosDbRepository.java) and [**ReactorCosmosDbQueryLauncher**](src/main/java/reactor/cosmosdb/criteria/infraestructure/ReactorCosmosDbQueryLauncher.java) )

Examples:

### Service needed to retrieve paginatedInformation and selectCountCriteria
```java
//E: entity (ej: Testing)
//C: container (ej: TestingContainer)
@Service
public class OperationsCosmosDbRepository<E,C> extends ReactorOperationsCosmosDbRepository<E,C> {
    public OperationsCosmosDbRepository(CosmosDbQueryLauncher<C> queryLauncher, CosmosDbQueryLauncher<TotalCountResponse> queryCountLauncher){
        super(queryLauncher,queryCountLauncher);
    }
}
```

### Service needed to launch queries against cosmosdb
```java
@Service
public class CosmosDbQueryLauncher<T> implements ReactorCosmosDbQueryLauncher<T> {
    @Value("${app.azure.cosmosdb.dbName}")
    public String databaseName;
    @Autowired
    public CosmosAsyncClient clientAsync;

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }
    public CosmosAsyncClient getClientAsync(){
        return this.clientAsync;
    }
}
```
### Repository interface example
```java
public interface TestingRepository {
    Flux<CustomResponse> findCustomResponseByCriteria(TestingCriteria criteria);
    Mono<ProjectionPaginated<Testing>> findByCriteriaPaginated(TestingCriteria criteria, int desiredPage, int pageSize);
    Mono<List<Testing>> findByCriteria(TestingCriteria criteria);
    Mono<TotalCountResponse> findBySelectCountCriteria(TestingCriteria criteria);
}

```
### Repository implementation example

```java
@Service
@RequiredArgsConstructor
public class TestingCosmosDbRepository implements TestingRepository {

    private final TestingReactiveCosmosDbRepository repository;

    private final CosmosDbQueryLauncher<TestingContainer> queryLauncher;
    private final CosmosDbQueryLauncher<CustomResponse> queryLauncherCustomResponse;
    private final OperationsCosmosDbRepository<Testing, TestingContainer> operationsCosmosDbRepository;

    @Override
    public Flux<CustomResponse> findCustomResponseByCriteria(TestingCriteria criteria) {
        return queryLauncherCustomResponse.launch(criteria.getQuerySentence(), "containerName", CustomResponse.class);
    }

    @Override
    public Mono<ProjectionPaginated<Testing>> findByCriteriaPaginated(TestingCriteria criteria, int desiredPage, int pageSize) {
        return operationsCosmosDbRepository
                .findByCriteriaPaginated(
                        criteria,
                        desiredPage,
                        pageSize,
                        new ContainerCosmosDbInfo<>("containerName", TestingContainer.class),
                        Testing::toEntity
                );
    }

    @Override
    public Mono<List<Testing>> findByCriteria(TestingCriteria criteria) {
        CosmosPagedFlux<SubscriptionContainer> dbResponse = queryLauncher.launch(criteria.getQuerySentence(), "containerName", TestingContainer.class);
        return dbResponse.collectList().map(x -> x
                .stream().map(TestingContainer::toEntity).collect(Collectors.toList()));
    }

    @Override
    public Mono<TotalCountResponse> findBySelectCountCriteria(TestingCriteria criteria) {
        return operationsCosmosDbRepository.findBySelectCountCriteria(criteria, "containerName");
    }
}

```
