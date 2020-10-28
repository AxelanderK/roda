package org.roda.core.plugins.plugins.internal.disposal;

import static org.roda.core.data.common.RodaConstants.PLUGIN_CATEGORY_NOT_LISTABLE;
import static org.roda.core.data.common.RodaConstants.PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE;
import static org.roda.core.data.common.RodaConstants.PreservationEventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roda.core.data.exceptions.AlreadyExistsException;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.InvalidParameterException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.LiteOptionalWithCause;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.disposal.DisposalConfirmationAIPEntry;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.PluginParameter;
import org.roda.core.data.v2.jobs.PluginState;
import org.roda.core.data.v2.jobs.PluginType;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.validation.ValidationException;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.AbstractPlugin;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginException;
import org.roda.core.plugins.RODAObjectsProcessingLogic;
import org.roda.core.plugins.orchestrate.JobPluginInfo;
import org.roda.core.plugins.plugins.PluginHelper;
import org.roda.core.storage.StorageService;
import org.roda.core.util.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class CreateDisposalConfirmationPlugin extends AbstractPlugin<AIP> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateDisposalConfirmationPlugin.class);

  private String title;

  private static final Map<String, PluginParameter> pluginParameters = new HashMap<>();
  static {
    pluginParameters.put(PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE,
      new PluginParameter(PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE, "Disposal confirmation title",
        PluginParameter.PluginParameterType.STRING, "", true, false, "Disposal confirmation report title"));
  }

  @Override
  public List<PluginParameter> getParameters() {
    ArrayList<PluginParameter> parameters = new ArrayList<>();
    parameters.add(pluginParameters.get(PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE));
    return parameters;
  }

  @Override
  public void setParameterValues(Map<String, String> parameters) throws InvalidParameterException {
    super.setParameterValues(parameters);
    if (parameters.containsKey(PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE)) {
      title = parameters.get(PLUGIN_PARAMS_DISPOSAL_CONFIRMATION_TITLE);
    }
  }

  @Override
  public String getVersionImpl() {
    return "1.0";
  }

  public static String getStaticName() {
    return "Create disposal confirmation report";
  }

  @Override
  public String getName() {
    return getStaticName();
  }

  public static String getStaticDescription() {
    return "";
  }

  @Override
  public String getDescription() {
    return getStaticDescription();
  }

  @Override
  public PreservationEventType getPreservationEventType() {
    return PreservationEventType.POLICY_ASSIGNMENT;
  }

  @Override
  public String getPreservationEventDescription() {
    return "Create disposal confirmation report";
  }

  @Override
  public String getPreservationEventSuccessMessage() {
    return "";
  }

  @Override
  public String getPreservationEventFailureMessage() {
    return "";
  }

  @Override
  public void init() throws PluginException {
    // do nothing
  }

  @Override
  public Report beforeAllExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report execute(IndexService index, ModelService model, StorageService storage,
    List<LiteOptionalWithCause> liteList) throws PluginException {
    return PluginHelper.processObjects(this,
      (RODAObjectsProcessingLogic<AIP>) (index1, model1, storage1, report, cachedJob, jobPluginInfo, plugin,
        objects) -> processAIP(model1, index1, report, jobPluginInfo, cachedJob, objects),
      index, model, storage, liteList);
  }

  private void processAIP(ModelService model, IndexService index, Report report, JobPluginInfo jobPluginInfo,
    Job cachedJob, List<AIP> aips) {

    Set<String> disposalSchedules = new HashSet<>();
    Set<String> disposalHolds = new HashSet<>();
    String confirmationId = IdUtils.createUUID();
    long storageSize = 0L;
    long numberOfCollection = 0L;

    for (AIP aip : aips) {
      String outcomeText;
      Report reportItem = PluginHelper.initPluginReportItem(this, aip.getId(), AIP.class);
      PluginHelper.updatePartialJobReport(this, model, reportItem, false, cachedJob);
      LOGGER.debug("Processing AIP {}", aip.getId());

      try {
        // Fetch the AIP information to crystallize in the confirmation report
        DisposalConfirmationAIPEntry entry = DisposalConfirmationPluginUtils.getAIPEntryFromAIP(index, aip,
          disposalSchedules, disposalHolds);
        model.addAIPEntry(confirmationId, entry);

        // Mark the AIP as "on confirmation" so they cannot be added to another
        // confirmation
        aip.setDisposalConfirmationId(confirmationId);
        model.updateAIP(aip, cachedJob.getUsername());

        // increment the storage size
        storageSize += entry.getAipSize();

        // increment the number of collection
        // TODO find out if this is the correct way to count the collections maybe a
        // configuration property to adapt which "level" to count
        if ("collection".equals(entry.getAipLevel())) {
          numberOfCollection++;
        }

        // Copy disposal schedules
        model.copyDisposalScheduleToConfirmationReport(confirmationId, disposalSchedules);

        // Copy disposal holds
        model.copyDisposalHoldToConfirmationReport(confirmationId, disposalHolds);

        // create report metadata
        model.createDisposalConfirmationMetadata(
          DisposalConfirmationPluginUtils.getDisposalConfirmationMetadata(confirmationId, title, storageSize,
            disposalHolds, disposalSchedules, aips.size(), numberOfCollection),
          cachedJob.getUsername());

        reportItem.setPluginState(PluginState.SUCCESS);
        reportItem.setPluginDetails("Add AIP to disposal confirmation report: " + confirmationId);
        jobPluginInfo.incrementObjectsProcessedWithSuccess();
        outcomeText = PluginHelper.createOutcomeTextForDisposalConfirmation(
          "was successfully added to disposal confirmation", confirmationId, aip.getId());
      } catch (RequestNotValidException | GenericException | NotFoundException | AuthorizationDeniedException
        | AlreadyExistsException e) {
        LOGGER.error("Error creating disposal confirmation {}: {}", confirmationId, e.getMessage(), e);
        jobPluginInfo.incrementObjectsProcessedWithFailure();
        reportItem.setPluginState(PluginState.FAILURE)
          .setPluginDetails("Error creating disposal confirmation " + confirmationId + ": " + e.getMessage());
        outcomeText = PluginHelper.createOutcomeTextForDisposalConfirmation(
          "failed to be added to disposal confirmation", confirmationId, aip.getId());
      } finally {
        report.addReport(reportItem);
        PluginHelper.updatePartialJobReport(this, model, reportItem, true, cachedJob);
      }

      try {
        PluginHelper.createPluginEvent(this, aip.getId(), model, index, null, null, reportItem.getPluginState(),
          outcomeText, true, cachedJob);
      } catch (ValidationException | RequestNotValidException | NotFoundException | GenericException
        | AuthorizationDeniedException | AlreadyExistsException e) {
        LOGGER.error("Error creating event: {}", e.getMessage(), e);
      }
    }
  }

  @Override
  public Report afterAllExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public void shutdown() {
    // do nothing
  }

  @Override
  public List<Class<AIP>> getObjectClasses() {
    return Collections.singletonList(AIP.class);
  }

  @Override
  public PluginType getType() {
    return PluginType.AIP_TO_AIP;
  }

  @Override
  public List<String> getCategories() {
    return Collections.singletonList(PLUGIN_CATEGORY_NOT_LISTABLE);
  }

  @Override
  public Plugin<AIP> cloneMe() {
    return new CreateDisposalConfirmationPlugin();
  }

  @Override
  public boolean areParameterValuesValid() {
    return true;
  }
}
