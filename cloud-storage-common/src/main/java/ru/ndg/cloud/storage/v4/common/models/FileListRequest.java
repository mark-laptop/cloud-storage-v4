package ru.ndg.cloud.storage.v4.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileListRequest implements Message {

    private String login;
}
