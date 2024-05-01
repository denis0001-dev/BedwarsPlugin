package ru.morozovit.util;

public class Random {
    public static int randomNumber(int min, int max) {
        return (int) ((Math.random() * (max-min)) + min);
    }
}
