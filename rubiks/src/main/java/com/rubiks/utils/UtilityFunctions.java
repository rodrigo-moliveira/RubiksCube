package com.rubiks.utils;

public class UtilityFunctions {
    public static int getRandomIntegerBetweenRange(double min, double max){
        return (int) ((int)(Math.random()*((max-min)+1))+min);
    }
}
