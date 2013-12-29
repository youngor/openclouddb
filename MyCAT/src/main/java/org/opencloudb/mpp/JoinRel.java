package org.opencloudb.mpp;

/**
 * join relation
 * 
 * @author wuzhih
 * 
 */
public class JoinRel {
	public JoinRel(String leftTable, String columnNameA, String rightTable,
			String columnNameB) {
		tableA=leftTable;
		columnA=columnNameA.toUpperCase();
		tableB=rightTable;
		columnB=columnNameB.toUpperCase();
		joinSQLExp=tableA+'.'+columnA+'='+tableB+'.'+columnB;
	}
	
	public final String joinSQLExp;
	public final String tableA;
	public final String columnA;
	public final String tableB;
	public final String columnB;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnA == null) ? 0 : columnA.hashCode());
		result = prime * result + ((columnB == null) ? 0 : columnB.hashCode());
		result = prime * result + ((tableA == null) ? 0 : tableA.hashCode());
		result = prime * result + ((tableB == null) ? 0 : tableB.hashCode());
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
		JoinRel other = (JoinRel) obj;
		if (columnA == null) {
			if (other.columnA != null)
				return false;
		} else if (!columnA.equals(other.columnA))
			return false;
		if (columnB == null) {
			if (other.columnB != null)
				return false;
		} else if (!columnB.equals(other.columnB))
			return false;
		if (tableA == null) {
			if (other.tableA != null)
				return false;
		} else if (!tableA.equals(other.tableA))
			return false;
		if (tableB == null) {
			if (other.tableB != null)
				return false;
		} else if (!tableB.equals(other.tableB))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JoinRel [tableA=" + tableA + ", columnA=" + columnA
				+ ", tableB=" + tableB + ", columnB=" + columnB + "]";
	}
	
	
}
