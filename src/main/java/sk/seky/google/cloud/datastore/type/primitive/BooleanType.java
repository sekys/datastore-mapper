package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Value;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public class BooleanType implements Serializer<Boolean>, Deserializer<Boolean> {
    @Override
    public Value serialize(Boolean value) {
        return BooleanValue.of(value);
    }

    @Override
    public Boolean deserialize(Value value) throws DatastoreException {
        return (Boolean) value.get();
    }
}

