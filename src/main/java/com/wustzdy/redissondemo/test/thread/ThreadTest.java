package com.wustzdy.redissondemo.test.thread;

public class ThreadTest {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new ThreadDemo());
        Thread thread2 = new Thread(new ThreadDemo());
        Thread thread3 = new Thread(new ThreadDemo());
        Thread thread4 = new Thread(new ThreadDemo());
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    private static class ThreadDemo implements Runnable {
        @Override
        public void run() {
            System.out.println("线程：" + Thread.currentThread().getId());
        }
    }
}
