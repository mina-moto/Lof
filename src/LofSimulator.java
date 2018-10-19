

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
/**
 * LOFのアルゴリズムに沿ってデータの局所外れ値係数を求める．距離にはL1ノルムを用いる
 * 
 * @author k.minamoto
 */
public class LofSimulator {
	/** 各データのリスト */
	private static ArrayList<Point> dataList = new ArrayList<Point>();
	/** 各データの結果のリスト */
	private static ArrayList<Double> resultDataList = new ArrayList<Double>();
	/** パラメータK */
	private static int k;
	/**
	 * 引数のファイル名を一行づつ読み込みデータとして登録
	 */
	public void inputData(String fileName) {
		String[] data = new String[2];
		try {
			// ファイルを読み込む
			File f = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {// 1行ずつCSVファイルを読み込む
				data = line.split(",", 0); // 行をカンマ区切りで配列に変換
				dataList.add(new Point(Integer.valueOf(data[0]), Integer
						.valueOf(data[1])));
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	/**
	 * 結果を指定のファイルに出力
	 */
	public void outputData(String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName, false); // 出力先を作成
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			int i = 0;
			for (double data : resultDataList) {
				i++;
				pw.print("x" + i + "の局所外れ値係数," + data);
				pw.println();
			}
			pw.close();// ファイルに書き出す
			System.out.println("出力が完了しました");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 引数dataからdataListの各データへの距離のリストを返す
	 * @param data
	 * @return
	 */
	public ArrayList<Double> getDisList(Point data) {
		ArrayList<Double> disList = new ArrayList<Double>();
		for (Point point : dataList) {
			if (!point.equals(data)) {
				disList.add(Math.abs(point.getX() - data.getX())
						+ Math.abs(point.getY() - data.getY()));
			}
		}
		return disList;
	}
	/**
	 * 引数dataのk-distanceを返す
	 */
	public double kdistance(Point data) {
		ArrayList<Double> disList = getDisList(data);// dataからdataListの他データの距離のリスト
		Collections.sort(disList);
		return disList.get(k - 1);
	}

	/**
	 * 引数のk-距離近傍以内のデータ集合を返す
	 */
	public ArrayList<Point> nk(Point data) {
		ArrayList<Point> nk = new ArrayList<Point>();
		double kdis = kdistance(data);
		for (Point point : dataList) {
			if (!point.equals(data)) {
				if (kdis >= Math.abs(point.getX() - data.getX())
						+ Math.abs(point.getY() - data.getY())) {
					nk.add(point);
				}
			}
		}
		return nk;
	}

	/**
	 * 引数のデータ間の到達可能距離を返す
	 */
	public double reactDist(Point data1, Point data2) {
		return Math.max(
				Math.abs(data1.getX() - data2.getX())
						+ Math.abs(data1.getY() - data2.getY()),
				kdistance(data2));
	}

	/**
	 * 引数のデータの局所到達可能密度を返す
	 */
	public double lrd(Point data) {
		double ret = 0, deno = 0;
		for (Point point : nk(data)) {
			deno += reactDist(data, point);
		}
		ret = nk(data).size() / deno;
		return ret;
	}

	/**
	 * 引数のデータの局所外れ値係数を返す
	 */
	public double lof(Point data) {
		double ret = 0;
		ArrayList<Point> nk = nk(data);
		for (Point point : nk) {
			ret += lrd(point);
		}
		ret /= lrd(data);
		ret /= nk.size();
		return ret;
	} 
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		LofSimulator simu = new LofSimulator();
		System.out.print("パラメータkを入力してください:");
		k = scan.nextInt();
		System.out.print("入力ファイル名を入力してください:");
		String inputFile = scan.next();
		simu.inputData(inputFile);
		for (Point data : dataList) {
			resultDataList.add(simu.lof(data));
		}
		System.out.print("出力ファイル名を入力してください:");
		String outputFile = scan.next();
		simu.outputData(outputFile);
		scan.close();
	}
}
