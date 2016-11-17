package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.*;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public class LongType implements Serializer<Long>, Deserializer<Long> {
    @Override
    public Value serialize(Long value) {
        return LongValue.of(value);
    }

    @Override
    public Long deserialize(Value value) throws DatastoreException {
        return (Long) value.get();
    }
}
