package org.opencloudb.mpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.util.ByteUtil;
import org.opencloudb.util.CompareUtil;

public class RowDataPacketSorter {

	private final OrderCol[] orderCols;

	private List<RowDataPacket> sorted = new ArrayList<RowDataPacket>();
	private RowDataPacket[] array, resultTemp;
	private int p1, pr, p2;

	public RowDataPacketSorter(OrderCol[] orderCols) {
		super();
		this.orderCols = orderCols;
	}

	public void addRow(RowDataPacket row) {
		this.sorted.add(row);

	}

	public Collection<RowDataPacket> getSortedResult() {
		try {
			this.mergeSort(sorted.toArray(new RowDataPacket[sorted.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (array != null) {
			Collections.addAll(this.sorted, array);
		}

		return sorted;
	}

	private RowDataPacket[] mergeSort(RowDataPacket[] result) throws Exception {
		this.sorted.clear();
		if (result == null || result.length < 2 || this.orderCols == null
				|| orderCols.length < 1) {
			return result;
		}

		array = result;
		mergeR(0, result.length - 1);

		return array;
	}

	private void mergeR(int startIndex, int endIndex) {
		if (startIndex < endIndex) {
			int mid = (startIndex + endIndex) / 2;

			mergeR(startIndex, mid);

			mergeR(mid + 1, endIndex);

			merge(startIndex, mid, endIndex);
		}
	}

	private void merge(int startIndex, int midIndex, int endIndex) {
		resultTemp = new RowDataPacket[(endIndex - startIndex + 1)];

		pr = 0;
		p1 = startIndex;
		p2 = midIndex + 1;
		while (p1 <= midIndex || p2 <= endIndex) {
			if (p1 == midIndex + 1) {
				while (p2 <= endIndex) {
					resultTemp[pr++] = array[p2++];

				}
			} else if (p2 == endIndex + 1) {
				while (p1 <= midIndex) {
					resultTemp[pr++] = array[p1++];
				}

			} else {
				compare(0);
			}
		}
		for (p1 = startIndex, p2 = 0; p1 <= endIndex; p1++, p2++) {
			array[p1] = resultTemp[p2];

		}
	}

	/**
	 * 递归按照排序字段进行排序
	 * 
	 * @param byColumnIndex
	 */
	private void compare(int byColumnIndex) {

		if (byColumnIndex == this.orderCols.length) {
			if (this.orderCols[byColumnIndex - 1].orderType == OrderCol.COL_ORDER_TYPE_ASC) {

				resultTemp[pr++] = array[p1++];
			} else {
				resultTemp[pr++] = array[p2++];
			}
			return;
		}

		byte[] left = array[p1].fieldValues
				.get(this.orderCols[byColumnIndex].colMeta.colIndex);
		byte[] right = array[p2].fieldValues
				.get(this.orderCols[byColumnIndex].colMeta.colIndex);

		if (compareObject(left, right, this.orderCols[byColumnIndex]) <= 0) {
			if (compareObject(left, right, this.orderCols[byColumnIndex]) < 0) {
				if (this.orderCols[byColumnIndex].orderType == OrderCol.COL_ORDER_TYPE_ASC) {// 升序
					resultTemp[pr++] = array[p1++];
				} else {
					resultTemp[pr++] = array[p2++];
				}
			} else {// 如果当前字段相等，则按照下一个字段排序
				compare(byColumnIndex + 1);

			}

		} else {
			if (this.orderCols[byColumnIndex].orderType == OrderCol.COL_ORDER_TYPE_ASC) {// 升序
				resultTemp[pr++] = array[p2++];
			} else {
				resultTemp[pr++] = array[p1++];
			}

		}
	}

	private int compareObject(Object l, Object r, OrderCol orderCol) {

		int colType = orderCol.getColMeta().getColType();
		byte[] left = (byte[]) l;
		byte[] right = (byte[]) r;
		// System.out.println("------------" + colType);
		switch (colType) {
		case ColMeta.COL_TYPE_INT:
		case ColMeta.COL_TYPE_LONG:
		case ColMeta.COL_TYPE_LONGLONG:
		case ColMeta.COL_TYPE_NEWDECIMAL:
		case ColMeta.COL_TYPE_DOUBLE:
		case ColMeta.COL_TYPE_FLOAT:
			return ByteUtil.compareNumberByte(left, right);
		case ColMeta.COL_TYPE_VAR_STRING:
			return CompareUtil.compareString(ByteUtil.getString(left),
					ByteUtil.getString(right));
		case ColMeta.COL_TYPE_BIT:
			return CompareUtil.compareChar(ByteUtil.getChar(left),
					ByteUtil.getChar(right));

		case ColMeta.COL_TYPE_DATE:
			return CompareUtil.compareString(ByteUtil.getDate(left),
					ByteUtil.getDate(right));
		case ColMeta.COL_TYPE_TIMSTAMP:
			return CompareUtil.compareString(ByteUtil.getTimestmap(left),
					ByteUtil.getTimestmap(right));
		case ColMeta.COL_TYPE_TIME:
			return CompareUtil.compareString(ByteUtil.getTimestmap(left),
					ByteUtil.getTimestmap(right));
		}

		return 0;
	}
}
