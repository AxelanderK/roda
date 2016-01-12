/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.roda.core.RodaCoreFactory;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.Representation;
import org.roda.core.model.DescriptiveMetadata;
import org.roda.core.model.ModelService;
import org.roda.core.model.PreservationMetadata;
import org.roda.core.model.ValidationException;
import org.roda.core.storage.Binary;
import org.roda.core.storage.ClosableIterable;
import org.roda.core.storage.StoragePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidationUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);
  private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

  /** Private empty constructor */
  private ValidationUtils() {

  }

  /**
   * Validates all descriptive metadata files contained in the AIP
   * 
   * @throws AuthorizationDeniedException
   * @throws NotFoundException
   * @throws RequestNotValidException
   * @throws GenericException
   * 
   * @throws ValidationException
   */
  public static boolean isAIPDescriptiveMetadataValid(ModelService model, String aipId, boolean failIfNoSchema)
    throws GenericException, RequestNotValidException, NotFoundException, AuthorizationDeniedException {
    boolean valid = true;
    ClosableIterable<DescriptiveMetadata> descriptiveMetadataBinaries = model.listDescriptiveMetadataBinaries(aipId);
    try {
      for (DescriptiveMetadata descriptiveMetadata : descriptiveMetadataBinaries) {
        if (!isDescriptiveMetadataValid(model, descriptiveMetadata, failIfNoSchema)) {
          valid = false;
          break;
        }
      }
    } finally {
      try {
        descriptiveMetadataBinaries.close();
      } catch (IOException e) {
        LOGGER.error("Error while while freeing up resources", e);
      }
    }
    return valid;
  }

  /**
   * Validates all preservation metadata files contained in the AIP
   * 
   * @throws RequestNotValidException
   * @throws GenericException
   * @throws NotFoundException
   * @throws AuthorizationDeniedException
   * 
   * @throws ValidationException
   */
  public static boolean isAIPPreservationMetadataValid(ModelService model, String aipId, boolean failIfNoSchema)
    throws NotFoundException, GenericException, RequestNotValidException, AuthorizationDeniedException {
    boolean valid = true;
    ClosableIterable<Representation> representations = model.listRepresentations(aipId);
    try {
      for (Representation representation : representations) {
        ClosableIterable<PreservationMetadata> preservationMetadataBinaries = model
          .listPreservationMetadataBinaries(aipId, representation.getId());
        try {
          for (PreservationMetadata preservationMetadata : preservationMetadataBinaries) {
            if (!isPreservationMetadataValid(model, preservationMetadata, failIfNoSchema)) {
              valid = false;
              break;
            }
          }
        } finally {
          try {
            preservationMetadataBinaries.close();
          } catch (IOException e) {
            LOGGER.error("Error while while freeing up resources", e);
          }
        }
      }
    } finally {
      try {
        representations.close();
      } catch (IOException e) {
        LOGGER.error("Error while while freeing up resources", e);
      }
    }

    return valid;
  }

  /**
   * Validates descriptive medatada (e.g. against its schema, but other
   * strategies may be used)
   * 
   * @param failIfNoSchema
   * @throws AuthorizationDeniedException
   * @throws NotFoundException
   * @throws RequestNotValidException
   * @throws GenericException
   * @throws ValidationException
   */
  public static boolean isDescriptiveMetadataValid(ModelService model, DescriptiveMetadata metadata,
    boolean failIfNoSchema)
      throws GenericException, RequestNotValidException, NotFoundException, AuthorizationDeniedException {
    boolean ret;
    try {
      StoragePath storagePath = metadata.getStoragePath();
      Binary binary = model.getStorage().getBinary(storagePath);
      validateDescriptiveBinary(binary, metadata.getType(), failIfNoSchema);
      ret = true;
    } catch (ValidationException e) {
      ret = false;
    }

    return ret;
  }

  /**
   * Validates preservation medatada (e.g. against its schema, but other
   * strategies may be used)
   * 
   * @param failIfNoSchema
   * @throws AuthorizationDeniedException
   * @throws NotFoundException
   * @throws RequestNotValidException
   * @throws GenericException
   * @throws ValidationException
   */
  public static boolean isPreservationMetadataValid(ModelService model, PreservationMetadata metadata,
    boolean failIfNoSchema)
      throws GenericException, RequestNotValidException, NotFoundException, AuthorizationDeniedException {
    boolean ret;
    try {
      StoragePath storagePath = metadata.getStoragePath();
      Binary binary = model.getStorage().getBinary(storagePath);
      validatePreservationBinary(binary);
      ret = true;
    } catch (ValidationException e) {
      ret = false;
    }

    return ret;
  }

  /**
   * Validates descriptive medatada (e.g. against its schema, but other
   * strategies may be used)
   * 
   * @param descriptiveMetadataType
   * 
   * @param failIfNoSchema
   * @throws ValidationException
   */
  public static void validateDescriptiveBinary(Binary binary, String descriptiveMetadataType, boolean failIfNoSchema)
    throws ValidationException {
    try {
      InputStream inputStream = binary.getContent().createInputStream();
      String lowerCaseDescriptiveMetadataType = descriptiveMetadataType.toLowerCase();
      InputStream schemaStream = RodaCoreFactory
        .getConfigurationFileAsStream("schemas/" + lowerCaseDescriptiveMetadataType + ".xsd");
      if (schemaStream != null) {
        // FIXME is inputstream closed???
        Source xmlFile = new StreamSource(inputStream);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
        Validator validator = schema.newValidator();
        RodaErrorHandler errorHandler = new RodaErrorHandler();
        validator.setErrorHandler(errorHandler);
        try {
          validator.validate(xmlFile);
          List<SAXParseException> errors = errorHandler.getErrors();
          if (errors.size() > 0) {
            throw new ValidationException("Error validating descriptive binary ", errorHandler.getErrors());
          }
        } catch (SAXException e) {
          LOGGER.debug("Error validating descriptive binary " + descriptiveMetadataType, e);
          throw new ValidationException("Error validating descriptive binary ", errorHandler.getErrors());
        }
      } else {
        if (failIfNoSchema) {
          throw new ValidationException("No schema to validate " + descriptiveMetadataType);
        }
      }
    } catch (SAXException | IOException e) {
      throw new ValidationException("Error validating descriptive binary: " + e.getMessage());
    }

  }

  /**
   * Validates preservation medatada (e.g. against its schema, but other
   * strategies may be used)
   * 
   * @param descriptiveMetadataId
   * 
   * @param failIfNoSchema
   * @throws ValidationException
   */
  public static void validatePreservationBinary(Binary binary) throws ValidationException {
    try {
      InputStream inputStream = binary.getContent().createInputStream();
      InputStream schemaStream = RodaCoreFactory.getConfigurationFileAsStream("schemas/premis-v2-0.xsd");
      if (schemaStream != null) {
        Source xmlFile = new StreamSource(inputStream);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
        Validator validator = schema.newValidator();
        RodaErrorHandler errorHandler = new RodaErrorHandler();
        validator.setErrorHandler(errorHandler);
        try {
          validator.validate(xmlFile);
          List<SAXParseException> errors = errorHandler.getErrors();
          if (errors.size() > 0) {
            throw new ValidationException("Error validating preservation binary ", errorHandler.getErrors());
          }
        } catch (SAXException e) {
          LOGGER.debug("Error validating preservation binary " + binary.getStoragePath().asString(), e);
          throw new ValidationException("Error validating preservation binary ", errorHandler.getErrors());
        }
      } else {
        throw new ValidationException("No schema to validate " + binary.getStoragePath().asString());
      }
    } catch (SAXException | IOException e) {
      throw new ValidationException("Error validating preservation binary: " + e.getMessage());
    }

  }

  private static class RodaErrorHandler extends DefaultHandler {
    List<SAXParseException> errors;

    public RodaErrorHandler() {
      errors = new ArrayList<SAXParseException>();
    }

    public void warning(SAXParseException e) throws SAXException {
      errors.add(e);
    }

    public void error(SAXParseException e) throws SAXException {
      errors.add(e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
      errors.add(e);
    }

    public List<SAXParseException> getErrors() {
      return errors;
    }

    public void setErrors(List<SAXParseException> errors) {
      this.errors = errors;
    }

  }
}
