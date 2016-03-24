package com;

import com.model.UserPreferences;
import com.view.GameView;

public class Main {

	public static void main(String[] args) {
		// Загружаем пользовательские настройки
		UserPreferences userPrefs = new UserPreferences();
		
		// Создаём окно программы на основе загруженных настроек
		new GameView(userPrefs);
	}

}
