# Google Cloud Datastore Mapper
Little library helping map/demap datastore's entities to java beans.

For example CrawlerStatus bean:
```
import sk.seky.google.cloud.datastore.Id;

public class CrawlerStatus {
	@Id
	private String id;
	private List<Epizoda> registered;
	private LocalDateTime created;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		  this.id = id;
	}

	public List<Epizoda> getRegistered() {
		return registered;
	}

	public void setRegistered(List<Epizoda> registered) {
		this.registered = registered;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
}

```
Store bean:
```
public void saveStatus(CrawlerStatus status) throws Exception {
    CrawlerStatus status = new CrawlerStatus();
    status.setId("lastStatus");
    status.setCreated(LocalDateTime.now());
    datastoreMapper.save(status);
}
```
Retrieve bean:
```
public CrawlerStatus getLastStatus() {
    CrawlerStatus status = datastoreMapper.get("lastStatus", CrawlerStatus.class);
    return status;
}
```

Find or get collection :

``` 	
Query<Entity> query = Query.newEntityQueryBuilder()
    .setKind(Subscriber.class.getName())
    .setFilter(StructuredQuery.PropertyFilter.eq("topics", topic))
    .build();
Iterator<Entity> result = datastoreMapper.getDatastore().run(query);

Function<Entity, Subscriber> transformer = new Function<Entity, Subscriber>() {
    @Nullable
    @Override
    public Subscriber apply(@Nullable Entity input) {
        return datastoreHelper.getDatastoreMapper().demap(input, Subscriber.class);
    }
};
Iterator<Subscriber> subsribers = Iterators.transform(result, transformer);

```

## Dependencies
- google-cloud 0.5.1
- fasterxml jackson-databind 2.7.3
