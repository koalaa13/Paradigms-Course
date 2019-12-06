package queue;

import java.util.*;
import java.io.*;

public class ArrayQueue {
    private int start = 1, end = 1, capacity = 0;
    private Object[] array;

    public ArrayQueue() {
        start = 1;
        end = 1;
        capacity = 0;
        array = new Object[2];
    }

    public void enqueue(Object a) {
        assert a != null;
        if (capacity == array.length - 1) {
            ensureCapacity();
        }
        array[end--] = a;
        capacity++;
        if (end == 0) {
            end = array.length - 1;
        }
    }

    public void push(Object a) {
        assert a != null;
        if (capacity == array.length - 1) {
            ensureCapacity();
        }
        start++;
        if (start == array.length) {
            start = 1;
        }
        array[start] = a;
        capacity++;
    }

    public Object peek() {
        if (end + 1 == array.length) {
            return array[1];
        }
        return array[end + 1];
    }

    public Object remove() {
        assert capacity > 0;
        end++;
        if (end == array.length) {
            end = 1;
        }
        Object res = array[end];
        capacity--;
        return res;
    }

    public Object dequeue() {
        assert capacity > 0;
        Object res = array[start--];
        if (start == 0) {
            start = array.length - 1;
        }
        capacity--;
        return res;
    }

    public Object element() {
        assert capacity > 0;
        return array[start];
    }

    public int size() {
        return capacity;
    }

    public boolean isEmpty() {
        return capacity == 0;
    }

    public void clear() {
        start = array.length - 1;
        end = array.length - 1;
        capacity = 0;
    }

    private void ensureCapacity() {
        int n = array.length;
        Object[] buff = new Object[2 * n];
        for (int ind = buff.length - 1, cnt = 0, i = start; cnt < capacity; ++cnt, --ind) {
            buff[ind] = array[i--];
            if (i == 0) {
                i = n - 1;
            }
        }
        array = buff;
        start = array.length - 1;
        end = start - capacity;
    }
}
