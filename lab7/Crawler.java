package com.company;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Crawler {

    private static int depth;
    private static int numThreads;

    public static LinkedList<URLDepthPair> getSites(URLDepthPair pair) {
        //список ссылок
        LinkedList<URLDepthPair> links = new LinkedList<>();
        //пробую просмотреть сайт на наличие ссылок
        try {
            //получаю данные с сайта
            URL url = new URL(pair.getUrl());
            Scanner scanner = new Scanner(url.openStream());

            //пока есть следующая строка, я просматриваю её на наличие ссылок
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parseLine(links, line, pair.getDepth());
            }
        }
        //вылавливаю ошибку, когда не удаётся подключиться к сайту
        catch (IOException e) {
            System.out.println("Cannot connect: " + pair.getUrl());
        }

        return links;
    }

    //метод для разбора строки
    public static void parseLine(LinkedList<URLDepthPair> links, String line, int depth) {
        //должно быть <a href="[любой_URL-адрес_начинающийся_с_http://]">
        if (line.contains("<a")) {
            line = line.substring(line.indexOf("<a"));
            if (line.contains("href=")) {
                line = line.substring(line.indexOf("href="));
                //пробую создать нормальную ссылку и добавить её в список. Если не получается, то игнорируем ту ссылку
                try {
                    //создаю нормальную ссылку вида https://... и добавляю её в список
                    line = line.substring(line.indexOf(URLDepthPair.URL_PREFIX));
                    String suburl = line.substring(0, line.indexOf("\""));
                    links.add(new URLDepthPair(suburl, depth + 1));
                    line = line.substring(suburl.length());
                    parseLine(links, line, depth);
                }
                catch (StringIndexOutOfBoundsException ignored) {	}
            }
        }
    }

    public static void main(String[] args) {

        //проверка на введённые данные(3 штуки)
        if (args.length != 3) {
            System.out.println("usage: java Crawler <URL> <depth> <number of crawler threads>");
            System.exit(1);
        }
        // если правильно, то продолжаем
        else {
            try {
                // проверяю цифры ли 2 и 3 аргументы
                depth = Integer.parseInt(args[1]);
                numThreads = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException nfe) {
                // 2 и 3 аргументы не цифры
                System.out.println("usage: java Crawler <URL> <depth> <number of crawler threads>");
                System.exit(1);
            }
        }
        //3 списка, для найденных, посещённых и для URL и максимальная глубина
        LinkedList<URLDepthPair> visited = new LinkedList<>();
        LinkedList<URLDepthPair> urls = new LinkedList<>();
        LinkedList<URLDepthPair> finded = new LinkedList<>();
        int maxDepth = Integer.parseInt(args[1]);

        //добавляю 1 URL(исходные данные) в список URL
        urls.add(new URLDepthPair(args[0], 0));

        //пока в списке URL что-то есть, мы проходим по нему
        while (!urls.isEmpty()) {
            for (URLDepthPair pair : urls) {
                //если посещённые не содержат данную пару и глубина пары меньше или равна максимальной,
                //то я вывожу пару, добавляю её в список найденных и посещённых
                if (!visited.contains(pair) && pair.getDepth() <= maxDepth) {
                    System.out.println(pair);
                    finded.addAll(Crawler.getSites(pair));
                    visited.add(pair);
                }
            }

            //переносим все найденные URL в список URL
            urls.clear();
            urls.addAll(finded);
            finded.clear();
        }
    }
}