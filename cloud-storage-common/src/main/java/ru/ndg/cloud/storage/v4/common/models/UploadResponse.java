package ru.ndg.cloud.storage.v4.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadResponse implements Message {

    private String fileList;
}
