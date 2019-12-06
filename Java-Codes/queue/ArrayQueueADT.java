package queue;

import java.util.*;
import java.io.*;

public class ArrayQueueADT {
    private int start, end, capacity;
    private Object[] array;

    public ArrayQueueADT() {
        start = 1;
        end = 1;
        capacity = 0;
        array = new Object[2];
    }

    public static void enqueue(ArrayQueueADT q, Object a) {
        assert a != null;
        if (q.capacity == q.array.length - 1) {
            ensureCapacity(q);
        }
        q.array[q.end--] = a;
        q.capacity++;
        if (q.end == 0) {
            q.end = q.array.length - 1;
        }
    }

    public static void push(ArrayQueueADT q, Object a) {
        assert a != null;
        if (q.capacity == q.array.length - 1) {
            ensureCapacity(q);
        }
        q.start++;
        if (q.start == q.array.length) {
            q.start = 1;
        }
        q.array[q.start] = a;
        q.capacity++;
    }

    public static Object peek(ArrayQueueADT q) {
        if (q.end + 1 == q.array.length) {
            return q.array[1];
        }
        return q.array[q.end + 1];
    }

    public static Object remove(ArrayQueueADT q) {
        assert q.capacity > 0;
        q.end++;
        if (q.end == q.array.length) {
            q.end = 1;
        }
        Object res = q.array[q.end];
        q.capacity--;
        return res;
    }

    public static Object dequeue(ArrayQueueADT q) {
        assert q.capacity > 0;
        Object res = q.array[q.start--];
        if (q.start == 0) {
            q.start = q.array.length - 1;
        }
        q.capacity--;
        return res;
    }

    public static Object element(ArrayQueueADT q) {
        assert q.capacity > 0;
        return q.array[q.start];
    }

    public static int size(ArrayQueueADT q) {
        return q.capacity;
    }

    public static boolean isEmpty(ArrayQueueADT q) {
        return q.capacity == 0;
    }

    public static void clear(ArrayQueueADT q) {
        q.start = q.array.length - 1;
        q.end = q.array.length - 1;
        q.capacity = 0;
    }

    private static void ensureCapacity(ArrayQueueADT q) {
        int n = q.array.length;
        Object[] buff = new Object[2 * n];
        for (int ind = buff.length - 1, cnt = 0, i = q.start; cnt < q.capacity; ++cnt, --ind) {
            buff[ind] = q.array[i--];
            if (i == 0) {
                i = n - 1;
            }
        }
        q.array = buff;
        q.start = q.array.length - 1;
        q.end = q.start - q.capacity;
    }
}
