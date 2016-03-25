package com.controller;

import javax.swing.JButton;

public class GameCell extends JButton {

	private static final long serialVersionUID = 1L;
	
	private String cellValue = "";

	public String getCellValue() {
		return cellValue;
	}

	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;
	}

}
