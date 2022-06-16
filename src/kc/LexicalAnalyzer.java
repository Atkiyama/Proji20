package kc;

/**
*
* @author atkiyama
*問題番号3.3
*提出日 2020/6/17
*字句解析をするクラス
*
*/
class LexicalAnalyzer {

	private FileScanner sourceFileScanner; // 入力ファイルのFileScannerへの参照

	/**
	 * 引数でフィールドを初期化する
	 * @param sourceFileName	ソースファイル名
	 */
	LexicalAnalyzer(String sourceFileName) {
		sourceFileScanner = new FileScanner(sourceFileName);
	}

	/**
	 *トークンを識別するメソッド
	 */

	Token nextToken() {
		//非初期化エラー回避のためにtokenを初期化しておく
		Token token = new Token(Symbol.ERR);
		//一文字を解析するときにStringとchar両方用意しておいたほうがスムーズなので用意する
		char word = sourceFileScanner.nextChar();
		String words = String.valueOf(word);
		if (word == '0') {
			if (sourceFileScanner.lookAhead() == 'x') {
				//16進数処理
				sourceFileScanner.nextChar();
				while (Character.digit(sourceFileScanner.lookAhead(), 16) != -1) {
					//数字の桁を足していく
					words += String.valueOf(sourceFileScanner.nextChar());
				}
				//Stringの16進数を10進intで登録
				if(words==null)
					syntaxError();
				token = new Token(Symbol.INTEGER, Integer.parseUnsignedInt(words, 16));

			} else {
				token = new Token(Symbol.INTEGER, 0);
				//0のときの処理
			}

		} else if (Character.isDigit(word)) {
			//数字が続く場合の処理
			while (Character.isDigit(sourceFileScanner.lookAhead())) {
				//数字の桁を足していく
				words += String.valueOf(sourceFileScanner.nextChar());
			}
			token = new Token(Symbol.INTEGER, Integer.parseInt(words));

		} else if (word == '+') {
			//+,+-,++の識別
			if (sourceFileScanner.lookAhead() == '=') {
				token = new Token(Symbol.ASSIGNADD);
				sourceFileScanner.nextChar();
			} else if (sourceFileScanner.lookAhead() == '+') {
				sourceFileScanner.nextChar();
				token = new Token(Symbol.INC);
			} else {
				token = new Token(Symbol.ADD);
			}
		} else if (word == '-') {
			//-,-=,--の識別
			if (sourceFileScanner.lookAhead() == '-') {
				token = new Token(Symbol.DEC);
				sourceFileScanner.nextChar();
			} else if (sourceFileScanner.lookAhead() == '=') {
				token = new Token(Symbol.ASSIGNSUB);
				sourceFileScanner.nextChar();
			} else {
				token = new Token(Symbol.SUB);
			}
		} else if (word == '*') {
			//*,*-の識別
			if (sourceFileScanner.lookAhead() == '=') {
				sourceFileScanner.nextChar();
				token = new Token(Symbol.ASSIGNMUL);
			} else {
				token = new Token(Symbol.MUL);
			}
		} else if (word == '/') {
			// /と/=の識別
			if (sourceFileScanner.lookAhead() == '=') {
				token = new Token(Symbol.ASSIGNDIV);
				sourceFileScanner.nextChar();
			} else {
				token = new Token(Symbol.DIV);
			}
		} else if (word == '%') {
			//%の識別
			token = new Token(Symbol.MOD);
		} else if (word == '!') {
			//！か！=の判別
			if (sourceFileScanner.lookAhead() == '=') {
				words += String.valueOf(sourceFileScanner.nextChar());
				token = new Token(Symbol.NOTEQ);
			} else {
				token = new Token(Symbol.NOT);
			}
		} else if (word == '=') {
			//=か==の判別
			if (sourceFileScanner.lookAhead() == '=') {
				words += String.valueOf(sourceFileScanner.nextChar());
				token = new Token(Symbol.EQUAL);
			} else {
				token = new Token(Symbol.ASSIGN);
			}
		} else if (word == '\0') {
			//終了のときの処理
			if (sourceFileScanner.lookAhead() == '\0') {
				token = new Token(Symbol.EOF);
			} else {
				token = new Token(Symbol.NULL);
			}
		} else if (word == ' ' || word == '\n') {
			//sourceFileScanner.nextChar();
			//空行や改行を飛ばすために再起処理で飛ばし進める
			token = nextToken();
			//以下165行目まで比較演算子の処理
		} else if (word == '>') {
			token = new Token(Symbol.GREAT);
		} else if (word == '<') {
			token = new Token(Symbol.LESS);
		} else if (word == '|' && sourceFileScanner.lookAhead() == '|') {
			token = new Token(Symbol.OR);
			sourceFileScanner.nextChar();
		} else if (word == '&' && sourceFileScanner.lookAhead() == '&') {
			sourceFileScanner.nextChar();
			token = new Token(Symbol.AND);
		} else if (word == ';') {
			token = new Token(Symbol.SEMICOLON);
		} else if (word == '(') {
			token = new Token(Symbol.LPAREN);
		} else if (word == ')') {
			token = new Token(Symbol.RPAREN);
		} else if (word == '{') {
			token = new Token(Symbol.LBRACE);
		} else if (word == '}') {
			token = new Token(Symbol.RBRACE);
		} else if (word == '[') {
			token = new Token(Symbol.LBRACKET);
		} else if (word == ']') {
			token = new Token(Symbol.RBRACKET);
		} else if (word == ',') {
			token = new Token(Symbol.COMMA);
		} else if (Character.isLowerCase(word) || Character.isUpperCase(word) || word == '_') {
			//文字列をStringで読み取り予約語や変数の処理
			while (Character.isLowerCase(sourceFileScanner.lookAhead())
					|| Character.isUpperCase(sourceFileScanner.lookAhead()) || sourceFileScanner.lookAhead() == '_'|| Character.isDigit(sourceFileScanner.lookAhead())) {
				words += String.valueOf(sourceFileScanner.nextChar());
			}
			if (words.equals("main")) {
				token = new Token(Symbol.MAIN);
			} else if (words.equals("for")) {
				token = new Token(Symbol.FOR);
			} else if (words.equals("while")) {
				token = new Token(Symbol.WHILE);
			}else if (words.equals("if")) {
				token = new Token(Symbol.IF);
			} else if (words.equals("inputint")) {
				token = new Token(Symbol.INPUTINT);
			} else if (words.equals("inputchar")) {
				token = new Token(Symbol.INPUTCHAR);
			} else if (words.equals("outputint")) {
				token = new Token(Symbol.OUTPUTINT);
			} else if (words.equals("outputchar")) {
				token = new Token(Symbol.OUTPUTCHAR);
			} else if (words.equals("int")) {
				token = new Token(Symbol.INT);
			} else if (words.equals("break")) {
				token = new Token(Symbol.BREAK);
			} else {
				token = new Token(Symbol.NAME, words);
			}
		} else if (word == '\'') {
			//シングルクオーテーションの処理
			word = sourceFileScanner.nextChar();


			if(sourceFileScanner.nextChar()!='\'') {
				//System.out.println("hoge");
				syntaxError();
			}else {
				token = new Token(Symbol.CHARACTER, word);

			}
			//token = new Token(Symbol.CHARACTER, word);

		} else {
			//エラー処理
			token = new Token(Symbol.ERR);
			syntaxError();
		}
		return token;
	}

	/**
	 * ファイルを閉じるメソッド
	 */
	void closeFile() {
		sourceFileScanner.closeFile();
	}

	/**
	 * 現在どの部分を解析中かを返す。
	 * @return	解析中の部分
	 */
	String analyzeAt() {
		return sourceFileScanner.scanAt();
	}

	/**
	 * 字句解析時に構文エラーを検出したときに呼ばれるメソッド
	 */
	void syntaxError() {
		System.out.print(sourceFileScanner.scanAt());
		//下記の文言は自動採点で使用するので変更しないでください。
		System.out.println("で字句解析プログラムが構文エラーを検出");
		closeFile();
		System.exit(1);
	}

	public FileScanner getSourceFileScanner() {
		return sourceFileScanner;
	}
}
