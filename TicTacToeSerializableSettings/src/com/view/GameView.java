package com.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.controller.GameCell;
import com.controller.GameEngine;
import com.model.UserPreferences;

public class GameView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final String PLAYERX = "Player X";
	public static final String PLAYERO = "Player O";

	private static final int BORDER = 1;
	private static final Color BORDER_COLOR = Color.ORANGE;
	
	private static final String CLASSIC_GAME = "Classic";
	private static final String BRIGHT_SKIN = "Bright";
	
	private static final Color MAIN_BRIGHT_COLOR = new Color(239, 237, 223);
	private static final Color STATUS_BRIGHT_COLOR = new Color(227, 225, 211);
	private static final Color MAIN_DARK_COLOR = new Color(183, 193, 199);
	private static final Color STATUS_DARK_COLOR = new Color(171, 181, 186);
	private static final Color CELLS_COLOR = new Color(255, 253, 255);
	
	private static final Font FONT_FORTE = new Font("Forte", Font.PLAIN, 22);
	private static final Font FONT_CALIBRI = new Font("Calibri", Font.PLAIN, 14);
	
	private String imagePlayerX;
	private String imagePlayerO;
	private String currentPlayer = PLAYERX;
	
	private Panel panelGeneral;
	private Panel panelMenu;
	private Panel panelCells;
	
	private JRadioButton radio1Player;
	private JRadioButton radio2Players;
	private JRadioButton radioEasy;
	private JRadioButton radioHard;
	
	private ButtonGroup groupPlayers = new ButtonGroup();
	private ButtonGroup groupLevels = new ButtonGroup();
	
	private JButton buttonPlayAgain;
	private JButton buttonPrefs;

	private JLabel labelStatus;

	private GameCell[][] cells = new GameCell[3][3];

	private boolean gameOver = false;
	private boolean onePlayerMode = true;
	private String gameLevel = "Easy";
	private int turn = 0;


	public GameView(final UserPreferences userPrefs) {
		super("Cats vs. Dogs");

		/*
		 * Если в настройках выбрана классическая версия игры,
		 * играем крестиками и ноликами, иначе играем кошками против собак
		 */
		
		if (userPrefs.getGameVersion().equals("Classic")) {
			imagePlayerX = "X";
			imagePlayerO = "O";
		} else {
			imagePlayerX = "Cat";
			imagePlayerO = "Dog";
		}

		/*ф
		 * Создаём панель игрового поля,
		 * задаём менеджер компоновки
		 * и желательные размеры
		 */
		
		panelCells = new Panel();
		panelCells.setLayout(new GridLayout(3, 3));
		panelCells.setPreferredSize(new Dimension(300, 300));

		/*
		 * Создаём экземпляр слушателя компонентов панели
		 * и передаём ему ссылки на себя и на массив ячеек 
		 */
		
		final GameEngine engine = new GameEngine(this, cells);
		
		/*
		 * Создаём экземпляры ячеек кастомного типа
		 * и добавляем их во многомерный массив
		 */
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				cells[i][j] = new GameCell();
				
				/*
				 * Задаём фон ячейки, снимаем фокус,
				 * назначаем слушателя и связываем с конкретным действием
				 */
				
				cells[i][j].setBackground(CELLS_COLOR);
				cells[i][j].setFocusable(false);
				cells[i][j].addMouseListener(engine);
				cells[i][j].setActionCommand(GameEngine.CELL_CLICK);
				
				/*
				 * Задаём ячейке кастомные границы
				 * и размещаем её на панеле игрового поля
				 */
				
				setCellsBorders(i, j);
				panelCells.add(cells[i][j]);
			}
		}
		
		/*
		 * Создаём экземпляр панели меню,
		 * задаём менеджер компоновки
		 * и желательные размеры
		 */
		
		panelMenu = new Panel();
		panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
		panelMenu.setPreferredSize(new Dimension(150, 300));
		
		/*
		 * Создаём экземпляры компонентов меню,
		 * попарно группируем переключатели
		 */
		
		radio1Player = new JRadioButton("1 Player", true);
		radio2Players = new JRadioButton("2 Players", false);
		radioEasy = new JRadioButton("Easy", true);
		radioHard = new JRadioButton("Hard", false);
		buttonPlayAgain = new JButton("Play Again");
		buttonPrefs = new JButton("Preferences");

		groupPlayers.add(radio1Player);
		groupPlayers.add(radio2Players);
		groupLevels.add(radioEasy);
		groupLevels.add(radioHard);
		
		/*
		 * Задаём выравнивание компонентов по центру
		 * и шрифты для переключателей
		 */

		radio1Player.setAlignmentX(Component.CENTER_ALIGNMENT);
		radio2Players.setAlignmentX(Component.CENTER_ALIGNMENT);
		radioEasy.setAlignmentX(Component.CENTER_ALIGNMENT);
		radioHard.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPlayAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPrefs.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		radio1Player.setFont(FONT_FORTE);
		radio2Players.setFont(FONT_FORTE);
		radioEasy.setFont(FONT_CALIBRI);
		radioHard.setFont(FONT_CALIBRI);
		
		/*
		 * Назначаем слушатель компонентам меню
		 * и связываем их с конкретными действиями
		 */
		
		radio1Player.addActionListener(engine);
		radio2Players.addActionListener(engine);
		radioEasy.addActionListener(engine);
		radioHard.addActionListener(engine);
		buttonPlayAgain.addActionListener(engine);
		buttonPrefs.addActionListener(engine);
		
		radio1Player.setActionCommand(GameEngine.ONE_PLAYER);
		radio2Players.setActionCommand(GameEngine.TWO_PLYAERS);
		radioEasy.setActionCommand(GameEngine.EASY);
		radioHard.setActionCommand(GameEngine.HARD);
		buttonPlayAgain.setActionCommand(GameEngine.PLAY_AGAIN);
		buttonPrefs.setActionCommand(GameEngine.PREFERENCES);
		
		/*
		 * Размещаем компоненты на панеле игрового меню
		 * с добавлением невидимых элементов фиксированного размера 
		 */
		
		panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
		panelMenu.add(radio1Player);
		panelMenu.add(radioEasy);
		panelMenu.add(radioHard);
		panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
		panelMenu.add(radio2Players);
		panelMenu.add(Box.createRigidArea(new Dimension(0, 20)));
		panelMenu.add(buttonPlayAgain);
		panelMenu.add(Box.createRigidArea(new Dimension(0, 10)));
		panelMenu.add(buttonPrefs);
		
		/*
		 * Создаём поле статуса, задаём ему выравнивание,
		 * размеры и устанавливаем пояснение об очерёдности хода
		 */

		labelStatus = new JLabel(currentPlayer);
		labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		labelStatus.setPreferredSize(new Dimension(200, 50));
		
		engine.setCurrentPlayerNote(PLAYERX);
		
		/*
		 * Создаём экземпляр основной панели, задаём менеджер компоновки
		 * и размещаем на ней игровую панель, меню и поле статуса 
		 */
		
		panelGeneral = new Panel();
		panelGeneral.setLayout(new BorderLayout());
		
		panelGeneral.add(panelCells, BorderLayout.CENTER);
		panelGeneral.add(panelMenu, BorderLayout.EAST);
		panelGeneral.add(labelStatus, BorderLayout.SOUTH);
		
		/*
		 * Задаём фон компонентов фрейма,
		 * опираясь на указанную в настройках цветовую схему
		 */
		
		setGameSkin(userPrefs.getGameSkin());
		
		/*
		 * Добавляем основную панель на фрейм,
		 * а расположение фрейма при запуске делаем
		 * чуть левее и выше центра экрана
		 */
		
		add(panelGeneral);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) screenSize.getWidth() / 4, (int) screenSize.getHeight() / 6);
		
		/*
		 * Указываем действие при нажатии на крестик,
		 * отключаем возможность изменения размеров,
		 * устанавливаем оптимальный размер
		 * и делаем фрейм видимым
		 */
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setVisible(true);
	}

	public String getImagePlayerO() {
		return imagePlayerO;
	}

	public String getImagePlayerX() {
		return imagePlayerX;
	}

	public void setCellImage(String gameVersion) {
		if (gameVersion.equals(CLASSIC_GAME)) {
			this.imagePlayerX = "X";
			this.imagePlayerO = "O";
		} else {
			this.imagePlayerX = "Cat";
			this.imagePlayerO = "Dog";
		}
	}

	public void setGameSkin(String skin) {
		Color mainColor;
		Color statusColor;
		
		if (skin.equals(BRIGHT_SKIN)) {
			mainColor = MAIN_BRIGHT_COLOR;
			statusColor = STATUS_BRIGHT_COLOR;
		} else {
			mainColor = MAIN_DARK_COLOR;
			statusColor = STATUS_DARK_COLOR;
		}
		
		this.panelGeneral.setBackground(mainColor);
		this.panelMenu.setBackground(mainColor);
		this.radio1Player.setBackground(mainColor);
		this.radio2Players.setBackground(mainColor);
		this.radioEasy.setBackground(mainColor);
		this.radioHard.setBackground(mainColor);
		this.labelStatus.setBackground(statusColor);
		this.labelStatus.setOpaque(true);
		
		this.repaint();
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public boolean isOnePlayerMode() {
		return onePlayerMode;
	}

	public void setOnePlayerMode(boolean onePlayerMode) {
		this.onePlayerMode = onePlayerMode;
	}

	public String getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(String gameLevel) {
		this.gameLevel = gameLevel;
	}

	public void setLevelsActivity(boolean isOnePlayer) {
		
		/*
		 * В зависимости от флага одиночной игры,
		 * активируем либо деактивируем переключатели
		 */
		
		if (isOnePlayer) {
			activateLevels();
		} else {
			deactivateLevels();
		}
	}

	public void setStatusFont(Font font) {
		labelStatus.setFont(font);
	}

	public void setStatusColor(Color color) {
		labelStatus.setForeground(color);
	}

	public void setStatusText(String string) {
		labelStatus.setText(string);
	}

	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public String getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public GameCell[][] getCells() {
		return cells;
	}

	private void setCellsBorders(int i, int j) {
		// Ячейки: 0,0, 0,1, 1,0 и 1,1 (границы снизу и справа)
		if ((i < 2) && (j < 2)) {
			cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, BORDER, BORDER,
					BORDER_COLOR));
		}
		
		// Ячейки: 0,2 и 1,2 (границы снизу)
		if ((i < 2) && (j == 2)) {
			cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, BORDER, 0,
					BORDER_COLOR));
		}
		
		// Ячейки: 2,0 и 2,1 (границы справа)
		if ((i == 2) && (j < 2)) {
			cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 0, BORDER,
					BORDER_COLOR));
		}
		
		// Ячейка 2,2 (оставляем без границ)
		if (i == 2 && j == 2) {
			cells[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0,
					BORDER_COLOR));
		}
	}

	private void deactivateLevels() {
		radioEasy.setEnabled(false);
		radioHard.setEnabled(false);
		
		/*
		 * Идём по переключателям группы уровней сложности,
		 * получаем ссылку на текущий элемент
		 * и задаём ему серый цвет
		 */
		
		for (Enumeration<AbstractButton> buttons = groupLevels.getElements();
				buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			button.setForeground(Color.GRAY);
		}
	}

	private void activateLevels() {
		radioEasy.setEnabled(true);
		radioHard.setEnabled(true);
		
		/*
		 * Идём по переключателям группы уровней сложности,
		 * получаем ссылку на текущий элемент
		 * и задаём ему тёмно-серый цвет
		 */
		
		for (Enumeration<AbstractButton> elements = groupLevels.getElements();
				elements.hasMoreElements();) {
			AbstractButton element = elements.nextElement();
			element.setForeground(Color.DARK_GRAY);
		}
	}

}
