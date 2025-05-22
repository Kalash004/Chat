package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Basic settings for json
    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        MAPPER.configure(SerializationFeature.WRAP_EXCEPTIONS, true);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        MAPPER.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    /**
     * Convert object to JSON string
     *
     * @param value object
     * @return serialized Object
     */
    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new RuntimeException(String.format("Unable to map %s to json", value.getClass()), e);
        }
    }

    /**
     * Convert JSON string to the object
     *
     * @param json JSON string
     * @param type requested object type
     * @return de-serialized JSON
     */
    public static <T> T fromJson(String json, Class<T> type) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        return MAPPER.readValue(json, type);
    }
}
