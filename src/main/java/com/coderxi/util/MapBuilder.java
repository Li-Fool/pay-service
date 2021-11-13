package com.coderxi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Map工具类
 * @author 汐涌及岸
 */
public class MapBuilder<K, V> {

  private Map<K, V> map;

  /**
   * 指定map实例
   */
  public static <K, V> MapBuilder<K, V> from(Map<K, V> map) {
    MapBuilder<K, V> mapBuilder = new MapBuilder<>();
    mapBuilder.map = map;
    return mapBuilder;
  }

  /**
   * 从对象转换map实例
   */
  @SneakyThrows(IllegalAccessException.class)
  public static MapBuilder<String, Object> from(Object obj) {
    MapBuilder<String, Object> mapBuilder = new MapBuilder<>();
    mapBuilder.map = new HashMap<>();
    for (Field field : obj.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      mapBuilder.map.put(field.getName(), field.get(obj));
    }
    return mapBuilder;
  }

  /**
   * 锁定k,v类型为首个元素的类型
   */
  public static <K, V> MapBuilder<K, V> pre(K k, V v) {
    return MapBuilder.from(new HashMap<K, V>()).put(k, v);
  }

  /**
   * 锁定k类型为首个元素的类型,v为object
   */
  public static <K> MapBuilder<K, Object> pro(K k, Object v) {
    return MapBuilder.from(new HashMap<K, Object>()).put(k, v);
  }

  /**
   * 添加
   */
  public MapBuilder<K, V> put(K k, V v) {
    map.put(k, v);
    return this;
  }

  /**
   * 返回map本体
   */
  public Map<K, V> end() {
    return map;
  }

  /**
   * 按json返回
   */
  @SneakyThrows(JsonProcessingException.class)
  public String json() {
    return new ObjectMapper().writeValueAsString(map);
  }

}
