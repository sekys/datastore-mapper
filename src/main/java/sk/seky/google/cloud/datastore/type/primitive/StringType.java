package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.*;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public class StringType implements Serializer<String>, Deserializer<String> {
    @Override
    public Value serialize(String value) {
        return StringValue.of(value);
    }

    @Override
    public String deserialize(Value value) throws DatastoreException {
        return (String) value.get();
    }
}
