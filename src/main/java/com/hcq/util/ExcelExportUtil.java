package com.hcq.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class ExcelExportUtil {

	private static final String FILEPATH = "F:/yu_text/upload" + File.separator ;
	
	public static String getTitle(){
		Date date = new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");  
	     String title=dateFormat.format(date)+"_统计数据.xls";  
	     return title;
	}
	
	
	public static File saveFile(Map<String, String> headData, String sql, File file) {
		// ����������
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		// sheet:һ�ű�ļ��
		// row:�������
		// �����������еĹ�����
		HSSFSheet hssfSheet = hssfWorkbook.createSheet();
		// ������
		HSSFRow row = hssfSheet.createRow(0);
		// ������Ԫ�����ñ�ͷ ������
		HSSFCell cell = null;
		// ��ʼ������
		int rowIndex = 0;
		int cellIndex = 0;

		// ����������
		row = hssfSheet.createRow(rowIndex);
		rowIndex++;
		// ��������
		for (String h : headData.keySet()) {
			//������
			cell = row.createCell(cellIndex);
			//��������
			cellIndex++;
			//���в������
			cell.setCellValue(headData.get(h));
		}

		// �õ����м�¼ �У���
		List<Record> list = Db.find(sql);
		Record record = null;

		if (list != null) {
			// ��ȡ���еļ�¼ �ж�������¼�ʹ���������
			for (int i = 0; i < list.size(); i++) {
				row = hssfSheet.createRow(rowIndex);
				// �õ����е��� һ��record�ʹ��� һ��
				record = list.get(i);
				//��һ������
				rowIndex++;
				//ˢ����������
				cellIndex = 0;
				// �������еļ�¼����֮�ϣ�������������ı�ͷ,�ٴ���N��
				for (String h : headData.keySet()) {
					cell = row.createCell(cellIndex);
					cellIndex++;
					//����ÿ����¼ƥ������
					cell.setCellValue(record.get(h) == null ? "" : record.get(h).toString());
				}
			}
		}
		try {
			FileOutputStream fileOutputStreane = new FileOutputStream(file);
			hssfWorkbook.write(fileOutputStreane);
			fileOutputStreane.flush();
			fileOutputStreane.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}