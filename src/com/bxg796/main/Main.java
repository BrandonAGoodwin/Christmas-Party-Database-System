package com.bxg796.main;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Object holder = new Object();
		UI clientUI = new UI("Database Manager", holder);
		while(true) {
			try {
				String input = clientUI.readLine();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
