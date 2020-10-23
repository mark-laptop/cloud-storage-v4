package ru.ndg.cloud.storage.v4.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadResponse implements Message {

    private String fileName;
    private byte[] data;
}
