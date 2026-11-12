package com.practice.bank;

import java.util.*;

public class StyleViolation {
    private int Bad_Name = 0;

    public int add(int a,int b){
        if (a > b) return a;
        return a+b;
    }

    public List<String> names() {
        return new ArrayList<String>();
    }
}
