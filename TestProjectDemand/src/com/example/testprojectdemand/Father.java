package com.example.testprojectdemand;

import java.util.ArrayList;

public class Father {
	public int id;
	public String name;
	public ArrayList<Son> sons;

	public static class Son {
		public String name;
	}
}