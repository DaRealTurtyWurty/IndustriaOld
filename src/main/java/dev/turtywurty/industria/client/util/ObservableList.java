package dev.turtywurty.industria.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

public class ObservableList<T> extends ArrayList<T> {
    public interface ListChangeListener<T> {
        void onAdd(T item);

        void onRemove(T item);

        void onClear();

        void onUpdate(T original, T updated);
    }

    private final ListChangeListener<T> listener;

    public ObservableList(ListChangeListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public boolean add(T item) {
        boolean result = super.add(item);
        if (result) this.listener.onAdd(item);
        return result;
    }

    @Override
    public T remove(int index) {
        T item = super.remove(index);
        if (item != null) this.listener.onRemove(item);
        return item;
    }

    @Override
    public boolean remove(Object item) {
        boolean result = super.remove(item);
        if (result) this.listener.onRemove((T) item);
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = super.removeAll(c);
        if (result) c.forEach(item -> this.listener.onRemove((T) item));
        return result;
    }

    @Override
    public T set(int index, T item) {
        T original = super.set(index, item);
        if (original != null) this.listener.onUpdate(original, item);
        return original;
    }

    @Override
    public void clear() {
        super.clear();
        this.listener.onClear();
    }

    @Override
    public void add(int index, T item) {
        super.add(index, item);
        this.listener.onAdd(item);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> items) {
        boolean result = super.addAll(index, items);
        if (result) items.forEach(this.listener::onAdd);
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> items) {
        boolean result = super.addAll(items);
        if (result) items.forEach(this.listener::onAdd);
        return result;
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        super.replaceAll(operator);
        this.listener.onClear();
        this.forEach(this.listener::onAdd);
    }

    @Override
    public void sort(java.util.Comparator<? super T> c) {
        List<T> copy = List.copyOf(this);
        super.sort(c);
        for (int index = 0; index < this.size(); index++) {
            T original = copy.get(index);
            T updated = this.get(index);
            if (!original.equals(updated)) this.listener.onUpdate(original, updated);
        }
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        removeAll(subList(fromIndex, toIndex));
    }

    @Override
    public boolean removeIf(java.util.function.Predicate<? super T> filter) {
        return removeAll(stream().filter(filter).toList());
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return removeAll(stream().filter(item -> !c.contains(item)).toList());
    }

    public void setAll(Collection<? extends T> items) {
        super.clear();
        super.addAll(items);
    }

    public static <T> ObservableList<T> create(Runnable listener) {
        return new ObservableList<>(new ListChangeListener<>() {
            @Override
            public void onAdd(T item) {
                listener.run();
            }

            @Override
            public void onRemove(T item) {
                listener.run();
            }

            @Override
            public void onClear() {
                listener.run();
            }

            @Override
            public void onUpdate(T original, T updated) {
                listener.run();
            }
        });
    }
}
