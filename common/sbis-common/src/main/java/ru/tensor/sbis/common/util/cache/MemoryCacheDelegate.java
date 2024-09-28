package ru.tensor.sbis.common.util.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Кэш для хранения в оперативной памяти объектов указанного типа в словаре ограниченного размера
 * с возможностью быстрого поиска
 *
 * @param <K> тип ключа
 * @param <T> тип хранимых данных
 *
 * @author ev.grigoreva
 */
public class MemoryCacheDelegate<K, T> {
    @NonNull
    private LruCache<K, T> mCache;
    @NonNull
    private DataProvider<K, T> mDataProvider;

    public MemoryCacheDelegate(int size, @NonNull DataProvider<K, T> provider) {
        mCache = new LruCache<>(size);
        mDataProvider = provider;
    }

    /**
     * Интерфейс для загрузки не найденных в кеше данных
     *
     * @param <Key>  тип ключа
     * @param <Type> тип данных
     */
    public interface DataProvider<Key, Type> {
        /**
         * Получить словарь данных по набору ключей
         *
         * @param keys список ключей
         * @return словарь данных
         */
        @NonNull
        HashMap<Key, Type> loadData(@NonNull List<Key> keys);

        /**
         * Получить элемент по ключу
         *
         * @param key ключ
         * @return элемент
         */
        @Nullable
        Type loadDataByKey(@NonNull Key key);
    }

    /**
     * @param key ключ
     * @return данные по ключу
     */
    @Nullable
    public T getDataByKey(@NonNull K key) {
        T value = mCache.get(key);
        if (value == null) {
            value = mDataProvider.loadDataByKey(key);
            if (value != null) {
                mCache.put(key, value);
            }
        }
        return value;
    }

    /**
     * Получение словаря данных по списку ключей
     * Данные, которые отсутствуют в кеше, будут загружены
     * методом {@link DataProvider#loadData(List)},
     * добавлены в кеш и в результирующий словарь данных
     *
     * @param keys список ключей
     * @return словарь данных
     */
    @NonNull
    public HashMap<K, T> getData(@NonNull List<K> keys) {
        SearchResultInCache searchResultInCache = getSearchResultInCache(keys);
        if (!searchResultInCache.getNotFoundKeys().isEmpty()) {
            HashMap<K, T> newCachedData = mDataProvider.loadData(searchResultInCache.getNotFoundKeys());
            putAllData(newCachedData);
            searchResultInCache.getFoundData().putAll(newCachedData);
        }
        return searchResultInCache.getFoundData();
    }

    /**
     * Получение списка данных по списку ключей
     *
     * @param keys список ключей
     * @return список данных
     */
    @NonNull
    public ArrayList<T> getDataList(@NonNull List<K> keys) {
        return new ArrayList<>(getData(keys).values());
    }

    /**
     * Добавление нового значения в кеш
     *
     * @param key   ключ
     * @param value значение
     */
    public void putDataByKey(@NonNull K key, @NonNull T value) {
        mCache.put(key, value);
    }

    /**
     * Метод добавления словаря данных в кеш
     * Работает по принципу "более новые данные заменяют более старые"
     *
     * @param map данные для добавления
     */
    public void putAllData(@NonNull HashMap<K, T> map) {
        for (K key : map.keySet()) {
            if (key == null) {
                continue;
            }
            T value = map.get(key);
            if (value == null) {
                continue;
            }
            mCache.put(key, value);
        }
    }

    /**
     * Очистка кеша
     */
    public void clearCache() {
        mCache.evictAll();
    }

    /**
     * Поиск данных в кеше
     *
     * @param keys список ключей, по которому осуществляется поиск
     * @return объект, содержащий информацию о том, какие данные были найдены по указанным ключам,
     * а для каких ключей соответствия не нашлось
     */
    @NonNull
    private SearchResultInCache getSearchResultInCache(@NonNull List<K> keys) {
        SearchResultInCache searchResult = new SearchResultInCache(keys.size());
        for (K key : keys) {
            if (key == null) {
                continue;
            }
            T value = mCache.get(key);
            if (value != null) {
                searchResult.putFoundByKeyData(key, value);
            } else {
                searchResult.addNotFoundKey(key);
            }
        }
        return searchResult;
    }

    /**
     * Результат поиска данных в кеше
     * {@link #mFoundData} словарь данных, содержащихся в кеше, полученных по спику ключей для поиска
     * {@link #mNotFoundKeys} список ключей, по которым данные в кеше не были найдены
     */
    private class SearchResultInCache {
        @NonNull
        private HashMap<K, T> mFoundData = new HashMap<>();
        @NonNull
        private ArrayList<K> mNotFoundKeys = new ArrayList<>();

        /**
         * @param maxMapSize максимальный размер словаря
         */
        SearchResultInCache(int maxMapSize) {
            mFoundData = new HashMap<>(maxMapSize);
            mNotFoundKeys = new ArrayList<>();
        }

        /**
         * @return данные, найденные в кеше
         */
        @NonNull
        HashMap<K, T> getFoundData() {
            return mFoundData;
        }

        /**
         * @return список ключей, по которому данные не были найдены в кеше
         */
        @NonNull
        ArrayList<K> getNotFoundKeys() {
            return mNotFoundKeys;
        }

        /**
         * Добавление объекта в словарь найденных
         *
         * @param key   ключ
         * @param value значение
         */
        void putFoundByKeyData(@NonNull K key, @NonNull T value) {
            mFoundData.put(key, value);
        }

        /**
         * Добавление ключа в список не найденных
         *
         * @param key ключ
         */
        void addNotFoundKey(@NonNull K key) {
            if (!mNotFoundKeys.contains(key)) {
                mNotFoundKeys.add(key);
            }
        }
    }
}
