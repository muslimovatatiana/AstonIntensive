package ru.aston.hometask1;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class AstonHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private AstonNode<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public AstonHashMap() {
        this.buckets = (AstonNode<K, V>[]) new AstonNode[DEFAULT_CAPACITY];
    }

    public V get(K key) {
        int hash = hash(key);
        int index = getBucketIndex(hash, buckets.length);
        AstonNode<K, V> currentNode = buckets[index];

        while (currentNode != null) {
            if (currentNode.getHash() == hash && isCurrentKeyEqualsKey(currentNode, key)) {
                return currentNode.getValue();
            }
            currentNode = currentNode.getNext();
        }
        return null;
    }

    public void put(K key, V value) {
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }

        int hash = hash(key);
        int index = getBucketIndex(hash, buckets.length);
        AstonNode<K, V> currentNode = buckets[index];

        if (currentNode == null) {
            buckets[index] = new AstonNode<>(hash, key, value, null);
            size++;
            return;
        }

        while (true) {
            if (currentNode.getHash() == hash && isCurrentKeyEqualsKey(currentNode, key)) {
                currentNode.setValue(value);
                return;
            }
            if (currentNode.getNext() == null) {
                break;
            }
            currentNode = currentNode.getNext();
        }

        currentNode.setNext(new AstonNode<>(hash, key, value, null));
        size++;
    }

    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int getBucketIndex(int hash, int capacity) {
        return hash & (capacity - 1);
    }

    public V remove(K key) {
        int hash = hash(key);
        int index = getBucketIndex(hash, buckets.length);
        AstonNode<K, V> currentNode = buckets[index];
        AstonNode<K, V> previousNode = null;

        while (currentNode != null) {
            if (currentNode.getHash() == hash && isCurrentKeyEqualsKey(currentNode, key)) {
                V removedValue = currentNode.getValue();

                if (previousNode == null) {
                    buckets[index] = currentNode.getNext();
                } else {
                    previousNode.setNext(currentNode.getNext());
                }

                size--;
                return removedValue;
            }
            previousNode = currentNode;
            currentNode = currentNode.getNext();
        }
        return null;
    }

    private boolean isCurrentKeyEqualsKey(AstonNode<K, V> current, K key) {
        return current.getKey() == key || (current.getKey() != null && current.getKey().equals(key));
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = buckets.length * 2;
        AstonNode<K, V>[] newBuckets = (AstonNode<K, V>[]) new AstonNode[newCapacity];

        Arrays.stream(buckets)
                .filter(Objects::nonNull)
                .forEach(bucket -> {
                    AstonNode<K, V> currentNode = bucket;
                    while (currentNode != null) {
                        AstonNode<K, V> nextNode = currentNode.getNext();
                        int newIndex = getBucketIndex(currentNode.getHash(), newCapacity);

                        currentNode.setNext(newBuckets[newIndex]);
                        newBuckets[newIndex] = currentNode;

                        currentNode = nextNode;
                    }
                });

        buckets = newBuckets;
    }

    public Stream<AstonNode<K, V>> nodesStream() {
        return Arrays.stream(buckets)
                .filter(Objects::nonNull)
                .flatMap(headNode -> {
                    Stream.Builder<AstonNode<K, V>> builder = Stream.builder();
                    AstonNode<K, V> currentNode = headNode;
                    while (currentNode != null) {
                        builder.accept(currentNode);
                        currentNode = currentNode.getNext();
                    }
                    return builder.build();
                });
    }

    public Stream<K> keysStream() {
        return nodesStream().map(AstonNode::getKey);
    }

    public Stream<V> valuesStream() {
        return nodesStream().map(AstonNode::getValue);
    }

    public int size() {
        return this.size;
    }

    private static class AstonNode<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private AstonNode<K, V> next;

        public AstonNode(int hash, K key, V value, AstonNode<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public int getHash() { return hash; }
        public K getKey() { return key; }
        public V getValue() { return value; }
        public void setValue(V value) { this.value = value; }
        public AstonNode<K, V> getNext() { return next; }
        public void setNext(AstonNode<K, V> next) { this.next = next; }
    }
}
