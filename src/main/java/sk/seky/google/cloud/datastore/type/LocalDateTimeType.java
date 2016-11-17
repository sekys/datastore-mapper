package sk.seky.google.cloud.datastore.type;

import com.google.cloud.datastore.*;
import org.joda.time.LocalDateTime;
import sk.seky.google.cloud.datastore.Deserializer;
import sk.seky.google.cloud.datastore.Serializer;


/**
 * Created by lsekerak on 17. 10. 2016.
 */
public class LocalDateTimeType implements Serializer<LocalDateTime>, Deserializer<LocalDateTime> {
    @Override
    public Value serialize(LocalDateTime value) {
        return DateTimeValue.of(DateTime.copyFrom(value.toDate()));
    }

    @Override
    public LocalDateTime deserialize(Value value) throws DatastoreException {
        DateTime date = (DateTime) value.get();
        return LocalDateTime.fromDateFields(date.toDate());
    }
}
