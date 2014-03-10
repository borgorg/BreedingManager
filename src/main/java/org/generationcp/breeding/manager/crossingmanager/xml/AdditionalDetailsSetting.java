package org.generationcp.breeding.manager.crossingmanager.xml;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class AdditionalDetailsSetting implements Serializable {

	private static final long serialVersionUID = -5167579512310794528L;
	
	private Integer harvestLocationId;
	private Date harvestDate;

	public AdditionalDetailsSetting(){
		
	}
	
	public AdditionalDetailsSetting(Integer harvestLocationId, Date harvestDate) {
		super();
		this.harvestLocationId = harvestLocationId;
		this.harvestDate = harvestDate;
	}

	@XmlAttribute
	public Integer getHarvestLocationId() {
		return harvestLocationId;
	}

	public void setHarvestLocationId(Integer harvestLocationId) {
		this.harvestLocationId = harvestLocationId;
	}

	@XmlAttribute
	public Date getHarvestDate() {
		return harvestDate;
	}

	public void setHarvestDate(Date harvestDate) {
		this.harvestDate = harvestDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdditionalDetailsSetting)) {
            return false;
        }

        AdditionalDetailsSetting rhs = (AdditionalDetailsSetting) obj;
        return new EqualsBuilder()
        		.append(harvestLocationId, rhs.harvestLocationId)
        		.append(harvestDate, rhs.harvestDate)
        		.isEquals();
    }

	@Override
	public String toString() {
		return "AdditionalDetailsSetting [harvestLocationId="
				+ harvestLocationId + ", harvestDate=" + harvestDate + "]";
	}
	
}