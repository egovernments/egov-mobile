/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.model;

import org.egov.android.model.BaseModel;
import org.egov.android.model.IModel;

/**
 * This ComplaintType Model class contains the complaint type information.
 *
 */

public class ComplaintType extends BaseModel implements IModel {

    private int id = 0;
    private String name = "";
    private String description = "";
    private int image = 0;
    private String imagePath = "";

    /**
     * returns the ComplainType Id
     * 
     * @return
     */

    public int getId() {
        return id;
    }

    /**
     * sets the ComplainType Id
     */

    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns the ComplainType name
     * 
     * @return
     */

    public String getName() {
        return name;
    }

    /**
     * sets the ComplainType name
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the ComplainType description
     * 
     * @return
     */

    public String getDescription() {
        return description;
    }

    /**
     * sets the ComplainType description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * returns the ComplainType image
     * 
     * @return
     */

    public int getImage() {
        return image;
    }

    /**
     * sets the ComplainType image
     */

    public void setImage(int image) {
        this.image = image;
    }

    /**
     * returns the ComplainType image path
     * 
     * @return
     */

    public String getImagePath() {
        return imagePath;
    }

    /**
     * sets the ComplainType image path
     */

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
