package com.wustzdy.redissondemo.test.thread;

public class ThreadTest {
    public static void main(String[] args) {
        Thread thread = new Thread(new ThreadDemo());
        thread.start();
    }

    private static class ThreadDemo implements Runnable {
        @Override
        public void run() {
            System.out.println("线程：" + Thread.currentThread().getId());
        }
    }
}
