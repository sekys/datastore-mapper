package sk.seky.google.cloud.datastore.type.primitive;

import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;
import sk.seky.google.cloud.datastore.DatastoreMapper;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;

/**
 * Created by lsekerak on 6. 11. 2016.
 * // "excludeFromIndexes": true
 */
public class ObjectType implements Serializer<Object>, Deserializer<Object> {
    private final DatastoreMapper mapper;
    private final Class type;

    public ObjectType(DatastoreMapper mapper) {
        this.mapper = mapper;
        this.type = null;
    }

    public ObjectType(DatastoreMapper mapper, Class type) {
        this.mapper = mapper;
        this.type = type;
    }

    @Override
    public Value serialize(Object value) throws Exception {
        if (value == null) {
            return NullValue.of();
        }
        return EntityValue.of(mapper.map(value));
    }

    @Override
    public Object deserialize(Value value) throws DatastoreException {
        return mapper.demap(((EntityValue) value).get(), type);
    }
}

