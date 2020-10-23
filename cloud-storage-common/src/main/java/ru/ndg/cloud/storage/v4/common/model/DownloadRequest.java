package ru.ndg.cloud.storage.v4.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadRequest implements Message {

    private String login;
    private String fileName;
}
