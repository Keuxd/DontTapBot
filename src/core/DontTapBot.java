package core;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class DontTapBot {
	protected static Point mousePoint;
	private static Robot robo;
	protected static JLabel coordsMessage;
	
	public static void main(String[] args) throws AWTException {
		registerGlobalScreen();
		robo = new Robot();
		
		//choose option
		Object[] options = {"Aimbot", "Auto"};
		int option = JOptionPane.showOptionDialog(new JFrame(),
				"In order to stop the application, press F2.",
				"Choose a function",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				null);
		
		switch(option) {
			case -1: {
				java.awt.Toolkit.getDefaultToolkit().beep();
				break;
			}
			case 0: {
				aimbot();
				break;
			}
			case 1: {
				//first square
				coordsMessage = new JLabel("<html>Press T to mark the center of the first square.<br>Then press 'OK'</html>");
				JOptionPane.showMessageDialog(new JFrame(), coordsMessage);
				
				Point firstSquare = mousePoint;
				
				//second square
				coordsMessage = new JLabel("<html>Press T to mark the center of the second square.<br>Then press 'OK'</html>");
				JOptionPane.showMessageDialog(new JFrame(), coordsMessage);
				
				auto(firstSquare, (int)(mousePoint.getX() - firstSquare.getX()));
				break;
			}
		}
		System.exit(1);
	} 
	
	private static void registerGlobalScreen() {
		//turning off logs
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
		logger.setUseParentHandlers(false);
		
		//register
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(1);
		}
		GlobalScreen.addNativeKeyListener(new KeysUsing());
	}
	
	private static void aimbot() {
		coordsMessage = null;
		
		while(true) {
			mousePoint = MouseInfo.getPointerInfo().getLocation();
			
			if(robo.getPixelColor((int)mousePoint.getX(), (int)mousePoint.getY()).equals(Color.BLACK)) {
				robo.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robo.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				robo.delay(75);
			}
		}
	}
	
	private static void auto(Point firstSquare,int distance) {
		mousePoint = null;
		coordsMessage = null;
		
		int x = (int)firstSquare.getX();
		int y = (int)firstSquare.getY();
		
		while(true) {
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					int newX = x + (distance*j);
					int newY = y + (distance*i);
					
					if(robo.getPixelColor(newX, newY).equals(Color.BLACK)) {
						robo.mouseMove(newX, newY);
						robo.mousePress(InputEvent.BUTTON1_DOWN_MASK);
						robo.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
						robo.delay(75);
					}
				}
			}
		}
	}
}

class KeysUsing implements NativeKeyListener {
	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		//turns off application
		if(arg0.getKeyCode() == NativeKeyEvent.VC_F2) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			System.exit(1);
		}
		//saves the mouse pointer location in mousePoint
		if(arg0.getKeyCode() == NativeKeyEvent.VC_T) {
			DontTapBot.mousePoint = MouseInfo.getPointerInfo().getLocation();
			try {
				DontTapBot.coordsMessage.setText(String.format("X >> [%.0f] | Y >> [%.0f]", DontTapBot.mousePoint.getX(), DontTapBot.mousePoint.getY()));
			} catch(Exception e) {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {}
}