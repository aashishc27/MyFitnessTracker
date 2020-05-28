package models;

import java.io.Serializable;

public class ApplicationStatusModel implements Serializable {


    public boolean isEligibilityCHecked, isDocumentUploaded, isAddressUpdated, isKYCUploaded;
    public int dropState, bankTab, docTab;

    public ApplicationStatusModel() {

    }

    public ApplicationStatusModel(boolean isEligibilityChecked, boolean isDocumentUploaded, boolean isAddressUpdated, boolean isKYCUploaded, int dropState, int bankTab, int docTab) {

        this.isEligibilityCHecked = isEligibilityChecked;
        this.isDocumentUploaded = isDocumentUploaded;
        this.isAddressUpdated = isAddressUpdated;
        this.dropState = dropState;
        this.isKYCUploaded = isKYCUploaded;
        this.bankTab = bankTab;
        this.docTab = docTab;
    }

}
