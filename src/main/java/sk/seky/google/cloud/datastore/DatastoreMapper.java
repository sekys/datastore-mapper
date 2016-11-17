package sk.seky.google.cloud.datastore;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.*;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;
import sk.seky.google.cloud.datastore.type.LocalDateTimeType;
import sk.seky.google.cloud.datastore.type.primitive.*;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lsekerak on 17. 10. 2016.
 */
public class DatastoreMapper {
    private final ObjectMapper mapper;

    private final Map<Class, Serializer> serializers;
    private final Map<Class, Deserializer> deserializers;
    private final Datastore datastore;

    @Inject
    public DatastoreMapper(ObjectMapper mapper, Datastore datastore) {
        this.datastore = datastore;
        this.serializers = new HashMap<>();
        this.deserializers = new HashMap<>();
        this.mapper = mapper;

        register(new BooleanType(), boolean.class, Boolean.class);
        register(new LongType(), int.class, Integer.class, long.class, Long.class);
        register(new StringType(), String.class);
        register(new LocalDateTimeType(), LocalDateTime.class);
    }

    public FullEntity map(Object object) throws Exception {
        if (object == null) {
            return null;
        }
        Class cl = object.getClass();
        FullEntity.Builder builder = Entity.builder();
        for (Field field : cl.getDeclaredFields()) {
            // if field is private then look for setters/getters
            if (Modifier.isPrivate(field.getModifiers())) {
                String name = field.getName();
                // TODO: field @Id
                Id id = field.getAnnotation(Id.class);
                Method getter = Util.getGetter(cl, name);
                Object result = getter.invoke(object);
                Class<?> klassType = field.getType();
                Value value = null;
                if (result == null) {
                    value = NullValue.of();
                } else if (klassType.equals(List.class)) {
                    Type ref = field.getGenericType();
                    JavaType javaType = mapper.getTypeFactory().constructType(ref);
                    klassType = javaType.getContentType().getRawClass();
                    Serializer serializer = serializers.get(klassType);
                    if (serializer == null) {
                        // Je to dalsia entita v liste
                        serializer = new ObjectType(this);
                    }
                    value = new ListType(serializer).serialize((List<Object>) result);
                } else if (klassType.equals(Set.class)) {
                    Type ref = field.getGenericType();
                    JavaType javaType = mapper.getTypeFactory().constructType(ref);
                    klassType = javaType.getContentType().getRawClass();
                    Serializer serializer = serializers.get(klassType);
                    if (serializer == null) {
                        // Je to dalsia entita v liste
                        serializer = new ObjectType(this);
                    }
                    value = new SetType(serializer).serialize((Set<Object>) result);
                } else {
                    Serializer serializer = serializers.get(klassType);
                    // mozno to je dalsi objekt
                    if (serializer == null) {
                        serializer = new ObjectType(this);
                    }
                    value = serializer.serialize(result);
                }
                if (id != null) {
                    IncompleteKey key;
                    if (result instanceof String) {
                        key = datastore.newKeyFactory().setKind(cl.getName()).newKey((String) result);
                    } else if (result instanceof Long) {
                        key = datastore.newKeyFactory().setKind(cl.getName()).newKey(result.toString());
                    } else {
                        throw new UnsupportedOperationException(klassType.getName());
                    }
                    builder.setKey(key);
                } else {
                    builder.set(name, value);
                }
            }
        }
        return builder.build();
    }

    public <T> T demap(FullEntity entity, Class<T> type) throws DatastoreException {
        if (entity == null) {
            return null;
        }
        try {
            T instance = type.newInstance();
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers())) {
                    Object arg;
                    String name = field.getName();
                    Class<?> fieldType = field.getType();
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        arg = entity.getKey().getKind();
                    } else {
                        Value attribute = entity.getValue(name);
                        if (attribute instanceof NullValue) {
                            arg = null;
                        } else if (fieldType.equals(List.class)) {
                            Type ref = field.getGenericType();
                            JavaType javaType = mapper.getTypeFactory().constructType(ref);
                            Class inType = javaType.getContentType().getRawClass();
                            Deserializer deserializer = deserializers.get(inType);
                            if (deserializer == null) {
                                deserializer = new ObjectType(this, inType);
                            }
                            arg = new ListType(deserializer).deserialize(attribute);
                        } else if (fieldType.equals(Set.class)) {
                            Type ref = field.getGenericType();
                            JavaType javaType = mapper.getTypeFactory().constructType(ref);
                            Class inType = javaType.getContentType().getRawClass();
                            Deserializer deserializer = deserializers.get(inType);
                            if (deserializer == null) {
                                deserializer = new ObjectType(this, inType);
                            }
                            arg = new SetType(deserializer).deserialize(attribute);
                        } else {
                            Deserializer deserializer = deserializers.get(fieldType);
                            if (deserializer == null) {
                                //throw new DatastoreException(500, "class not found", type.getName());
                                deserializer = new ObjectType(this, fieldType);
                            }
                            arg = deserializer.deserialize(attribute);
                        }
                    }

                    Method setter = Util.getSetter(type, name, fieldType);
                    setter.invoke(instance, new Object[]{arg});
                }
            }
            return instance;
        } catch (NoSuchMethodException e) {
            throw new DatastoreException(500, e.getMessage(), "error", e);
        } catch (IllegalAccessException e) {
            throw new DatastoreException(500, e.getMessage(), "error", e);
        } catch (InvocationTargetException e) {
            throw new DatastoreException(500, e.getMessage(), "error", e);
        } catch (InstantiationException e) {
            throw new DatastoreException(500, e.getMessage(), "error", e);
        }
    }

    private void register(Object obj, Class... types) {
        for (Class type : types) {
            register(obj, type);
        }
    }

    private void register(Object obj, Class type) {
        if (obj instanceof Serializer) {
            serializers.put(type, (Serializer) obj);
        }
        if (obj instanceof Deserializer) {
            deserializers.put(type, (Deserializer) obj);
        }
    }

    public void save(Object object) throws Exception {
        FullEntity created = this.map(object);
        datastore.put(created);
    }

    public <T> T get(String key, Class<T> type) {
        KeyFactory statusKeys = datastore.newKeyFactory().setKind(type.getName());
        Entity entity = datastore.get(statusKeys.newKey(key));
        return this.demap(entity, type);
    }

    public Datastore getDatastore() {
        return datastore;
    }
}

