package com;

import com.model.UserPreferences;
import com.view.GameView;

public class Main {

	public static void main(String[] args) {
		// ��������� ���������������� ���������
		UserPreferences userPrefs = new UserPreferences();
		
		// ������ ���� ��������� �� ������ ����������� ��������
		new GameView(userPrefs);
	}

}
