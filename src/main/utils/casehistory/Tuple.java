package uk.gov.dwp.utils.casehistory;

public class Tuple<K, V> {

    private K key;
    private V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Tuple(V value) {
        this(null, value);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + this.key + ", " + this.value + ")";
    }
}
