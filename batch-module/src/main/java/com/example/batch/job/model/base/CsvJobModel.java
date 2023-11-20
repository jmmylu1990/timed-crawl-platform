package com.example.batch.job.model.base;

import com.example.batch.annotation.ItemReaderIgnore;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public class CsvJobModel extends AbstractJobModel {
     public static String[] getSortedFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(item -> item.getAnnotation(ItemReaderIgnore.class) == null)
                .sorted(Comparator.comparingInt(field -> getIndex(clazz, field)))
                .map(Field::getName)
                .toArray(String[]::new);
    }

    private static int getIndex(Class<?> clazz, Field field) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].equals(field)) {
                return i;
            }
        }
        return -1;
    }
}
