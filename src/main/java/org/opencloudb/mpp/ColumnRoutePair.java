package org.opencloudb.mpp;

/**
 * column ->node index
 * 
 * @author wuzhih
 * 
 */
public class ColumnRoutePair {
	public final String colValue;
	public Integer nodeId;

	public ColumnRoutePair(String colValue) {
		super();
		this.colValue = colValue;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((colValue == null) ? 0 : colValue.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnRoutePair other = (ColumnRoutePair) obj;
		if (colValue == null) {
			if (other.colValue != null)
				return false;
		} else if (!colValue.equals(other.colValue))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ColumnRoutePair [colValue=" + colValue + ", nodeId=" + nodeId
				+ "]";
	}

}
