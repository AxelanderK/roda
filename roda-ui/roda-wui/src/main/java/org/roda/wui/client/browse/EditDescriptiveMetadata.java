/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
/**
 * 
 */
package org.roda.wui.client.browse;

import java.util.List;
import java.util.Map;

import org.roda.wui.client.common.UserLogin;
import org.roda.wui.common.client.HistoryResolver;
import org.roda.wui.common.client.tools.Tools;
import org.roda.wui.common.client.widgets.Toast;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.BrowseMessages;

/**
 * @author Luis Faria
 * 
 */
public class EditDescriptiveMetadata extends Composite {

  public static final HistoryResolver RESOLVER = new HistoryResolver() {

    @Override
    public void resolve(List<String> historyTokens, final AsyncCallback<Widget> callback) {
      if (historyTokens.size() == 2) {
        final String aipId = historyTokens.get(0);
        final String descriptiveMetadataId = historyTokens.get(1);

        BrowserService.Util.getInstance().getDescriptiveMetadataEditBundle(aipId, descriptiveMetadataId,
          new AsyncCallback<DescriptiveMetadataEditBundle>() {

          @Override
          public void onFailure(Throwable caught) {
            callback.onFailure(caught);
          }

          @Override
          public void onSuccess(DescriptiveMetadataEditBundle bundle) {
            EditDescriptiveMetadata edit = new EditDescriptiveMetadata(aipId, bundle);
            callback.onSuccess(edit);
          }
        });
      } else {
        Tools.newHistory(Browse.RESOLVER);
        callback.onSuccess(null);
      }
    }

    @Override
    public void isCurrentUserPermitted(AsyncCallback<Boolean> callback) {
      // TODO check for edit metadata permission
      UserLogin.getInstance().checkRoles(new HistoryResolver[] {Browse.RESOLVER}, false, callback);
    }

    public List<String> getHistoryPath() {
      return Tools.concat(Browse.RESOLVER.getHistoryPath(), getHistoryToken());
    }

    public String getHistoryToken() {
      return "edit_metadata";
    }
  };

  interface MyUiBinder extends UiBinder<Widget, EditDescriptiveMetadata> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  private final String aipId;
  private final DescriptiveMetadataEditBundle bundle;

  // private ClientLogger logger = new ClientLogger(getClass().getName());
  private static final BrowseMessages messages = GWT.create(BrowseMessages.class);

  @UiField
  TextBox id;

  @UiField
  ListBox type;

  @UiField
  TextArea xml;

  @UiField
  Button buttonApply;

  @UiField
  Button buttonRemove;

  @UiField
  Button buttonCancel;

  @UiField
  HTML errors;

  /**
   * Create a new panel to edit a user
   * 
   * @param user
   *          the user to edit
   */
  public EditDescriptiveMetadata(final String aipId, final DescriptiveMetadataEditBundle bundle) {
    this.aipId = aipId;
    this.bundle = bundle;

    initWidget(uiBinder.createAndBindUi(this));

    id.setText(bundle.getId());
    xml.setText(bundle.getXml());

    id.setEnabled(false);

    BrowserService.Util.getInstance().getSupportedMetadata(LocaleInfo.getCurrentLocale().getLocaleName(),
      new AsyncCallback<Map<String, String>>() {

        @Override
        public void onFailure(Throwable caught) {
          Toast.showError(caught.getClass().getName(), caught.getMessage());
        }

        @Override
        public void onSuccess(Map<String, String> metadataTypes) {
          // TODO sort by alphabetic order of value
          int selected = -1;
          int index = 0;
          for (Map.Entry<String, String> entry : metadataTypes.entrySet()) {
            type.addItem(entry.getValue(), entry.getKey());
            if (entry.getKey().equals(bundle.getType().toLowerCase())) {
              selected = index;
            }

            index++;
          }

          if (selected >= 0) {
            type.addItem("Other", "");
            type.setSelectedIndex(selected);
          } else if ("".equals(bundle.getType())) {
            type.addItem("Other", "");
            type.setSelectedIndex(type.getItemCount() - 1);
          } else {
            type.addItem("Other (" + bundle.getType() + ")", bundle.getType());
            type.addItem("Other", "");
            type.setSelectedIndex(type.getItemCount() - 2);
          }
        }
      });

  }

  @UiHandler("buttonApply")
  void buttonApplyHandler(ClickEvent e) {
    String typeText = type.getSelectedValue();
    String xmlText = xml.getText();

    DescriptiveMetadataEditBundle updatedBundle = new DescriptiveMetadataEditBundle(bundle.getId(), typeText, xmlText);

    BrowserService.Util.getInstance().updateDescriptiveMetadataFile(aipId, updatedBundle, new AsyncCallback<Void>() {

      @Override
      public void onFailure(Throwable caught) {
        if (caught instanceof MetadataParseException) {
          MetadataParseException e = (MetadataParseException) caught;
          updateErrors(e);
        } else {
          // TODO show error
          Toast.showError(caught.getMessage());
        }
      }

      @Override
      public void onSuccess(Void result) {
        errors.setText("");
        errors.setVisible(false);
        Toast.showInfo("Success", "Saved descriptive metadata file");
        Tools.newHistory(Browse.RESOLVER, aipId);
      }
    });

  }

  protected void updateErrors(MetadataParseException e) {
    SafeHtmlBuilder b = new SafeHtmlBuilder();
    for (ParseError error : e.getErrors()) {
      b.append(SafeHtmlUtils.fromSafeConstant("<span class='error'>"));
      b.append(messages.metadataParseError(error.getLineNumber(), error.getColumnNumber(), error.getMessage()));
      b.append(SafeHtmlUtils.fromSafeConstant("</span>"));
    }

    errors.setHTML(b.toSafeHtml());
    errors.setVisible(true);
  }

  @UiHandler("buttonRemove")
  void buttonRemoveHandler(ClickEvent e) {
    BrowserService.Util.getInstance().removeDescriptiveMetadataFile(aipId, bundle.getId(), new AsyncCallback<Void>() {

      @Override
      public void onFailure(Throwable caught) {
        // TODO show error
        Toast.showError(caught.getMessage());
      }

      @Override
      public void onSuccess(Void result) {
        Toast.showInfo("Success", "Removed descriptive metadata file");
        Tools.newHistory(Browse.RESOLVER, aipId);
      }
    });
  }

  @UiHandler("buttonCancel")
  void buttonCancelHandler(ClickEvent e) {
    cancel();
  }

  private void cancel() {
    Tools.newHistory(Browse.RESOLVER, aipId);
  }

}
