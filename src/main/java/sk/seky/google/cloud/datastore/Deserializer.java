package sk.seky.google.cloud.datastore;

import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Value;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public interface Deserializer<T> {
    T deserialize(Value value) throws DatastoreException;
}
