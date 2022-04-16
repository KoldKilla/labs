package com.company;

//реализуем интерфейс раннабл
public class CrawlerTask implements Runnable {

    private final URLPool pool;
    private final int threadsAmount;

    public CrawlerTask(URLPool pool, int threadsAmount) {
        this.pool = pool;
        this.threadsAmount = threadsAmount;
    }

    @Override
    public void run() {

        //Пока количество ожидаемых меньше количества потоков, мы получаем пару
        while (pool.getWaitingAmount() < threadsAmount) {
            URLDepthPair pair = pool.first();

            //если пара не null, мы начинаем с ней работать
            if (pair != null) {
                for (URLDepthPair urlPair : Crawler.getSites(pair)) {
                    pool.add(urlPair);
                }
            }
            //если пара null, то мы блокируем доступ к pool, если его использует другой поток
            else {
                try {
                    synchronized (pool) {
                        //увеличиваем количество ожидающих потоков на 1 и выводим
                        pool.incWaitingAmount();
                        System.out.println(pool.getWaitingAmount());
                        //если количество ожидающих потоков равно всем возможным, то мы все пробуждаем, если нет,
                        //то ставим его на паузу
                        if (pool.getWaitingAmount() == threadsAmount) {
                            pool.notifyAll();
                        }
                        else {
                            pool.wait();
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}