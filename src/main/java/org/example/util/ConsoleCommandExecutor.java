package org.example.util;

import org.example.thread.CheckerThreadHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleCommandExecutor {

    private final CheckerThreadHolder checkerThreadHolder;

    public ConsoleCommandExecutor(CheckerThreadHolder checkerThreadHolder) {
        this.checkerThreadHolder = checkerThreadHolder;
    }

    public void startCommandLoop() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command;

        System.out.println("Введите команду:");

        label:
        while (true) {
            try {
                command = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            switch (command) {
                case "start program":
                    checkerThreadHolder.startCheckers();
                    System.out.println("Программа запущена");
                    break;
                case "stop program":
                    checkerThreadHolder.stopAllCheckers();
                    System.out.println("Программа остановлена");
                    break;
                case "exit":
                    System.out.println("Выход из программы");
                    break label;
                default:
                    System.out.println("Неверная команда, попробуйте еще раз");
                    break;
            }
        }
    }
}

