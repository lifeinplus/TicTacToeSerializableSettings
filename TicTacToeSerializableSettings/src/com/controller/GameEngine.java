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
		// ���������� �������� ActionCommand � �������
		switch (e.getActionCommand()) {
		
		/*
		 * ���� ��� ���������� ������������� "1 Player",
		 * ���������� ������������� ������� ���������
		 * � ��������� ���� ��������� ����
		 */
		
		case ONE_PLAYER:
			parent.setLevelsActivity(true);
			parent.setOnePlayerMode(true);
			break;
			
		/*
		 * ���� ��� ���������� ������������� "2 Players",
		 * ������������ ������������� ������� ���������
		 * � �������� ���� ��������� ����
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
		 * �������� ������� ��������� � ���� GameCell
		 * � ��������� �������� �� ����� �� ������
		 */
		
		GameCell currentCell = (GameCell) e.getComponent();
		
		clickOnCell(currentCell);
	}

	private void clickOnCell(GameCell currentCell) {

		/*
		 * ���� �� ����� �������� ����
		 * ���� ������ ������ ������,
		 * ����������� ������� ����� �� �������
		 */
			
		if (!parent.isGameOver()) {
			if (currentCell.getCellValue() == "") {
				parent.setTurn(parent.getTurn() + 1);
				
				/*
				 * ������������� ������ � ������� ������
				 */
				
				String turnType = setCellsIcon(currentCell);
				
				/*
				 * ��������� ������� ����������
				 */
				
				checkForWinner();

				/*
				 * ��� ���� ���������� ���� ������ ���� ���������,
				 * � ��� ���� �� ������ ���� ������ ��� ���������
				 */
				
				if (parent.isOnePlayerMode()) {
					
					int turn = parent.getTurn();
					
					if ((turn % 2 == 1) && (turn < 8)) {
						
						/*
						 * ��������� ���� ���������� ���������� � �����������
						 * �� ���������� ������������� ������ ��������� ����
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
		 * ��������� ��� �������� ������,
		 * ������� � ������ ��������������� ��������,
		 * ������ � ������������� � ������ ������
		 * � �������� ������ ���������� ����
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
		 * ������ ������ ���������������� ��������
		 * � ������� � � ���������� ����
		 */
			
		final PreferencesView prefsView = new PreferencesView();
		
		String[] settingsArray = {"Save settings", "Cancel"};

		int i = (int) JOptionPane.showOptionDialog(parent,
				prefsView, "Settings", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, settingsArray, settingsArray[0]);

		/*
		 * ���� ����� ������ ������� "Save settings",
		 * ������ ��������� ���������������� ��������,
		 * �������� �������� ��� ����� �� �������� �� ����������� ����,
		 * �������� ����� ���� � ��������� ��������� � ����
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
		 * ���� ����� ���������� ����
		 * � ������ ����� ������ � ��������� �����,
		 * ����� �������� ����� ����������� �������������
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
		 * ����� ������ � ���� ������ ���� �������
		 * � ������� �������� �������� ������ � GUI-�����
		 */
		
		parent.setStatusFont(STATUS_TEXT_FONT);
		parent.setStatusColor(STATUS_TEXT_COLOR);
		
		parent.setCurrentPlayer(player);

		/*
		 * � ����������� �� ������ ����
		 * � �������� ���������� �������� ������,
		 * ������������� � ���� ������� ��������������� �����
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
		 * ��� ������� � ���������� ����������� �������� � ���� ����
		 * ���������� ������� ����� ���� � �������� ������
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
		 * ���� ����������� �������� � ���� ���� ���,
		 * �������� �������� �������� ������,
		 * �������� � ������ �������� ������
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
		 * ���� ���������� ���������� �����������,
		 * � ����������� ������ �����, �������� �
		 */
		
		if (cells[1][1].getCellValue().equals("")) {
			MouseEvent event = new MouseEvent(cells[1][1],
					MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
			cells[1][1].dispatchEvent(event);
			
			return;
		}
		
		/*
		 * ���� ����������� ������ ��� ������,
		 * ���� ��������� ������ � ���� � �������� �
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
		
		// ���� �� ���� �� ��������� �� ��������,
		// ������� �� ��������� ������
		easyBotClick();
	}

	private Point findThird(String turnType) {
		String firstCellValue = "";
		String secondCellValue = "";
		String thirdCellValue = "";
		
		Point nextCellClick = new Point();
		nextCellClick.x = -1;
		
		/*
		 * ���������� ������ � �������
		 * � ������� ���������� ������
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
		 * ���������� ������ �� ���������
		 * �� ������ �������� ���� �� ������� �������
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
		 * ���������� ������ �� ���������
		 * �� ������ ������� ���� �� ������� ��������
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
		 * ���� �� �������� ������ ������,
		 * ���������� ����� ��������� ����������
		 */
		
		while (cells[randCellX][randCellY].getCellValue() != "") {
			randCellX = randNumber.nextInt(3);
			randCellY = randNumber.nextInt(3);
		}
		
		/*
		 * ������ ��������� ������� ����� ����
		 * � ���������� ��� � ��������������� ������
		 */
		
		MouseEvent event = new MouseEvent(cells[randCellX][randCellY],
				MouseEvent.MOUSE_PRESSED, 1, 0, 1, 1, 1, false);
		cells[randCellX][randCellY].dispatchEvent(event);
	}

	private void checkForWinner() {
		
		/*
		 * ���� ���� ������ �� ��� ���������� �����,
		 * �������� ����� � ���� �������
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
		 * � ������ ����������� ���������� �����
		 * ����� �����, �������� ��� ������������ �����,
		 * ��������� �� ���������� � ��� ��������
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
		 * ���� ��� ������� ����� � ����������� ��������,
		 * ���������� � � ���������� ���� ��������� ����
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
		 * ��� ������� ����������� �� ������,
		 * ������ ������ �� ��� ������,
		 * ����� ������� � ������� ��������� �� ������
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
		 * �������� ��������� ������ �������� ������
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
		 * �������� ����� ���� �������,
		 * ���������� ������� �����
		 * � �������� ���� ����� ����
		 */
		
		setCurrentPlayerNote(GameView.PLAYERX);
		parent.setTurn(0);
		parent.setGameOver(false);
	}
	
}
