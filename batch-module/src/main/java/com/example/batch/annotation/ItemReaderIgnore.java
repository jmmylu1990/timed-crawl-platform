package com.example.batch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 設置註解保留策略
@Target(ElementType.FIELD) // 設置註解可以應用在哪些元素上（這裡是FIELD，表示可以用在屬性上）
public @interface ItemReaderIgnore {
}
