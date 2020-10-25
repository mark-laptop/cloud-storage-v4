package ru.ndg.cloud.storage.v4.common.services;

import com.sun.istack.internal.NotNull;
import io.netty.channel.Channel;
import ru.ndg.cloud.storage.v4.common.model.*;

public class AuthenticationClientService {
    public void sendAuthentication(@NotNull Channel channel, @NotNull String login, @NotNull String password) {
        channel.writeAndFlush(new AuthenticationRequest(login, password));
    }

    public void sendRegistration(@NotNull Channel channel, @NotNull String login, @NotNull String password) {
        channel.writeAndFlush(new RegistrationRequest(login, password));
    }
}