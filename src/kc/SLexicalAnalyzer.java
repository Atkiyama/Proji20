package kc;

/**
*
* @author atkiyama
*問題番号3.2
*提出日 2020/6/3
*字句解析をする簡易版クラス
*
*/
class SLexicalAnalyzer {
    private FileScanner sourceFileScanner; // 入力ファイルのFileScannerへの参照

    /**
	 * 引数でフィールドを初期化する
	 * @param sourceFileName	ソースファイル名
	 */
    SLexicalAnalyzer(String sourceFileName) {
    			sourceFileScanner =new FileScanner(sourceFileName);

    }

    /**
     *トークンを識別するメソッド
     */
    Token nextToken() {
    		//非初期化エラー回避のためにtokenを初期化しておく
    		Token token=new Token(Symbol.EOF);
    		//一文字を解析するときにStringとchar両方用意しておいたほうがスムーズなので用意する
    		char word=sourceFileScanner.nextChar();
    		String words =String.valueOf(word);
    		if(word=='0') {
    			//0のときの処理
     			token=new Token(Symbol.INTEGER,0);
        	}else if(Character.isDigit(word)) {
        		//数字が続く場合の処理
        		while(Character.isDigit(sourceFileScanner.lookAhead())) {
        			//数字の桁を足していく
        			words +=String.valueOf(sourceFileScanner.nextChar());
        		}
        		token =new Token(Symbol.INTEGER,Integer.parseInt(words));

        	}else if(word=='+') {
        		token =new Token(Symbol.ADD);
        	}else if(word=='!') {
        		//！か！=の判別
        		if(sourceFileScanner.lookAhead()=='='){
        				words +=String.valueOf(sourceFileScanner.nextChar());
        				token =new Token(Symbol.NOTEQ);
        		}else {
        			token=new Token(Symbol.NOT);
        		}
        	}else if(word=='=') {
        		//=か==の判別
        		if(sourceFileScanner.lookAhead()=='='){
    				words +=String.valueOf(sourceFileScanner.nextChar());
    				token =new Token(Symbol.EQUAL);
        		}else {
        			token=new Token(Symbol.ASSIGN);
        		}
        	}else if(word=='\0'){
        		//終了のときの処理
        		token=new Token(Symbol.EOF);
        	}else if(word==' '||word=='\n') {
        		//sourceFileScanner.nextChar();
        		//空行や改行を飛ばすために再起処理で飛ばし進める
        		token=nextToken();
        	}else {
        		syntaxError();
        		token=new Token(Symbol.EOF);
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
        System.out.print (sourceFileScanner.scanAt());
        //下記の文言は自動採点で使用するので変更しないでください。
        System.out.println ("で字句解析プログラムが構文エラーを検出");
        closeFile();
        System.exit(1);
    }
}
