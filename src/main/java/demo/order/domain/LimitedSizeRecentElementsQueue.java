package demo.order.domain;

import java.util.*;

public class LimitedSizeRecentElementsQueue<K> extends LinkedList<K> {

    private int maxSize;

    public LimitedSizeRecentElementsQueue(int size) {
        this.maxSize = size;
    }

    public boolean add(K elem) {
        boolean r = super.offerFirst(elem);
        removeOldests();
        return r;
    }

    private void removeOldests() {
        if (size() > maxSize) {
            removeRange(maxSize, size());
        }
    }

    public List<K> getLatest(int latest) {
        return new ArrayList<>(subList(0, Math.min(latest, size())));
    }

}
