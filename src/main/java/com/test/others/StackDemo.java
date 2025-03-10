package com.test.others;

import java.util.Stack;

public class StackDemo {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.push("盤子1");
        stack.push("盤子2");
        stack.push("盤子3");
        //peek是讀取 pop 是拿走
        System.out.println("現在盤子狀態是：" + stack);

        System.out.println("堆疊頂部的盤子是：" + stack.peek());
        System.out.println("現在盤子狀態是：" + stack);
        System.out.println("拿走最上面的盤子：" + stack.pop());
        System.out.println("現在盤子狀態是：" + stack);

        System.out.println("現在堆疊頂部的盤子是：" + stack.peek());
        System.out.println("拿走最上面的盤子：" + stack.pop());
        System.out.println("現在盤子狀態是：" + stack);

        System.out.println("現在堆疊頂部的盤子是：" + stack.peek());
        System.out.println("拿走最上面的盤子：" + stack.pop());
       
    }
}
