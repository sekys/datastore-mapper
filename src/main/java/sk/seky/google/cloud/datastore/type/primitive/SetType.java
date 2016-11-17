package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

import java.util.*;

/**
 * Created by lsekerak on 12. 11. 2016.
 */
public class SetType implements Serializer<Set<Object>>, Deserializer<Set<Object>> {
    private final Serializer serializer;
    private final Deserializer deserializer;

    public SetType(Serializer serializer) {
        this.serializer = serializer;
        this.deserializer = null;
    }

    public SetType(Deserializer deserializer) {
        this.serializer = null;
        this.deserializer = deserializer;
    }

    @Override
    public Value serialize(Set<Object> oldList) throws Exception {
        ListValue.Builder builder = ListValue.newBuilder();
        Value value;
        for (Object item : oldList) {
            if (item == null) {
                value = NullValue.of();
            } else {
                value = serializer.serialize(item);
            }
            builder.addValue(value);
        }
        return builder.build();
    }

    @Override
    public Set<Object> deserialize(Value x) throws DatastoreException {
        List<? extends Value<?>> oldList = ((ListValue) x).get();
        Set<Object> newList = new HashSet<>(oldList.size());
        Object object;
        for (Value item : oldList) {
            if (item instanceof NullValue) {
                object = null;
            } else {
                object = deserializer.deserialize(item);
            }
            newList.add(object);
        }
        return newList;
    }
}
