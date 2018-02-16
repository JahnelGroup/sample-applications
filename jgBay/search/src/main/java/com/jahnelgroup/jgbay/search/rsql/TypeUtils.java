package com.jahnelgroup.jgbay.search.rsql;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.ReflectionUtils;

import javax.persistence.ElementCollection;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class TypeUtils {

    private static final Map<String, Function<String, ?>> CAST_MAP;

    static {
        final Map<String, Function<String, ?>> tmp = new HashMap<>();
        tmp.put(String.class.getSimpleName(), s -> s);
        tmp.put(Integer.class.getSimpleName(), Integer::parseInt);
        tmp.put(Long.class.getSimpleName(), Long::parseLong);
        tmp.put(Double.class.getSimpleName(), Double::parseDouble);
        tmp.put(BigDecimal.class.getSimpleName(), s -> BigDecimal.valueOf(Double.parseDouble(s)));
        tmp.put(Boolean.class.getSimpleName(), Boolean::valueOf);
        tmp.put(LocalDate.class.getSimpleName(), TypeUtils::toLocalDate);
        tmp.put(LocalDateTime.class.getSimpleName(), TypeUtils::toLocalDateTime);
        tmp.put(Date.class.getSimpleName(), TypeUtils::toDate);
        CAST_MAP = Collections.unmodifiableMap(tmp);
    }

    public static <T> Object convert(Class<T> type, String val) {
        if(canConvert(type)) {
            return CAST_MAP.get(type.getSimpleName()).apply(val);
        }
        return val;
    }

    public static Boolean canConvert(Class<?> type) {
        return CAST_MAP.containsKey(type.getSimpleName());
    }

    /**
     * Finds the actual path to the searched property.<br/>
     * Takes JsonProperty and ElementCollection mapping into consideration.
     *
     * @param fields the list of sub-fields representing the queried path
     * @param type   the current type to search the fields in
     */
    public static Stream<Field> findJavaPropertyRecursive(List<String> fields, Class<?> type) {
        String queriedName = fields.get(0);
        Field field = ReflectionUtils.findField(type, queriedName);
        if (field == null) {
            List<Field> fieldList = new LinkedList<>();
            ReflectionUtils.doWithFields(type, fieldList::add);
            field = fieldList.stream()
                    .filter(f -> isMappedAsJsonProperty(f, queriedName))
                    .findFirst()
                    .orElseThrow(() -> new RsqlException(String.format("Field %s is not a valid field.", queriedName)));
        }

        // if the found field is ElementCollection - just skip the rest of the search
        if (field.getAnnotation(ElementCollection.class) != null) {
            return Stream.of(field);
        }

        if (fields.size() == 1) {
            return Stream.of(field);
        } else {
            Stream<Field> subProperties = findJavaPropertyRecursive(fields.subList(1, fields.size()), field.getType());
            return Stream.concat(Stream.of(field), subProperties);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Object> castValues(Class<?> type, List<String> arguments, String propertyPath, boolean isNull) {
        List<Object> args = new ArrayList<>();

        for (String argument : arguments) {
            final Object parsedArgument;
            if (isNull) {
                parsedArgument = argument;
            } else if (type.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
                Enum[] enumValues = (enumClass).getEnumConstants();
                parsedArgument = Arrays.stream(enumValues)
                        .filter(e -> e.name().equalsIgnoreCase(argument))
                        .findFirst()
                        .orElseThrow(() -> new RsqlEnumException(argument, enumValues, enumClass, propertyPath));
            } else {
                parsedArgument = TypeUtils.convert(type, argument);
            }
            args.add(parsedArgument);
        }

        return args;
    }

    /**
     * Returns true if the given class field is mapped to a given json name.
     */
    private static boolean isMappedAsJsonProperty(Field field, String jsonName) {
        final JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        return jsonProperty != null && jsonProperty.value().equals(jsonName);
    }

    private static LocalDate toLocalDate(String s) {
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            throw new RsqlException("Unable to parse LocalDate from string: " + s);
        }
    }

    private static LocalDateTime toLocalDateTime(String s) {
        try {
            return LocalDateTime.parse(s);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));
            } catch (Exception e2) {
                try {
                    return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                } catch (Exception e3) {
                    throw new RsqlException("Unable to parse LocalDateTime from string: " + s);
                }
            }
        }
    }

    private static Date toDate(String s) {
        try {
            return new SimpleDateFormat("").parse(s);
        } catch (Exception e) {
            throw new RsqlException("Unable to parse Date from string: " + s);
        }
    }

}
