package ru.ndg.cloud.storage.v4.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RenameResponse implements Message {

    private String fileList;
}
