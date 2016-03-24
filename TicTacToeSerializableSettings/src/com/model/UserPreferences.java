package com.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserPreferences implements IPreferences, Serializable {

	private static final long serialVersionUID = 2468350646925673709L;

	private static final String FILE_PATH = "src/res/AppPreferences.ser";
	
	private String gameVersion;
	private String gameSkin;

	public UserPreferences() {
		
		/*
		 * ������ ������ �� ���� � �����������
		 */
		
		File file = new File(FILE_PATH);
		
		/*
		 * ��������� ������� ����� �� ������
		 * � ���� ��������� ��������� �� ����,
		 * ���� ������ ����� � ����������� �� ���������
		 */

		if (file.exists()) {
			readPrefsFromFile();
		} else {
			setDefaultPrefs();
			
			try {
				file.createNewFile();
				writePrefsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writePrefsToFile() {
		
		/*
		 * ��������� ����� �� ������ � ����
		 * � ����������� ������ � �������� ����������
		 */
		
		try (FileOutputStream file = new FileOutputStream(FILE_PATH);
		ObjectOutputStream object = new ObjectOutputStream(file)) {
			object.writeObject(this);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public String getGameSkin() {
		return gameSkin;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public void setGameSkin(String gameSkin) {
		this.gameSkin = gameSkin;
	}

	private void setDefaultPrefs() {
		this.gameVersion = IPreferences.gameVersion[0];
		this.gameSkin = IPreferences.gameSkin[0];
	}

	private void readPrefsFromFile() {
		
		/*
		 * ��������� ����� �� ������ �� �����,
		 * ������������� ������ � ����� ����������
		 * � �������� �������� � ���� �������� �������
		 */
		
		try (FileInputStream file = new FileInputStream(FILE_PATH);
		ObjectInputStream object = new ObjectInputStream(file)) {
			UserPreferences tempUP = (UserPreferences) object.readObject();
			
			this.gameVersion = tempUP.gameVersion;
			this.gameSkin = tempUP.gameSkin;
		} catch (ClassNotFoundException cnf) {
			cnf.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
	}

}
