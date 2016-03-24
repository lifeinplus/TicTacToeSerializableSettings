package com.view;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.model.IPreferences;
import com.model.UserPreferences;

public class PreferencesView extends JPanel implements IPreferences {

	private JLabel labelGameVersion;
	private JLabel labelGameSkin;
	private JComboBox gameVersionCombo;
	private JComboBox gameSkinCombo;
	
	public PreferencesView() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		/*
		 * ������ ���������� ������
		 */
		
		labelGameVersion = new JLabel("Version:");
		labelGameSkin = new JLabel("Skin:");
		gameVersionCombo = new JComboBox(IPreferences.gameVersion);
		gameSkinCombo = new JComboBox(IPreferences.gameSkin);
		
		/*
		 * ������ �� ���������� ������� ��������
		 * � ������������ � ������������ �����������
		 */
		
		final UserPreferences userPrefs = new UserPreferences();
		gameVersionCombo.setSelectedItem(userPrefs.getGameVersion());
		gameSkinCombo.setSelectedItem(userPrefs.getGameSkin());
		
		/*
		 * ������ �������������� ����������
		 * � ��������� �� ������� ����
		 * ��� �������� �����
		 */
		
		Box boxVersion = Box.createHorizontalBox();
		boxVersion.add(labelGameVersion);
		boxVersion.add(Box.createHorizontalGlue());
		
		Box boxSkin = Box.createHorizontalBox();
		boxSkin.add(labelGameSkin);
		boxSkin.add(Box.createHorizontalGlue());

		/*
		 * ��������� ���������� �� ������, �������� ��
		 * ���������� ���������� �������������� �������
		 */
		
		this.add(boxVersion);
		this.add(Box.createRigidArea(new Dimension(0, 10)));
		this.add(gameVersionCombo);
		this.add(Box.createRigidArea(new Dimension(0, 25)));
		this.add(boxSkin);
		this.add(Box.createRigidArea(new Dimension(0, 10)));
		this.add(gameSkinCombo);
		this.add(Box.createRigidArea(new Dimension(0, 20)));
	}

	public String getGameVersion() {
		return gameVersionCombo.getSelectedItem().toString();
	}

	public String getGameSkin() {
		return gameSkinCombo.getSelectedItem().toString();
	}
	
}
