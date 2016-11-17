package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsekerak on 6. 11. 2016.
 */
public class ListType implements Serializer<List<Object>>, Deserializer<List<Object>> {
    private final Serializer serializer;
    private final Deserializer deserializer;

    public ListType(Serializer serializer) {
        this.serializer = serializer;
        this.deserializer = null;
    }

    public ListType(Deserializer deserializer) {
        this.serializer = null;
        this.deserializer = deserializer;
    }

    @Override
    public Value serialize(List<Object> oldList) throws Exception {
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
    public List<Object> deserialize(Value x) throws DatastoreException {
        List<? extends Value<?>> oldList = ((ListValue) x).get();
        List<Object> newList = new ArrayList<>(oldList.size());
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
