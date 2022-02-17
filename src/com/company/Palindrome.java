package com.company;

import java.util.Scanner;

public class Palindrome {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
        }
        Scanner in = new Scanner(System.in);
        System.out.print("Введите проверяемую строку: ");
        String s = in.nextLine();
        System.out.println(isPalindrome(s));
    }
    public static String reverseString(String s){
        String a = "";
        for (int i = s.length() - 1; i >= 0; i--)
            a += s.charAt(i);
        return a;
    }
    public static boolean isPalindrome(String s){
        String changedText = s.toLowerCase();
        if (changedText.equals(reverseString(changedText)))
            System.out.println(s + " is a palindrome");
        else
            System.out.println(s + " is not a palindrome");
        return s.equals(s);
    }
}