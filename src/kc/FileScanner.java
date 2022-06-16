package kc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author 18-1-037-0107 秋山周平
 *問題番号2.7
 *提出日 2020/5/20
 *再提出 2020/6/3
 *ファイルを読み込むクラス
 *
 */
class FileScanner {

	/**
	 * 引数 sourceFileName で指定されたファイルを開き, sourceFile で参照する．
	 * 教科書 p. 210 ソースコード 10.1 ではtry-with-resources 文を用いて
	 * ファイルの 参照と読み取りを一度に行っているが，このコンストラクタでは
	 * ファイルの参照 だけを行う．
	 * また lineNumber, columnNumber, currentCharacter, nextCharacter を初期化する
	 *
	 * @param sourceFileName ソースプログラムのファイル名
	 * @param line 行バッファ
	 * @param lineNumber 行カウンター
	 * @param columonNumber 列カウンター
	 * @oaram currentCharacter 読み取り文字
	 * @param nextCharacter 先読み文字
	 */
	private BufferedReader sourceFile;
	private String line ;
	private int lineNumber;
	private int columnNumber ;
	private char currentCharacter ;
	private char nextCharacter ;
	FileScanner(String sourceFileName) {
		lineNumber=0;
		columnNumber=-1;
		nextCharacter='\n';



		Path path = Paths.get(sourceFileName);
		// ファイルのオープン
		try {
			sourceFile = Files.newBufferedReader(path);
		} catch (IOException err_mes) {
			System.out.println(err_mes);
			System.exit(1);
		}
		nextChar();

		// 各フィールドの初期化

	}

	/**
	 * sourceFileで参照しているファイルを閉じる
	 */
	void closeFile() {
		try {
			sourceFile.close();
		} catch (IOException err_mes) {
			System.out.println(err_mes);
			System.exit(1);
		}
	}

	/**
	 * sourceFile で参照しているファイルから一行読み, フィールド line(文字列変数) に
	 * その行を格納する 教科書 p. 210 ソースコード10.1 では while文で全行を読み取って
	 * いるが，このメソッド内では while文は使わず1行だけ読み取りフィールドline に格納する．
	 */
	void readNextLine() {
		try {
			if (sourceFile.ready()) { // sourceFile中に未読の行があるかを確認 (例外:IllegalStateException)
				/*
				 * nextLineメソッドでsourceFileから1行読み出し
				 * 読み出された文字列は改行コードを含まないので
				 * 改めて改行コードをつけ直す
				 */

				if ((line =sourceFile.readLine()) != null) {
					line+="\n";
				}

			} else {
				line=null;
			}
		} catch (IOException err_mes) { // 例外は Exception でキャッチしてもいい
			// ファイルの読み出しエラーが発生したときの処理
			System.out.println(err_mes);
			closeFile();
			System.exit(1);
		}
	}

	/**
	 *　次の文字を返す
	 */
	char lookAhead() {
		return nextCharacter;

	}

	/**
	 *フィールドlineのゲッター
	 */
	String getLine() {
		return line;
	}

	/**
	 * 次の文字を読む
	 * @return currentCharacter 今の文字
	 *
	 */

	 char nextChar() {
		 currentCharacter=nextCharacter;

//改行時の処理
		 if(nextCharacter=='\n') {
			 readNextLine();
			 if(line!=null) {

				 if(line!=null)
				  	nextCharacter=line.charAt(0);
				 lineNumber++;
				 columnNumber=0;
			 }else {
				 nextCharacter='\0';
			 }


		 }
		 //文末の処理
		 else if(nextCharacter=='\0') {

		 }
		 //文中の処理
		 else{

			 columnNumber++;
			 nextCharacter=line.charAt(columnNumber);
		 }



		return currentCharacter;
	 }

	/**
	 *読み込んでいる箇所を表示するメソッド
	 *SLexicalAnalyzerに合わせて修正を行った
	 */
	public String scanAt() {
		return "lineNumber:"+lineNumber+"\ncolumn:"+columnNumber+"currentCharacter="+currentCharacter;
	}
/**
 * メインメソッド
 *二通りのやり方でコードを出力する
 * @param args
 */
	public static void main(String args[]) {
		FileScanner fileScanner=new FileScanner("/Users/akiyama/Documents/workspace/ProjI20/bsort.k");
		/*2.5用
		System.out.print(fileScanner.getLine());

		fileScanner.readNextLine();
		while(fileScanner.getLine()!=null) {
			System.out.print(fileScanner.getLine());
			fileScanner.readNextLine();

		}*/

		/**

		char nextChar;
		while( (nextChar = fileScanner.nextChar() ) != '\0' ) {
		    System.out.print(nextChar);
		    2.6用
		}*/
		fileScanner.scanAt();


		fileScanner.closeFile();
	}



}
