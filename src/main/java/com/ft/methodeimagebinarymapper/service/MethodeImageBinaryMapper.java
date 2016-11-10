package com.ft.methodeimagebinarymapper.service;

import com.ft.methodeimagebinarymapper.model.BinaryContent;
import com.ft.methodeimagebinarymapper.model.EomFile;

import java.util.Date;

public class MethodeImageBinaryMapper {

    public BinaryContent mapImageBinary(EomFile eomFile, String transactionId, Date lastModifiedDate) {
        return new BinaryContent(eomFile.getUuid(), eomFile.getValue(), lastModifiedDate, transactionId);
    }

}
