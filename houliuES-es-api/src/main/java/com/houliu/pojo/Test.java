package com.houliu.pojo;

public class Test {
    public static void main(String[] args) {
        int i = 1;
        int j = i++; //2
// 不执行if
        if ((j > ++j) && (i++ == j)) {
            j += i;
        }
//输出j=2
        System.out.println(j);
    }
}