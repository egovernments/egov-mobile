/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by egov on 12/4/16.
 */
public class SupportDoc implements Parcelable  {

    @SerializedName("fileId")
    @Expose
    private String fileId;
    @SerializedName("fileContentType")
    @Expose
    private String fileContentType;
    @SerializedName("fileIndexId")
    @Expose
    private String fileIndexId;

    public SupportDoc(Parcel in)
    {
        this.fileId = in.readString();
        this.fileContentType = in.readString();
        this.fileIndexId = in.readString();
    }

    /**
     *
     * @return
     * The fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     *
     * @param fileId
     * The fileId
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     *
     * @return
     * The fileContentType
     */
    public String getFileContentType() {
        return fileContentType;
    }

    /**
     *
     * @param fileContentType
     * The fileContentType
     */
    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    /**
     *
     * @return
     * The fileIndexId
     */
    public String getFileIndexId() {
        return fileIndexId;
    }

    /**
     *
     * @param fileIndexId
     * The fileIndexId
     */
    public void setFileIndexId(String fileIndexId) {
        this.fileIndexId = fileIndexId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileId);
        dest.writeString(this.fileContentType);
        dest.writeString(this.fileIndexId);
    }

    public static final Parcelable.Creator<SupportDoc> CREATOR = new Parcelable.Creator<SupportDoc>() {
        public SupportDoc createFromParcel(Parcel in) {
            return new SupportDoc(in);
        }

        public SupportDoc[] newArray(int size) {
            return new SupportDoc[size];
        }
    };


}
