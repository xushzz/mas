package com.sirap.basic.util;

import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings({ "rawtypes"})
public class MatrixUtil {
	
	/***
	 * A B C D E
	 * 1 2 3 4 5
	 * K M J Y F
	 * @return
	 */
	public static List<List> rotate(List<List> matrix) {
		int cols = 0;
		
		if(!matrix.isEmpty()) {
			cols = matrix.get(0).size();
		}
		
		List<List> newMatrix = Lists.newArrayList();
		
		for(int k = 0; k < cols; k++) {
			List<Object> newRow = Lists.newArrayList();
			int fk = k;
			matrix.stream().forEach(row -> newRow.add(row.get(fk)));
			newMatrix.add(newRow);
		}
		
		return newMatrix;
	}
	
	public static <T extends Object> int[] maxlenOfEachColumn(List<List> matrix) {
		int cols = 0;
		
		if(!matrix.isEmpty()) {
			cols = matrix.get(0).size();
		}
		
		int[] maxLens = new int[cols];
		
		for(int k = 0; k < cols; k++) {
			int max = 0;
			for(List row : matrix) {
				int len = StrUtil.countOfAscii(row.get(k) + "");
				if(len > max) {
					max = len;
				}
			}
			maxLens[k] = max;
		}
		
		return maxLens;
	}
	
	public static List<List<String>> prettyMatrix(List<List> matrix) {
		int cols = 0;
		
		if(!matrix.isEmpty()) {
			cols = matrix.get(0).size();
		}
		
		int[] maxLens = maxlenOfEachColumn(matrix);
		
		List<List<String>> newMatrix = Lists.newArrayList();
		
		for(List row : matrix) {
			List<String> newRow = Lists.newArrayList();
			for(int k = 0; k < cols; k++) {
				String item = StrUtil.padRightAscii(row.get(k) + "", maxLens[k]);
				newRow.add(item);
			}
			newMatrix.add(newRow);
		}
		
		return newMatrix;
	}
	
	public static List<String> prettyMatrixLines(List<List> matrix, String connector) {
		int cols = 0;
		
		if(!matrix.isEmpty()) {
			cols = matrix.get(0).size();
		}
		
		int[] maxLens = maxlenOfEachColumn(matrix);
		
		List<String> newMatrix = Lists.newArrayList();
		
		for(List row : matrix) {
			List<String> newRow = Lists.newArrayList();
			for(int k = 0; k < cols; k++) {
				String item = StrUtil.padRightAscii(row.get(k) + "", maxLens[k]);
				newRow.add(item);
			}
			newMatrix.add(StrUtil.connect(newRow, connector));
		}
		
		return newMatrix;
	}
	
	public static List<String> lines(List<List> matrix, String connector) {
		
		List<String> newMatrix = Lists.newArrayList();

		for(List newRow : matrix) {
			newMatrix.add(StrUtil.connect(newRow, connector));
		}
		
		return newMatrix;
	}

	/***
	 * 1) each row has same size of elements
	 * 2) each row has different size of elements
	 * @param matrix
	 * @return
	 */
	public static List<List> rotateUnevenMatrixWithEmptyString(List<List> matrix) {
		return rotateUnevenMatrix(matrix, "");
	}
	
	public static List<List> rotateUnevenMatrix(List<List> matrix, Object whatToFill) {
		XXXUtil.nullCheck(matrix, "matrix");
		List<List> allRecords = Lists.newArrayList();
		
		int k = -1;
		while(true) {
			k++;
			List<Object> newRow = Lists.newArrayList();
			boolean solid = false;
			for(List items : matrix) {
//				D.pl(k, items, items.size());
				if(k < items.size()) {
					solid = true;
					newRow.add(items.get(k));
				} else {
					newRow.add(whatToFill);
				}
			}
			
			if(!solid) {
				break;
			}
			allRecords.add(newRow);
		}
		
		return allRecords;
	}
	
	public static <T extends Object> List<List> checkMatrix(List<T> matrix) {
		XXXUtil.nullCheck(matrix, "matrix");
		
		List lastRow = null;
		int k = -1;
		List<List> goodMatrix = Lists.newArrayList();
		for(T row : matrix) {
			k++;
			List items = Colls.listOf(row);
			
			if(lastRow == null) {
				lastRow = items;
			} else if(lastRow.size() != items.size()) {
				XXXUtil.alert("Uneven matrix between row {0} and {1}, \nrow {0}: {2} \nrow {1}: {3}", k, k - 1, lastRow, items);
			}
			
			goodMatrix.add(items);
		}
		
		return goodMatrix;
	}
	

}
