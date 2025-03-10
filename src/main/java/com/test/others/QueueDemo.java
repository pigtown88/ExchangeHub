package com.test.others;

import java.util.LinkedList;
import java.util.Queue;

public class QueueDemo {
	public static void main(String[] args) {
		Queue<String> queue = new LinkedList<>();
		queue.offer("顧客1");
		queue.offer("顧客2");
		queue.offer("顧客3");

		// 直接列印整個佇列
		System.out.println("當前佇列的完整內容：" + queue);

		// 依序取出並列印
		System.out.println("第一個結帳的顧客是：" + queue.poll());

		// 再次列印剩餘佇列
		System.out.println("移除第一個顧客後的佇列：" + queue);

		// 查看下一個顧客
		System.out.println("下一個結帳的顧客是：" + queue.peek());

		// 最終佇列狀態
		System.out.println("目前佇列的最終狀態：" + queue);
	}
}
