package org.egov.android.model;

import java.util.List;

public class ComplaintListByType {

    private String name = null;
    private String image = null;
    private int groupCount = 0;
    private List<Complaint> complaintList = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }

    public void setComplaintList(List<Complaint> complaintList) {
        this.complaintList = complaintList;
        this.groupCount = complaintList.size();
    }

}
