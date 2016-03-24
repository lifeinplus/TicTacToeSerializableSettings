package com.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import com.model.UserPreferences;
import com.view.GameView;
import com.view.PreferencesView;

public class GameEngine extends MouseAdapter implements ActionListener, MouseListener {
	
	public static final String ONE_PLAYER = "1 player";
	public static final String TWO_PLYAERS = "2 players";
	public static final String EASY = "Easy";
	public static final String HARD = "Hard";
	public static final String PLAY_AGAIN = "Play Again";
	public static final String PREFERENCES = "Preferences";
	public static final String CELL_CLICK = "Cell";
	
	private static final Color STATUS_TEXT_COLOR = new Color(95, 109, 103);
	private static final Font STATUS_TEXT_FONT = new Font("Forte", Font.PLAIN, 28);
	
	private static final Color WINNER_TEXT_COLOR_X = new Color(192, 60, 21);
	private static final Color WINNER_TEXT_COLOR_O = new Color(82, 87, 137);
	private static final Font END_GAME_FONT = new Font("Forte", Font.PLAIN, 36);
	
	private static final Color CELL_COLOR = Color.WHITE;
	private static final Color WINNER_CELLS_COLOR = new Color(248, 212, 110);
	
	private GameView parent;
	private GameCell[][] cells;

	public GameEngine(final GameView parent, final GameCell[][] cells) {
		this.parent = parent;
		this.cells = cells;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Определяем значение ActionCommand в событии
		switch (e.getActionCommand()) {
		
		/*
		 * Если был установлен переключатель "1 Player",
		 * активируем переключатели уровней сложности
		 * и поднимаем флаг одиночной игры
		 */
		
		case ONE_PLAYER:
			parent.setLevelsActivity(true);
			parent.setOnePlayerMode(true);
			break;
			
		/*
		 * Если был установлен переключатель "2 Players",
		 * деактивируем переключатели уровней сложности
		 * и опускаем флаг одиночной игры
		 */
			
		case TWO_PLYAERS:
			parent.setLevelsActivity(false);
			parent.setOnePlayerMode(false);
			break;
			
		case EASY:
			parent.setGameLevel(EASY);
			break;
			
		case HARD:
			parent.setGameLevel(HARD);
			break;
			
		case PLAY_AGAIN:
			resetGame();
			break;
			
		case PREFERENCES:
			showPreferences();
			break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		/*
		 * Приводим нажатый компонент к типу GameCell
		 * и выполняем операции по клику на ячейку
		 */
		
		GameCell currentCell = (GameCell) e.getComponent();
		
		clickOnCell(currentCell);
	}

	private void clickOnCell(GameCell currentCell) {

		/*
		 * Если во время активной игры
		 * была нажата пустая ячейка,
		 * увеличиваем счётчик ходов на единицу
		 */
			
		if (!parent.isGameOver()) {
			if (currentCell.getCellValue() == "") {
				parent.setTurn(parent.getTurn() + 1);
				
				/*
				 * Устанавливаем иконку в нажатую ячейку
				 */
				
				String turnType = setCellsIcon(currentCell);
				
				/*
				 * Проверяем наличие победителя
				 */
				
				checkForWinner();

				/*
				 * Для хода компьютера игра должна быть одиночной,
				 * а ход игры не должен быть чётным или последним
				 */
				
				if (parent.isOnePlayerMode()) {
					
					int turn = parent.getTurn();
					
					if ((turn % 2 == 1) && (turn < 8)) {
						
						/*
						 * Сложность хода компьютера выбирается в зависимости
						 * от выбранного переключателя уровня сложности игры
						 */
						
						if (parent.getGameLevel().equals("Easy")) {
							easyBotClick();
						} else {
							hardBotClick(turnType);
						}
					}
				}
			}
		}
	}

	private String setCellsIcon(GameCell currentCell) {
		
		ImageIcon imageIcon;
		String currentPlayer = parent.getCurrentPlayer();
		
		/*
		 * Определив тип текущего игрока,
		 * передаём в ячейку соответствующее значение,
		 * создаём и устанавливаем в ячейку иконку
		 * и изменяем статус очерёдности хода
		 */
		
		if (currentPlayer == GameView.PLAYERX) {
			currentCell.setCellValue("X");
			
			imageIcon = createImageIcon("/res/" + parent.getImagePlayerX() + ".png");
			currentCell.setIcon(imageIcon);

			setCurrentPlayerNote(GameView.PLAYERO);
		} else if (currentPlayer == GameView.PLAYERO) {
			currentCell.setCellValue("O");
			
			imageIcon = createImageIcon("/res/" + parent.getImagePlayerO() + ".png");
			currentCell.setIcon(imageIcon);

			setCurrentPlayerNote(GameView.PLAYERX);
		}
		
		return currentCell.getCellValue();
	}

	private void showPreferences() {

		/*
		 * Создаём панель пользовательских настроек
		 * и передаём её в диалоговое окно
		 */
			
		final PreferencesView prefsView = new PreferencesView();
		
		String[] settingsArray = {"Save settings", "Cancel"};

		int i = (int) JOptionPane.showOptionDialog(parent,
				prefsView, "Settings", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, settingsArray, settingsArray[0]);

		/*
		 * Если игрок выбрал вариант "Save settings",
		 * создаём экземпляр пользовательских настроек,
		 * изменяем значения его полей на значения из диалогового окна,
		 * изменяем стиль игры и сохраняем настройки в файл
		 */
		
		if (i == JOptionPane.YES_OPTION) {
			final UserPreferences userPrefs = new UserPreferences();

			userPrefs.setGameVersion(prefsView.getGameVersion());
			userPrefs.setGameSkin(prefsView.getGameSkin());

			parent.setCellImage(prefsView.getGameVersion());
			parent.setGameSkin(prefsView.getGameSkin());

			userPrefs.writePrefsToFile();
		}
	}

	private void resetGame() {
		
		/*
		 * Если игрок сбрасывает игру
		 * в момент между первым и последним ходом,
		 * перед очисткой ячеек запрашиваем подтверждение
		 */
		
		int turn = parent.getTurn();
		
		if (!parent.isGameOver() && (turn > 0 && turn < 9)) {
			int reply = JOptionPane.showConfirmDialog(null,
					"Still playing. Are you sure to reset?",
					"Play Again", JOptionPane.YES_NO_OPTION);
			
			if (reply == JOptionPane.YES_OPTION) {
				resetCells();
			}
		} else {
			resetCells();
		}
	}

	public void setCurrentPlayerNote(String player) {
		
		/*
		 * Задаём размер и цвет шрифта поля статуса
		 * и передаём значение текущего игрока в GUI-класс
		 */
		
		parent.setStatusFont(STATUS_TEXT_FONT);
		parent.setStatusColor(STATUS_TEXT_COLOR);
		
		parent.setCurrentPlayer(player);

		/*
		 * В зависимости от режима игры
		 * и значения переменной текущего игрока,
		 * устанавливаем в поле статуса соответствующий текст
		 */
		
		int playerLength = parent.getImagePlayerX().length();
		
		if ((playerLength > 1) && player.endsWith("X")) {
			parent.setStatusText("Hey Cat, your turn!");
		} else if ((playerLength > 1) && player.endsWith("O")) {
			parent.setStatusText("Hey Dog, your turn!");
		} else {
			parent.setStatusText(player + ", your turn!");
		}
	}

	private void hardBotClick(String turnType) {
		
		/*
		 * При наличии у компьютера возможности победить в этом ходу
		 * отправляем событие клика мыши в ключевую ячейку
		 */

		Point botCell = new Point();
		botCell.x = -1;
		
		String nextTurnType = turnType.equals("X") ? "O" : "X";
		
		botCell = findThird(nextTurnType);
		
		if (botCell.x != -1) {
			MouseEvent event = new MouseEvent(cells[botCell.x][botCell.y],
					MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
			cells[botCell.x][botCell.y].dispatchEvent(event);
			
			return;
		}
		
		/*
		 * Если возможности победить в этом ходу нет,
		 * пытаемся помешать победить игроку,
		 * опередив в выборе ключевой ячейки
		 */
		
		Point obstacleCell = new Point();
		obstacleCell.x = -1;
		
		obstacleCell = findThird(turnType);
		
		if (obstacleCell.x != -1) {
			MouseEvent event = new MouseEvent(cells[obstacleCell.x][obstacleCell.y],
					MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
			cells[obstacleCell.x][obstacleCell.y].dispatchEvent(event);
			
			return;
		}
		
		/*
		 * Если выигрышные комбинации отсутствуют,
		 * а центральная ячейка пуста, выбираем её
		 */
		
		if (cells[1][1].getCellValue().equals("")) {
			MouseEvent event = new MouseEvent(cells[1][1],
					MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
			cells[1][1].dispatchEvent(event);
			
			return;
		}
		
		/*
		 * Если центральная ячейка уже занята,
		 * ищем свободную ячейку в углу и занимаем её
		 */
		
		if (cells[1][1].getCellValue() != "") {
			if (cells[0][0].getCellValue().equals("")) {
				MouseEvent event = new MouseEvent(cells[0][0],
						MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
				cells[0][0].dispatchEvent(event);
				
				return;
			}
			
			if (cells[0][2].getCellValue().equals("")) {
				MouseEvent event = new MouseEvent(cells[0][2],
						MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
				cells[0][2].dispatchEvent(event);
				
				return;
			}
			
			if (cells[2][2].getCellValue().equals("")) {
				MouseEvent event = new MouseEvent(cells[2][2],
						MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
				cells[2][2].dispatchEvent(event);
				
				return;
			}
			
			if (cells[2][0].getCellValue().equals("")) {
				MouseEvent event = new MouseEvent(cells[2][0],
						MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
				cells[2][0].dispatchEvent(event);
				
				return;
			}
		}
		
		// Если ни один из вариантов не сработал,
		// кликаем по случайной ячейке
		easyBotClick();
	}

	private Point findThird(String turnType) {
		String firstCellValue = "";
		String secondCellValue = "";
		String thirdCellValue = "";
		
		Point nextCellClick = new Point();
		nextCellClick.x = -1;
		
		/*
		 * Перебираем строки и столбцы
		 * в поисках подходящей ячейки
		 */
		
		for (int i = 0; i < 3; i++) {
			firstCellValue = cells[i][0].getCellValue();
			secondCellValue = cells[i][1].getCellValue();
			thirdCellValue = cells[i][2].getCellValue();
			
			if (firstCellValue.equals("")
					&& secondCellValue.equals(turnType)
					&& (secondCellValue == thirdCellValue)) {
				nextCellClick.x = i;
				nextCellClick.y = 0;
			}
			
			if (secondCellValue.equals("")
					&& firstCellValue.equals(turnType)
					&& (firstCellValue == thirdCellValue)) {
				nextCellClick.x = i;
				nextCellClick.y = 1;
			}

			if (thirdCellValue.equals("")
					&& firstCellValue.equals(turnType)
					&& (firstCellValue == secondCellValue)) {
				nextCellClick.x = i;
				nextCellClick.y = 2;
			}
			
			firstCellValue = cells[0][i].getCellValue();
			secondCellValue = cells[1][i].getCellValue();
			thirdCellValue = cells[2][i].getCellValue();
			
			if (firstCellValue.equals("")
					&& secondCellValue.equals(turnType)
					&& (secondCellValue == thirdCellValue)) {
				nextCellClick.x = 0;
				nextCellClick.y = i;
			}
			
			if (secondCellValue.equals("")
					&& firstCellValue.equals(turnType)
					&& (firstCellValue == thirdCellValue)) {
				nextCellClick.x = 1;
				nextCellClick.y = i;
			}
			
			if (thirdCellValue.equals("")
					&& firstCellValue.equals(turnType)
					&& (firstCellValue == secondCellValue)) {
				nextCellClick.x = 2;
				nextCellClick.y = i;
			}
		}
		
		/*
		 * Перебираем ячейки по диагонали
		 * от левого верхнего угла до правого нижнего
		 */
		
		firstCellValue = cells[0][0].getCellValue();
		secondCellValue = cells[1][1].getCellValue();
		thirdCellValue = cells[2][2].getCellValue();
		
		if (firstCellValue.equals("")
				&& secondCellValue.equals(turnType)
				&& (secondCellValue == thirdCellValue)) {
			nextCellClick.x = 0;
			nextCellClick.y = 0;
		}
			
		if (secondCellValue.equals("")
				&& firstCellValue.equals(turnType)
				&& (firstCellValue == thirdCellValue)) {
			nextCellClick.x = 1;
			nextCellClick.y = 1;
		}
		
		if (thirdCellValue.equals("")
				&& firstCellValue.equals(turnType)
				&& (firstCellValue == secondCellValue)) {
			nextCellClick.x = 2;
			nextCellClick.y = 2;
		}
		
		/*
		 * Перебираем ячейки по диагонали
		 * от левого нижнего угла до правого верхнего
		 */
		
		firstCellValue = cells[2][0].getCellValue();
		secondCellValue = cells[1][1].getCellValue();
		thirdCellValue = cells[0][2].getCellValue();
		
		if (firstCellValue.equals("")
				&& secondCellValue.equals(turnType)
				&& (secondCellValue == thirdCellValue)) {
			nextCellClick.x = 2;
			nextCellClick.y = 0;
		}
		
		if (secondCellValue.equals("")
				&& firstCellValue.equals(turnType)
				&& (firstCellValue == thirdCellValue)) {
			nextCellClick.x = 1;
			nextCellClick.y = 1;
		}
		
		if (thirdCellValue.equals("")
				&& firstCellValue.equals(turnType)
				&& (firstCellValue == secondCellValue)) {
			nextCellClick.x = 0;
			nextCellClick.y = 2;
		}
		
		return nextCellClick;
	}

	private void easyBotClick() {
		Random randNumber = new Random();
		int randCellX = randNumber.nextInt(3);
		int randCellY = randNumber.nextInt(3);
		
		/*
		 * Пока не попадётся пустая ячейка,
		 * генерируем новые случайные координаты
		 */
		
		while (cells[randCellX][randCellY].getCellValue() != "") {
			randCellX = randNumber.nextInt(3);
			randCellY = randNumber.nextInt(3);
		}
		
		/*
		 * Создаём экземпляр события клика мыши
		 * и отправляем его в сгенерированную ячейку
		 */
		
		MouseEvent event = new MouseEvent(cells[randCellX][randCellY],
				MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
		cells[randCellX][randCellY].dispatchEvent(event);
	}

	private void checkForWinner() {
		
		/*
		 * Если есть строка из трёх одинаковых ячеек,
		 * изменяем текст в поле статуса
		 */
		
		if (fingThreeInARow()) {
			String winnerName = "";
			
			if (parent.getCurrentPlayer() == GameView.PLAYERX) {
				winnerName = parent.getImagePlayerO();
				parent.setStatusColor(WINNER_TEXT_COLOR_O);
			} else {
				winnerName = parent.getImagePlayerX();
				parent.setStatusColor(WINNER_TEXT_COLOR_X);
			}

			if (winnerName.equals("Dog") || winnerName.equals("Cat")) {
				winnerName = "The " + winnerName;
			}

			parent.setStatusText(winnerName.concat(" won!!!"));
			parent.setStatusFont(END_GAME_FONT);
		} else if (parent.getTurn() == 9) {
			parent.setStatusText("Tie!");
			parent.setStatusFont(END_GAME_FONT);
		}
	}

	private boolean fingThreeInARow() {
		String firstCellValue = "";
		String secondCellValue = "";
		String thirdCellValue = "";
		
		Point firstCell = new Point();
		Point secondCell = new Point();
		Point thirdCell = new Point();
		
		boolean threeFound = false;
		String cellType = "";

		/*
		 * В случае обнаружения одинаковых ячеек
		 * среди строк, столбцов или диагональных линий,
		 * сохраняем их координаты и тип значения
		 */
		
		for (int i = 0; i < 3; i++) {
			firstCellValue = cells[i][0].getCellValue();
			secondCellValue = cells[i][1].getCellValue();
			thirdCellValue = cells[i][2].getCellValue();
			
			if ((firstCellValue != "")
					&& (firstCellValue == secondCellValue)
					&& (secondCellValue == thirdCellValue)) {
				firstCell.x = i;
				firstCell.y = 0;
				secondCell.x = i;
				secondCell.y = 1;
				thirdCell.x = i;
				thirdCell.y = 2;
				
				threeFound = true;
				cellType = firstCellValue;
			}
		}
		
		for (int i = 0; i < 3; i++) {
			firstCellValue = cells[0][i].getCellValue();
			secondCellValue = cells[1][i].getCellValue();
			thirdCellValue = cells[2][i].getCellValue();
			
			if ((firstCellValue != "")
					&& (firstCellValue == secondCellValue)
					&& (secondCellValue == thirdCellValue)) {
				firstCell.x = 0;
				firstCell.y = i;
				secondCell.x = 1;
				secondCell.y = i;
				thirdCell.x = 2;
				thirdCell.y = i;
				
				threeFound = true;
				cellType = firstCellValue;
			}
		}

		firstCellValue = cells[0][0].getCellValue();
		secondCellValue = cells[1][1].getCellValue();
		thirdCellValue = cells[2][2].getCellValue();
		
		if ((firstCellValue != "")
				&& (firstCellValue == secondCellValue)
				&& (secondCellValue == thirdCellValue)) {
			firstCell.x = 0;
			firstCell.y = 0;
			secondCell.x = 1;
			secondCell.y = 1;
			thirdCell.x = 2;
			thirdCell.y = 2;
			
			threeFound = true;
			cellType = firstCellValue;
		}
		
		firstCellValue = cells[0][2].getCellValue();
		secondCellValue = cells[1][1].getCellValue();
		thirdCellValue = cells[2][0].getCellValue();
		
		if ((firstCellValue != "")
				&& (firstCellValue == secondCellValue)
				&& (secondCellValue == thirdCellValue)) {
			firstCell.x = 0;
			firstCell.y = 2;
			secondCell.x = 1;
			secondCell.y = 1;
			thirdCell.x = 2;
			thirdCell.y = 0;
			
			threeFound = true;
			cellType = firstCellValue;
		}
		
		/*
		 * Если был найдена линия с одинаковыми ячейками,
		 * окрашиваем её и активируем флаг окончания игры
		 */
		
		if (threeFound) {
			paintWinnerLine(firstCell, secondCell, thirdCell, cellType);
			parent.setGameOver(true);
		}
		
		return threeFound;
	}

	private void paintWinnerLine(Point firstCell, Point secondCell, Point thirdCell, String cellType) {
		Color color = WINNER_CELLS_COLOR;
		
		cells[firstCell.x][firstCell.y].setBackground(color);
		cells[secondCell.x][secondCell.y].setBackground(color);
		cells[thirdCell.x][thirdCell.y].setBackground(color);
	}

	private ImageIcon createImageIcon(String path) {
		URL iconPath = getClass().getResource(path);

		/*
		 * При наличии изображения по адресу,
		 * создаём иконку на его основе,
		 * иначе выводим в консоль сообщение об ошибке
		 */
		
		if (iconPath != null) {
			return new ImageIcon(iconPath);
		} else {
			System.err.println("Couldn't find file: " + iconPath);
			return null;
		}
	}

	private void resetCells() {
		
		/*
		 * Обнуляем состояние каждой непустой ячейки
		 */
		
		for (GameCell[] line : cells) {
			for (GameCell cell : line) {
				if (cell.getCellValue() != "") {
					cell.setCellValue("");
					cell.setIcon(null);
					cell.setBackground(CELL_COLOR);
				}
			}
		}

		parent.repaint();

		/*
		 * Изменяем текст поля статуса,
		 * сбрасываем счётчик ходов
		 * и опускаем флаг конца игры
		 */
		
		setCurrentPlayerNote(GameView.PLAYERX);
		parent.setTurn(0);
		parent.setGameOver(false);
	}
	
}
