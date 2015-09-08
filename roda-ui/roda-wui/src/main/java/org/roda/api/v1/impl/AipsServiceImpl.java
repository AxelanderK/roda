package org.roda.api.v1.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.roda.api.v1.AipsService;
import org.roda.api.v1.ApiResponseMessage;
import org.roda.api.v1.NotFoundException;
import org.roda.model.DescriptiveMetadata;
import org.roda.model.ModelService;
import org.roda.model.ModelServiceException;
import org.roda.model.utils.ModelUtils;
import org.roda.storage.Binary;
import org.roda.storage.ClosableIterable;
import org.roda.storage.StoragePath;
import org.roda.storage.StorageService;
import org.roda.storage.StorageServiceException;

import pt.gov.dgarq.roda.common.RodaCoreFactory;
import pt.gov.dgarq.roda.core.data.v2.Representation;
import pt.gov.dgarq.roda.disseminators.common.tools.ZipEntryInfo;
import pt.gov.dgarq.roda.disseminators.common.tools.ZipTools;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-09-03T11:38:49.275+01:00")
public class AipsServiceImpl extends AipsService {
  
      @Override
      public Response aipsGet(String start,String limit)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic2!")).build();
  }
  
      @Override
      public Response aipsAipIdGet(String aipId,String acceptFormat)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdPut(String aipId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdPost(String aipId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDelete(String aipId)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataGet(String aipId,String start,String limit)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdGet(String aipId,String representationId,String acceptFormat)
      throws NotFoundException {
    	  try{
    		  if(acceptFormat!=null && acceptFormat.equalsIgnoreCase("zip")){
	    	      ModelService model = RodaCoreFactory.getModelService();
	    	      StorageService storage = RodaCoreFactory.getStorageService();
	    	      Representation representation = model.retrieveRepresentation(aipId, representationId);
	    	      
	    	      List<ZipEntryInfo> zipEntries = new ArrayList<ZipEntryInfo>();
	    	      List<String> fileIds = representation.getFileIds();
	    	      if(fileIds!=null && fileIds.size()>0){
	    	    	  for(String fileId : fileIds){
	    	    		  StoragePath filePath = ModelUtils.getRepresentationFilePath(aipId, representationId, fileId);
	    	    		  Binary binary = storage.getBinary(filePath);
	    	    		  Path tempFile = Files.createTempFile("test", ".tmp");
	    	    		  Files.copy(binary.getContent().createInputStream(), tempFile,StandardCopyOption.REPLACE_EXISTING);
	    	    		  ZipEntryInfo info = new ZipEntryInfo(filePath.getName(), tempFile.toFile());
	    	    		  zipEntries.add(info);
	    	    	  }
	    	      }
	    	      StreamingOutput stream = new StreamingOutput() {
	    	    	    @Override
	    	    	    public void write(OutputStream os) throws IOException,
	    	    	    WebApplicationException {
	    	      	      ZipTools.zip(zipEntries, os);
	    	    	    }
	    	    	  };
	    	    	  
	    	    	  return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
	    	    	            .header("content-disposition","attachment; filename = "+representationId+".zip")
	    	    	            .build();
    		  }else{
    			  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Not yet implemented")).build();
    		  }
    	  }catch(IOException e){
    		  e.printStackTrace();
    		  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
    	  } catch (StorageServiceException e) {
    		  e.printStackTrace();
    		  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
		} catch (ModelServiceException e) {
			e.printStackTrace();
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
		}
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdPut(String aipId,String representationId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdPost(String aipId,String representationId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdDelete(String aipId,String representationId)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdFileIdGet(String aipId,String representationId,String fileId,String acceptFormat)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdFileIdPut(String aipId,String representationId,String fileId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdFileIdPost(String aipId,String representationId,String fileId,String filepath)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDataRepresentationIdFileIdDelete(String aipId,String representationId,String fileId)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDescriptiveMetadataGet(String aipId,String start,String limit, String acceptFormat)
      throws NotFoundException {
    	  try{
    		  if(acceptFormat!=null && acceptFormat.equalsIgnoreCase("zip")){
	    	      ModelService model = RodaCoreFactory.getModelService();
	    	      StorageService storage = RodaCoreFactory.getStorageService();
	    	      ClosableIterable<DescriptiveMetadata> metadata = model.listDescriptiveMetadataBinaries(aipId);
	    	      int startInt = (start==null)?0:Integer.parseInt(start);
	    	      int limitInt = (limit==null)?-1:Integer.parseInt(limit);
	
	    	      int counter = 0;
	    	      List<ZipEntryInfo> zipEntries = new ArrayList<ZipEntryInfo>();
	    	      for(DescriptiveMetadata dm : metadata){
	    	    	  if(counter>=startInt && (counter<=limitInt || limitInt==-1)){
	    	    		  Binary binary = storage.getBinary(dm.getStoragePath());
	    	    		  Path tempFile = Files.createTempFile("test", ".tmp");
	    	    		  Files.copy(binary.getContent().createInputStream(), tempFile,StandardCopyOption.REPLACE_EXISTING);
	    	    		  ZipEntryInfo info = new ZipEntryInfo(dm.getStoragePath().getName(), tempFile.toFile());
	    	    		  zipEntries.add(info);
	    	    	  }
	    	      }
	    	      StreamingOutput stream = new StreamingOutput() {
	    	    	    @Override
	    	    	    public void write(OutputStream os) throws IOException,
	    	    	    WebApplicationException {
	    	      	      ZipTools.zip(zipEntries, os);
	    	    	    }
	    	    	  };
	    	    	  return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
	    	    	            .header("content-disposition","attachment; filename = "+aipId+".zip")
	    	    	            .build();
    		  }else{
    			  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "Not yet implemented")).build();
    		  }
    	  }catch(IOException e){
    		  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
    	  } catch (StorageServiceException e) {
    		  return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
		} catch (ModelServiceException e) {
			return Response.serverError().entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
		}
     
  }
  
      @Override
      public Response aipsAipIdDescriptiveMetadataMetadataIdGet(String aipId,String metadataId,String acceptFormat)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDescriptiveMetadataMetadataIdPut(String aipId,String metadataId,FormDataContentDisposition fileDetail,String metadataType)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDescriptiveMetadataMetadataIdPost(String aipId,String metadataId,FormDataContentDisposition fileDetail,String metadataType)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
      @Override
      public Response aipsAipIdDescriptiveMetadataMetadataIdDelete(String aipId,String metadataId)
      throws NotFoundException {
      // do some magic!
      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
  }
  
}
