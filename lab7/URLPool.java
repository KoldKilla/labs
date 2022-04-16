package com.company;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class URLPool {

    //два списка для хранения посещённых и планируемых посетить URL
    private LinkedList<URLDepthPair> visited = new LinkedList<>();
    private LinkedList<URLDepthPair> planned = new LinkedList<>();

    //максимальная глубина и количество ожидающих URL
    private int maxDepth;
    private int waitingAmount = 0;

    //конструктор для добавления пары в список URL планируемых посетить
    URLPool(URLDepthPair pair, int maxDepth) {
        planned.add(pair);
        this.maxDepth = maxDepth;
    }

    //метод для удаления пары из списка планируемых и добавления в список посещённых
    public synchronized URLDepthPair first() {
        //вылавливаю ошибку, если данного элемента нет
        try {
            URLDepthPair pair = planned.removeFirst();
            visited.add(pair);
            return pair;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    //метод для повторного посещения просмотренных URL
    public synchronized void add(URLDepthPair pair) {
        //если список посещённых содержит пару, и пара этой глубины меньше или равна максимальной, то...
        if (!visited.contains(pair) && pair.getDepth() <= maxDepth) {
            //пробуждает все потоки с помощью notifyAll()
            this.notifyAll();
            waitingAmount = 0;
            planned.addLast(pair);
        } else {
            visited.addLast(pair);
        }
    }

    //увеличение количества ожидающих потоков
    public synchronized void incWaitingAmount() {
        waitingAmount++;
    }

    //возвращение количества ожидающих потоков
    public synchronized int getWaitingAmount() {
        return waitingAmount;
    }

}