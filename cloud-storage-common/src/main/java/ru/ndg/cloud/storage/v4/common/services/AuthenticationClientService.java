package ru.ndg.cloud.storage.v4.common.services;

import io.netty.channel.Channel;
import ru.ndg.cloud.storage.v4.common.model.*;

public class AuthenticationClientService {
    public void sendAuthentication(Channel channel, String login, String password) {
        channel.writeAndFlush(new AuthenticationRequest(login, password));
    }

    public void sendRegistration(Channel channel, String login, String password) {
        channel.writeAndFlush(new RegistrationRequest(login, password));
    }
}