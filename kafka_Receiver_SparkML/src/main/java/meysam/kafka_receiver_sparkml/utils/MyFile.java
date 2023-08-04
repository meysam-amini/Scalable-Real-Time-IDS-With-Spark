package meysam.kafka_receiver_sparkml.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MyFile {
	private static String FileAdress;

	public static void SetFileAdress(String s) throws IOException {
		FileAdress = s;
		File f1 = new File(FileAdress);
		if (!(f1.exists()))
			f1.createNewFile();

	}

	public static void add(String m) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s = "";
		while (infile.hasNextLine() == true)
			s += infile.nextLine() + "\r\n";
		s += m;
		PrintWriter outfile = new PrintWriter(f1);
		outfile.print(s);
		outfile.close();
	}

	public void Insert(int row, String g) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 0;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";

		String s2[] = new String[cs1 + 2], s = "";
		int i = 0;
		row--;
		for (i = 0; i < row; i++)
			s2[i] = "" + s1[i];
		s2[i] = "" + g;
		for (int j = i; j < cs1 + 1; j++)
			s2[j + 1] = "" + s1[j];
		for (i = 0; i < cs1; i++)
			s1[i] = "";
		for (i = 0; i < cs1 + 1; i++)
			s1[i] += s2[i];
		for (i = 0; i < cs1; i++)
			s += s1[i] + "\r\n";
		PrintWriter outfile = new PrintWriter(f1);
		outfile.print(s);
		outfile.close();

	}

	public int getLines() throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s = "";
		int cs1 = 0;
		while (infile.hasNextLine() == true) {
			s += infile.nextLine() + "\r\n";
			cs1++;
		}
		return cs1;
	}

	public String getInfo(int row) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 0;
		row--;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";
		String r = "";
		for (int i = 0; i < s1[row].length() - 2; i++)
			r += s1[row].charAt(i);

		return r;

	}

	public String getInfo() throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s = "";
		while (infile.hasNextLine() == true)
			s += infile.nextLine() + "\r\n";
		return s;
	}

	public int search(String g) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 1;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";
		for (int i = 1; i < cs1; i++)
			if (indexof(s1[i], g) != -1)
				return i;

		return -1;

	}

	public int[] search(int start, String g) throws FileNotFoundException {
		int a[] = new int[1000000], ca = 0;
		start--;
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 1;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";
		for (int i = start; i < cs1; i++)
			if (indexof(s1[i], g) != -1)
				a[ca++] = i;

		return a;
	}

	public void delete(int row) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 0;
		;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";
		String s2[] = new String[cs1], s = "";
		int cs2 = 0;
		
			
		for (int i = 0; i < cs1; i++) {
			if ((indexof(s1[i], getInfo(row))==-1)) {
				s2[cs2++] = "" + s1[i];
			}

		}

		for (int i = 0; i < cs2; i++)
			s += s2[i];
		PrintWriter outfile = new PrintWriter(f1);
		outfile.print(s);
		outfile.close();

	}

	public void apdate(int row, String g) throws FileNotFoundException {
		File f1 = new File(FileAdress);
		Scanner infile = new Scanner(f1);
		String s1[] = new String[100000];
		int cs1 = 0;
		while (infile.hasNextLine() == true)
			s1[cs1++] = infile.nextLine() + "\r\n";
		String s2[] = new String[cs1 + 1], s = "";
		int i = 0;
		row--;
		for (i = 0; i < row; i++)
			s2[i] = "" + s1[i];
		s2[i] = "" + g;
		for (int j = i + 1; j < cs1; j++)
			s2[j] = "" + s1[j];
		for (i = 0; i < cs1; i++)
			s1[i] = "";
		for (i = 0; i < cs1; i++)
			s1[i] += s2[i];
		for (i = 0; i < cs1; i++)
			s += s1[i] + "\r\n";
		PrintWriter outfile = new PrintWriter(f1);
		outfile.print(s);
		outfile.close();

	}

	public void clear() throws FileNotFoundException {
		PrintWriter outfile = new PrintWriter(FileAdress);
	}

	public int indexof(String s1, String s2) {
		char c = s2.charAt(0);
		int x = 0;
		while (x < (s1.length() - s2.length() + 1)) {
			int i;
			if (s1.charAt(x) == c) {
				for (i = 0; i < s2.length(); i++)
					if (s1.charAt(i + x) != s2.charAt(i))
						break;
				if (i == s2.length())
					return x + 1;
				else
					x++;
			} else
				x++;
		}
		return -1;
	}

	public int toint(String a) {
		int x = 0;
		for (int i = 0; i < a.length(); i++)
			x = x * 10 + (a.charAt(i) - '0');
		return x;
	}
}
