package ru.ndg.cloud.storage.v4.server;

import ru.ndg.cloud.storage.v4.server.network.Network;

public class MainApp {
    public static void main(String[] args) {
        Network network = new Network();
        network.run();
    }
}
