package com.company;

import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator {
    //константа для максимального числа итераций
    public static final int MAX_ITERATIONS = 2000;

    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        //начальный диапозон
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }

    //итеративная функция для фрактала
    @Override
    public int numIterations(double x, double y) {
        //итерация
        int iteration = 0;
        //z_real и z_imaginary это 2 части комплексного числа
        double z_real = 0;
        double z_imaginary = 0;

        //считаю Zn = Zn-1^2. делается пока Z^2 > 4 и iteration < 2000
        while (iteration < MAX_ITERATIONS && z_real * z_real + z_imaginary * z_imaginary < 4) {
            double z_real_new = z_real * z_real - z_imaginary * z_imaginary + x;
            double z_imaginary_new = -2 * z_real * z_imaginary + y;
            z_real = z_real_new;
            z_imaginary = z_imaginary_new;
            iteration += 1;
        }

        //проверка с максимальными итерациями
        if (iteration == MAX_ITERATIONS) {
            return -1;
        }

        return iteration;
    }

    public String toString() {
        return "Tricon";
    }

}