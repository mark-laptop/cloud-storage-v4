package ru.ndg.cloud.storage.v4.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationRequest implements Message {

    private String login;
    private String password;
}
