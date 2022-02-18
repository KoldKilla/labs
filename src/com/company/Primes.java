package com.company;

import java.util.Scanner;

public class Primes {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print(" Введите число меньше которого будут простые числа: ");
        int maxNumber = in.nextInt();
        System.out.println("Prime Numbers: ");

        for(int numbers = 2; numbers <= maxNumber; ++numbers) {
            boolean outputNumbers = isPrime(numbers);
            if (outputNumbers) {
                System.out.print(numbers + " ");
            }
        }

    }

    public static boolean isPrime(int numbers) {
        for(int j = 2; j < numbers; ++j) {
            if (numbers % j == 0) {
                return false;
            }
        }

        return true;
    }
}