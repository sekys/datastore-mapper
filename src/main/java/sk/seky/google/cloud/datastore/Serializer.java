package sk.seky.google.cloud.datastore;

import com.google.cloud.datastore.Value;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public interface Serializer<T> {
    Value serialize(T value) throws Exception;
}
