package com.ft.methodeimagebinarymapper.validation;

import com.ft.methodeimagebinarymapper.model.EomFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishingValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(PublishingValidator.class);
  private static final String IMAGE_TYPE = "Image";
  private static final String PDF_TYPE = "Pdf";

  public boolean isValidImageForPublishing(EomFile eomFile) {
    return IMAGE_TYPE.equals(eomFile.getType()) && isBinaryValueValid(eomFile);
  }

  public boolean isValidPDFForPublishing(EomFile eomFile) {
    return PDF_TYPE.equals(eomFile.getType()) && isBinaryValueValid(eomFile);
  }

  private boolean isBinaryValueValid(EomFile eomFile) {
    return !missingBinaryData(eomFile);
  }

  private boolean missingBinaryData(EomFile eomFile) {
    byte[] value = eomFile.getValue();
    if (value == null || eomFile.getValue().length == 0) {
      LOGGER.info(String.format("Image [%s] has no image bytes.", eomFile.getUuid()));
      return true;
    }
    return false;
  }
}
