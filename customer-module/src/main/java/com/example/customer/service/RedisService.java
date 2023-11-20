package com.example.customer.service;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    /**
     * 普通缓存放入
     *
     * @param key 鍵
     * @param value 值
     * @return
     */
    void setNormalCache(String key, Object value);

    /**
     * 普通緩存放入並設定時間
     *
     * @param key 鍵
     * @param value 值
     * @param time 時間(秒) time要大於0 如果time小於等於0 將設定無限期
     * @return
     */
    void setCacheWithTime(String key, Object value, long time);

    /**
     * 指定緩存失效時間
     *
     * @param key 鍵
     * @param time 時間(秒)
     * @return
     */
     void setExpireTime(String key, long time);

    /**
     * 根據key取得過期時間
     *
     * @param key 鍵 不能為null
     * @return 時間(秒) 回傳0代表為永久有效
     */
      long getExpireTime(String key);

    /**
     * 判斷key是否存在
     *
     * @param key 鍵
     * @return true 存在 false不存在
     */
     boolean hasKeyInCache(String key);

    /**
     * 刪除緩存
     *
     * @param key 可以傳一個值 或多個
     */
     void deleteChche(String... key);

    /**
     * 普通緩存獲取
     *
     * @param key 鍵
     * @return 值
     */
    Object getNormalCache(String key);

    /**
     * 遞增
     *
     * @param key 鍵
     * @param delta 要增加幾個(大於0)
     * @return
     */
     long increment(String key, long delta);

    /**
     * 遞減
     *
     * @param key 鍵
     * @param delta 要減少幾(小於0)
     * @return
     */
     long decrement(String key, long delta);

    /**
     * 獲取 Redis 伺服器信息
     *
     * @param type 要取得的屬性類型
     * @return 屬性資訊
     */
     Properties getRedisInfo(String type);
}
