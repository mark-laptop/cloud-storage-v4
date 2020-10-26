package ru.ndg.cloud.storage.v4.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegistrationResponse implements Message {

    private boolean result;
}
