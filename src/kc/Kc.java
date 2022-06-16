package kc;

import java.util.ArrayList;

/**
*
* @author atkiyama
*問題番号5.1
*提出日 2020/8/5
*構文解析をしアセンブラコードを出力するクラス
*
*/
public class Kc {
	/**
	 * lexer 字句解析プログラム
	 * token 今読んでいる文字トークン
	 * variableTable 変数テーブル
	 * iseg スタックやisegを管理するクラス
	 * breakAddrList break文でのjump命令の番地のリスト
	 * inLoop loopの中であればtrueを保持する
	 */
	private LexicalAnalyzer lexer;
	private Token token;
	private VarTable variableTable;
    private PseudoIseg iseg;
    private ArrayList<Integer> breakAddrList;
    private boolean inLoop;

	/**
	 * フィールドを初期化し最初のトークンを読む。なおinLoopはfalseで初期化する
	 * @param sourceFileName	読み込むファイル名
	 */
	Kc(String sourceFileName) {
		lexer = new LexicalAnalyzer(sourceFileName);
		token = lexer.nextToken();
		iseg = new PseudoIseg();
		variableTable =new VarTable();
		breakAddrList=new ArrayList<>();
		inLoop=false;

	}

	/**
	 * 構文Programを解析するメソッド
	 */
	void parseProgram() {
		if (token.checkSymbol(Symbol.MAIN))
			parseMain_function();
		else
			syntaxError("mainが期待されます");
		if (token.checkSymbol(Symbol.EOF))
			token = lexer.nextToken();
		else
			syntaxError("EOFが期待されます");
		//プログラム終了のアセンブラコード
		iseg.appendCode(Operator.HALT);
	}
	/**
	 * 構文main_functionを解析するメソッド
	 */
	void parseMain_function() {
		if (token.checkSymbol(Symbol.MAIN))
			token = lexer.nextToken();
		else
			syntaxError("mainが期待されます");
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			syntaxError("(が期待されます");
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage(")", "parseMain_function()"));
		if (token.checkSymbol(Symbol.LBRACE))
			parseBlock();
		else
			syntaxError(makeErrorMessage("{", "parseMain_function()"));

	}
/**
 * 構文Blockを解析するメソッド
 */
	void parseBlock() {
		if (token.checkSymbol(Symbol.LBRACE))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("{", "parseBlock()"));
		while (token.checkSymbol(Symbol.INT))
			parseVar_decl();
		while (judgeStatement())
			parseStatement();

		if (token.checkSymbol(Symbol.RBRACE))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("}", "parseBlock()"));
	}
	/**
	 * 構文Statementを解析するメソッド
	 */
	private void parseStatement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.IF))
			parseIf_statement();
		else if (token.checkSymbol(Symbol.WHILE))
			parseWhile_Statement();
		else if (token.checkSymbol(Symbol.FOR))
			parseFor_Statement();
		else if (judgeExp())
			parseExp_Statement();
		else if (token.checkSymbol(Symbol.OUTPUTCHAR))
			parseOutputchar_Statement();
		else if (token.checkSymbol(Symbol.OUTPUTINT))
			parseOutputInt_Statement();
		else if (token.checkSymbol(Symbol.BREAK))
			parseBrake_Statement();
		else if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else if (token.checkSymbol(Symbol.LBRACE)) {
			token = lexer.nextToken();
			while (judgeStatement())
				parseStatement();
			if (token.checkSymbol(Symbol.RBRACE))
				token = lexer.nextToken();
			else
				syntaxError(makeErrorMessage("}", "parseStatement()"));
		} else
			syntaxError(makeErrorMessage("if,while,for,outputchar,outputint,break,セミコロン,{,Exp", "parseStatement()"));

	}
	/**
	 * 構文OutputCharを解析するメソッド
	 */
	private void parseOutputchar_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.OUTPUTCHAR)) {
			token = lexer.nextToken();
		}else
			this.syntaxError(makeErrorMessage("outputchar", "parseOutputchar_Statement()"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("(", "parseOutputchar_Statement()"));
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp", "parseOutputchar_Statement()"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage(")", "parseOutputchar_Statement()"));
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseOutputchar_Statement()"));
		//outputをisegに積む
		iseg.appendCode(Operator.OUTPUTC);
		iseg.appendCode(Operator.OUTPUTLN);

	}
	/**
	 * 構文break_Statementを解析するメソッド
	 */
	private void parseBrake_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.BREAK))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("break", "parseBrake_Statement()"));
		//ループ外でbreak文を使用した時のエラー
		if(!inLoop)
			syntaxError("ループ内ではありません");
		//break文のjump命令を積む、またリストに番地を登録する
		int addr = iseg.appendCode(Operator.JUMP,-1);
		breakAddrList.add(addr);
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseBrake_Statement()"));

	}
	/**
	 * 構文OutputIntを解析するメソッド
	 */
	private void parseOutputInt_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.OUTPUTINT)) {
			token = lexer.nextToken();
		}else
			this.syntaxError(makeErrorMessage("outputint", "parseOutputInt_Statement()"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("(", "parseOutputInt_Statement()"));
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp", "parseOutputInt_Statement()"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage(")", "parseOutputInt_Statement()"));
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseOutputInt_Statement()"));
		//出力のアセンブラコードを積む
		iseg.appendCode(Operator.OUTPUT);
		iseg.appendCode(Operator.OUTPUTLN);

	}
	/**
	 * 構文exp_Statementを解析するメソッド
	 */
	private void parseExp_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (judgeExp()) {
			parseExpression();
		} else
			this.syntaxError(makeErrorMessage("exp", "parseExp_Statement()"));
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseExp_Statement()"));
		iseg.appendCode(Operator.REMOVE);
	}
	/**
	 * 構文forを解析するメソッド
	 */
	private void parseFor_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.FOR))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("outputint", "parseFor_Statement() "));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("(", "parseFor_Statement() "));
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp", "parseFor_Statement() "));
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseFor_Statement() "));
		//remove命令のアドレスを保持
		int removeAddr = iseg.appendCode(Operator.REMOVE);
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp", "parseFor_Statement() "));
		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("セミコロン", "parseFor_Statement() "));
		//beq、jump命令のアドレスを保持
		int beqAddr = iseg.appendCode(Operator.BEQ,-1);
		int jumpAddr = iseg.appendCode(Operator.JUMP,-1);
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp", "parseFor_Statement() "));
		iseg.appendCode(Operator.REMOVE);
		//2つめのjump命令のアドレスを保持
		int jumpAddr2 = iseg.appendCode(Operator.JUMP,-1);
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage(")", "parseFor_Statement() "));
		if (judgeStatement())
			parseStatement();
		else
			syntaxError(makeErrorMessage("if,while,for,outputchar,outputint,break,セミコロン,{,Exp", "parseFor_Statement() "));
		//3つめのjump命令のアドレスを保持
		int jumpAddr3 = iseg.appendCode(Operator.JUMP,-1);
		//必要に応じて命令の対象アドレスを
		iseg.replaceCode(jumpAddr2, removeAddr+1);
		iseg.replaceCode(jumpAddr3, jumpAddr+1);
		iseg.replaceCode(jumpAddr, jumpAddr2+1);
		iseg.replaceCode(beqAddr, jumpAddr3+1);


	}
	/**
	 * 構文while_Statementを解析するメソッド
	 */
	private void parseWhile_Statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.WHILE))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("while","parseWhile_Statement"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("(","parseWhile_Statement"));
		int lastAddr = iseg.getLastCodeAddress();
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp","parseWhile_Statement"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage(")","parseWhile_Statement"));
		//beq命令のアドレスを保持
		int beqAddr = iseg.appendCode (Operator.BEQ, -1);
		//フィールド情報を一時的に記憶
		boolean outerLoop = inLoop;
		ArrayList<Integer> outerList = breakAddrList;
		//フィールドを変更し元ループ仕様に初期化
		inLoop = true;
		breakAddrList = new ArrayList<>();
		if (judgeStatement())
			parseStatement();
		else
			this.syntaxError(makeErrorMessage("if,while,for,outputchar,outputint,break,セミコロン,{,Exp","parseWhile_Statement"));
		//jump命令のアドレスを保持
		int jumpAddr = iseg.appendCode(Operator.JUMP, lastAddr+1);
		for(int i=0;i<breakAddrList.size();++i)
			//break文でのjump先を決定
			iseg.replaceCode (breakAddrList.get(i), jumpAddr+1);
		//フィールドをもとに戻す
		inLoop=outerLoop;
		breakAddrList = outerList;
		//beq命令の対象アドレスを変更
		iseg.replaceCode (beqAddr, jumpAddr+1);

	}
	/**
	 * 構文if_Statementを解析するメソッド
	 */
	private void parseIf_statement() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.IF))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("if","parseIf_statement()"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage("(","parseIf_statement()"));
		if (judgeExp())
			parseExpression();
		else
			this.syntaxError(makeErrorMessage("exp","parseIf_statement()"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			this.syntaxError(makeErrorMessage(")","parseIf_statement()"));
		//beq命令の番地を取得
		int beqAddr = iseg.appendCode (Operator.BEQ, -1);
		if (judgeStatement())
			parseStatement();
		else
			this.syntaxError(makeErrorMessage("if,while,for,outputchar,outputint,break,セミコロン,{,Exp","parseIf_statement()"));
		int stLastAddr = iseg.getLastCodeAddress();
		// <St> 部分のコードの末尾のコードのアドレスを得る
		iseg.replaceCode (beqAddr, stLastAddr+1);

	}
	/**
	 * 構文expを解析するメソッド
	 * @return 左辺値が正しい変数かどうか
	 */
	private boolean parseExp() {
		boolean rightIsVar = true;
		if (judgeExp())
			rightIsVar = parseLogical_Term();
		else
			syntaxError(makeErrorMessage("exp"));

		if (token.checkSymbol(Symbol.OR)) {
			token = lexer.nextToken();
			if (judgeExp())
				parseExp();
			else
				syntaxError(makeErrorMessage("exp"));
			iseg.appendCode(Operator.OR);
		}
		return rightIsVar;
	}
	/**
	 * 構文logical_termの解析をするメソッド
	 * @return 左辺値が正しい変数かどうか
	 */
	private boolean parseLogical_Term() {
		boolean rightIsVar = true;
		// TODO 自動生成されたメソッド・スタブ
		if (judgeExp())
			rightIsVar = parseLogical_factor();
		else
			syntaxError(makeErrorMessage("exp"));
		if (token.checkSymbol(Symbol.AND)) {
			token = lexer.nextToken();
			if (judgeExp()) {
				parseLogical_Term();
			}
			iseg.appendCode(Operator.AND);

		}
		return rightIsVar;
	}
	/**
	 * 構文logical_factorを解析するメソッド
	 * @return 左辺値が正しい変数かどうか
	 */
	private boolean parseLogical_factor() {
		// TODO 自動生成されたメソッド・スタブ
		boolean rightIsVar = true;
		if (judgeExp())
			rightIsVar = parseArithmetic_Expression();
		else
			syntaxError(makeErrorMessage("exp"));
		if (token.checkSymbol(Symbol.EQUAL) || token.checkSymbol(Symbol.NOTEQ) || token.checkSymbol(Symbol.LESS)
				|| token.checkSymbol(Symbol.GREAT)) {
			Symbol op = token.getSymbol();
			token = lexer.nextToken();
			if (judgeExp())
				parseArithmetic_Expression();
			else
				syntaxError(makeErrorMessage("exp"));
			int compAddr = iseg.appendCode(Operator.COMP);
			if(op.equals(Symbol.EQUAL))
				 iseg.appendCode(Operator.BEQ,compAddr+4);
			else if(op.equals(Symbol.LESS))
				iseg.appendCode(Operator.BLT,compAddr+4);
			else if(op.equals(Symbol.LESSEQ))
				iseg.appendCode(Operator.BLE,compAddr+4);
			else if(op.equals(Symbol.GREAT))
				iseg.appendCode(Operator.BGT,compAddr+4);
			else if(op.equals(Symbol.GREATEQ))
				iseg.appendCode(Operator.BGE,compAddr+4);

			iseg.appendCode(Operator.PUSHI,0);
			iseg.appendCode(Operator.JUMP,compAddr+5);
			iseg.appendCode(Operator.PUSHI,1);

		}
		return rightIsVar;


	}
	/**
	 * 構文parseArithmetic_Expressionを解析するメソッド
	 * @return 左辺値が正しい変数かどうか
	 */
	private boolean parseArithmetic_Expression() {
		//アリスメティック
		// TODO 自動生成されたメソッド・スタブ
		boolean rightIsVar = true;
		if (judgeExp())
			rightIsVar = parseArithmetic_term();
		else
			syntaxError(makeErrorMessage("exp"));
		while (token.checkSymbol(Symbol.ADD) || token.checkSymbol(Symbol.SUB)) {
			rightIsVar = false;
			if(token.checkSymbol(Symbol.ADD)) {
				token = lexer.nextToken();
				if (judgeExp())
					parseArithmetic_term();
				else
					syntaxError(makeErrorMessage("exp"));
				iseg.appendCode(Operator.ADD);
			}else{
				token = lexer.nextToken();
				if (judgeExp())
					parseArithmetic_term();
				else
					syntaxError(makeErrorMessage("exp"));
				iseg.appendCode(Operator.SUB);
			}
		}
		return rightIsVar;

	}
		/**
		 * 構文parseArithmetic_termを解析するクラス
		 * @return 左辺値が正しい変数かどうか
		 */
		private boolean parseArithmetic_term() {
		// TODO 自動生成されたメソッド・スタブ
			boolean rightIsVar = false;
		if (judgeExp())
			rightIsVar = parseArithmetic_factor();
		else
			syntaxError(makeErrorMessage("exp"));
		while (token.checkSymbol(Symbol.DIV) || token.checkSymbol(Symbol.MUL) || token.checkSymbol(Symbol.MOD)) {
			rightIsVar = false;
			if(token.checkSymbol(Symbol.DIV)) {
				token = lexer.nextToken();
				if (judgeExp())
					parseArithmetic_factor();
				else
					syntaxError(makeErrorMessage("exp"));
				iseg.appendCode(Operator.DIV);
			}else if(token.checkSymbol(Symbol.MUL)) {
				token = lexer.nextToken();
				if (judgeExp())
					parseArithmetic_factor();
				else
					syntaxError(makeErrorMessage("exp"));
				iseg.appendCode(Operator.MUL);
			}else {
				token = lexer.nextToken();
				if (judgeExp())
					parseArithmetic_factor();
				else
					syntaxError(makeErrorMessage("exp"));
				iseg.appendCode(Operator.MOD);
			}
		}
		return rightIsVar;
	}
		/**
		 * 構文parseArithmetic_factorを解析するクラス
		 * @return 左辺値が正しい変数かどうか
		 */
	private boolean parseArithmetic_factor() {
		// TODO 自動生成されたメソッド・スタブ
		boolean rightIsVar =true;
		if (judgeUnsignedFactor())
			rightIsVar = parseUnsigndeFactor();
		else if (token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.NOT)) {
			rightIsVar = false;
			if(token.checkSymbol(Symbol.SUB)) {
				token = lexer.nextToken();
				if (judgeUnsignedFactor())
					parseArithmetic_factor();
				else
					syntaxError("makeErrorMessage(\"exp\")");
				iseg.appendCode(Operator.CSIGN);
			}else {
				token = lexer.nextToken();
				if (judgeUnsignedFactor())
					parseArithmetic_factor();
				else
					syntaxError("makeErrorMessage(\"exp\")");
				iseg.appendCode(Operator.NOT);
			}
		}
		return rightIsVar;

	}
	/**
	 * 構文unsignedFactorを解析するクラス
	 * @return 左辺値が正しい変数かどうか
	 */
	private boolean parseUnsigndeFactor() {
		// TODO 自動生成されたメソッド・スタブ
		//NAME [ "++" |  "--" |  ( "[" ‹Expression> "]" ) ] 部分の解析
		boolean rightIsVar=true;

		if (token.checkSymbol(Symbol.NAME)) {
			String name = token.getStrValue();//変数名を所得
			//登録されてるか確認
			if(!variableTable.exist(name))
				syntaxError("その変数は登録されていません");
			int address = variableTable.getAddress(name);
			token = lexer.nextToken();

			if(token.checkSymbol(Symbol.ASSIGN)||token.checkSymbol(Symbol.ASSIGNADD)||token.checkSymbol(Symbol.ASSIGNSUB)||
					token.checkSymbol(Symbol.ASSIGNMUL)||token.checkSymbol(Symbol.ASSIGNDIV)||variableTable.getType(name).equals(Type.ARRAYOFINT))
				iseg.appendCode(Operator.PUSHI,address);
			else
				iseg.appendCode(Operator.PUSH,address);


			if (token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC)) {
				rightIsVar = false;
				//iseg.appendCode(Operator.PUSH,address);
				if(variableTable.checkType(name, Type.ARRAYOFINT))
					syntaxError("型が不一致です");
				iseg.appendCode(Operator.COPY);
				if(token.checkSymbol(Symbol.INC))
					iseg.appendCode(Operator.INC);
				else
					iseg.appendCode(Operator.DEC);
				iseg.appendCode(Operator.POP,address);
				//後置
				token=lexer.nextToken();
			}

			else if (token.checkSymbol(Symbol.LBRACKET)) {
				token = lexer.nextToken();
				if(!variableTable.checkType(name, Type.ARRAYOFINT))
					syntaxError("型が不一致です");

				if (judgeExp())
					parseExpression();
				else
					syntaxError(makeErrorMessage("exp","parseUnsigndeFactor()"));
				if (token.checkSymbol(Symbol.RBRACKET))
					token = lexer.nextToken();
				else
					syntaxError(makeErrorMessage("]","parseUnsigndeFactor()"));
				iseg.appendCode(Operator.ADD);

				if(!(token.checkSymbol(Symbol.ASSIGN)||token.checkSymbol(Symbol.ASSIGNADD)||token.checkSymbol(Symbol.ASSIGNSUB)||
						token.checkSymbol(Symbol.ASSIGNMUL)||token.checkSymbol(Symbol.ASSIGNDIV)))
					iseg.appendCode(Operator.LOAD);

			}


		} else if (token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC)) {
			//( "++" | "--" ) NAME [ "[" ‹Expression> "]" ]
			String name ="";
			rightIsVar =false;
			boolean incOrDec =false;
			int address=0;
			if(token.checkSymbol(Symbol.INC)) {
				incOrDec =true;
			}
			token = lexer.nextToken();
			if (token.checkSymbol(Symbol.NAME)) {
				//登録されてるか確認
				name = token.getStrValue();
				if(!variableTable.exist(token.getStrValue()))
					syntaxError("その変数は登録されていません");

				//番地の取得
				address=variableTable.getAddress(token.getStrValue());
				token = lexer.nextToken();
				iseg.appendCode(Operator.PUSHI,address);
			}else
				syntaxError(makeErrorMessage("name","parseUnsigndeFactor()"));


			if (token.checkSymbol(Symbol.LBRACKET)) {
				if(variableTable.checkType(name, Type.INT))
					syntaxError("型が不一致です");

				token = lexer.nextToken();
				if (judgeExp())
					parseExpression();
				else
					syntaxError(makeErrorMessage("expU","parseUnsigndeFactor()"));
				if (token.checkSymbol(Symbol.RBRACKET))
					token = lexer.nextToken();
				else
					syntaxError(makeErrorMessage("]","parseUnsigndeFactor()"));
				iseg.appendCode(Operator.ADD);
				iseg.appendCode(Operator.COPY);
				iseg.appendCode(Operator.LOAD);
				if(incOrDec)
					iseg.appendCode(Operator.INC);
				else
					iseg.appendCode(Operator.DEC);
				iseg.appendCode(Operator.ASSGN);

			}else {
				iseg.appendCode(Operator.PUSH,address);
				if(incOrDec)
					iseg.appendCode(Operator.INC);
				else
					iseg.appendCode(Operator.DEC);
				iseg.appendCode(Operator.ASSGN);
			}

		}else if (token.checkSymbol(Symbol.INTEGER)){
				int value = token.getIntValue();
				token = lexer.nextToken();
				iseg.appendCode(Operator.PUSHI, value);
				rightIsVar =false;
		}else if(token.checkSymbol(Symbol.CHARACTER)){
				int value = token.getIntValue();
				token = lexer.nextToken();
				iseg.appendCode(Operator.PUSHI,value);
				rightIsVar =false;
		}else if(token.checkSymbol(Symbol.INPUTCHAR)){
				token = lexer.nextToken();
				iseg.appendCode(Operator.INPUTC);
				lexer.getSourceFileScanner().lookAhead();
		}else if(token.checkSymbol(Symbol.INPUTINT)){
				token = lexer.nextToken();
				iseg.appendCode(Operator.INPUT);
		}else if (token.checkSymbol(Symbol.LPAREN)) {
			//"(" ‹Expression> ")"の解析
			token = lexer.nextToken();
			if (judgeExp())
				parseExpression();
			else
				syntaxError(makeErrorMessage("exp","parseUnsigndeFactor()"));
			if (token.checkSymbol(Symbol.RPAREN))
				token = lexer.nextToken();
			else
				syntaxError(makeErrorMessage(")","parseUnsigndeFactor()"));

		} else if (token.checkSymbol(Symbol.ADD)) {
			parseSum_function();
			rightIsVar =false;
		}
		else if (token.checkSymbol(Symbol.MUL)) {
			parseProduct_function();
			rightIsVar = false;
		}
		else
			syntaxError(makeErrorMessage("NAME,\"++\",\"--\",INT,CHAR,\"(\",\"inputchar\", \"inputint\",\"+\",\"*\"","parseUnsigndeFactor()"));
		return rightIsVar;


	}

	/**
	 * 構文Product_functionを解析するクラス
	 *  本来の仕様と違い逆ポーランド記法のように全てpushした後で計算をする
	 */
	private void parseProduct_function() {
		// TODO 自動生成されたメソッド・スタブ
		int times=0;
		if (token.checkSymbol(Symbol.MUL))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("*","parseProduct_function()"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("(","parseProduct_function()"));
		if (judgeExp())
			times=parseExpression_list();
		else
			syntaxError(makeErrorMessage("exp","parseProduct_function()"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage(")","parseProduct_function()"));
		for(int i=0;i<times;i++)
			iseg.appendCode(Operator.MUL);

	}
	/**
	 * 構文expression_listを解析するクラス
	 * @return 要素数(和関数、積関数で使用)
	 */
	 int parseExpression_list() {
		// TODO 自動生成されたメソッド・スタブ
		 //要素数
		 int times=0;
		if (judgeExp())
			parseExpression();
		else
			syntaxError(makeErrorMessage("exp"));
		while (token.checkSymbol(Symbol.COMMA)) {
			token = lexer.nextToken();
			if (judgeExp())
				parseExpression();
			else
				syntaxError(makeErrorMessage("exp"));
			times++;
		}
		return times;
	}
	/**
	 * 構文Sum_functionを解析するクラス
	 * 本来の仕様と違い逆ポーランド記法のように全てpushした後で計算をする
	 */
	private void parseSum_function() {
		// TODO 自動生成されたメソッド・スタブ
		//計算回数
		int times =0;
		if (token.checkSymbol(Symbol.ADD))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("+","parseSum_function()"));
		if (token.checkSymbol(Symbol.LPAREN))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("(","parseSum_function()"));
		if (judgeExp())
			times = parseExpression_list();
		else
			syntaxError(makeErrorMessage("exp","parseSum_function()"));
		if (token.checkSymbol(Symbol.RPAREN))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage(")","parseSum_function()"));
		for(int i=0;i<times;i++)
			iseg.appendCode(Operator.ADD);
	}
	/**
	 * 構文Expressionを解析するクラス
	 * 代入を行うので左辺値の判定も行う
	 */
	private void parseExpression() {
		// TODO 自動生成されたメソッド・スタブ
		boolean  rightIsVar = true;
		if (judgeExp())
			rightIsVar = parseExp();
		else
			syntaxError(makeErrorMessage("expエラー"));


		if (token.checkSymbol(Symbol.ASSIGN) || token.checkSymbol(Symbol.ASSIGNADD)
				|| token.checkSymbol(Symbol.ASSIGNSUB) || token.checkSymbol(Symbol.ASSIGNMUL)
				|| token.checkSymbol(Symbol.ASSIGNDIV)) {

			if(!rightIsVar)
				syntaxError("左辺値が無効です");
			Symbol op = token.getSymbol();
			token = lexer.nextToken();
			if(!op.equals(Symbol.ASSIGN)) {
				//+=,-=,*=,/=の場合の処理
				iseg.appendCode(Operator.COPY);
				iseg.appendCode(Operator.LOAD);
			}
			if (judgeExp())
				parseExpression();
			else
				syntaxError(makeErrorMessage("exp"));
			//ここ以下でスタックに代入系を詰む
			if(op.equals(Symbol.ASSIGNADD))
				iseg.appendCode(Operator.ADD);
			else if(op.equals(Symbol.ASSIGNSUB))
				iseg.appendCode(Operator.SUB);
			else if(op.equals(Symbol.ASSIGNMUL))
				iseg.appendCode(Operator.MUL);
			else if(op.equals(Symbol.ASSIGNDIV))
				iseg.appendCode(Operator.MUL);
			iseg.appendCode(Operator.ASSGN);
			}

	}
	/**
	 * 構文Var_declを解析するクラス
	 */
	private void parseVar_decl() {
		// TODO 自動生成されたメソッド・スタブ
		//ここをintegerにすると発生
		if (token.checkSymbol(Symbol.INT))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("int"));
		if (token.checkSymbol(Symbol.NAME)) {
			parseName_List();
		}else
			syntaxError(makeErrorMessage("name"));

		if (token.checkSymbol(Symbol.SEMICOLON))
			token = lexer.nextToken();
		else
			syntaxError(makeErrorMessage("セミコロン"));

	}
	/**
	 * 構文parseNameを解析するクラス
	 */
	private void parseName() {
		int value =0;
		String name = "";
		if (token.checkSymbol(Symbol.NAME)) {
			name =token.getStrValue();
			token = lexer.nextToken();
		}else
			syntaxError(makeErrorMessage("name"));
		if (token.checkSymbol(Symbol.ASSIGN)) {
			token = lexer.nextToken();
			if (judgeConstant()){
				//変数の値を取得
				value =parseConstant();
			}else syntaxError(makeErrorMessage("constant","parseName"));
			if(!variableTable.exist(name))
				//多重定義の判別
				variableTable.registerNewVariable(Type.INT,name,1);
			else
				syntaxError("変数が登録されています");
				int address = variableTable.getAddress(name);
				iseg.appendCode(Operator.PUSHI,value);
				iseg.appendCode(Operator.POP,address);

		}
		else if (token.checkSymbol(Symbol.LBRACKET)) {
		//"[" ( INTEGER "]"...の解析
			token = lexer.nextToken();
			if (token.checkSymbol(Symbol.INTEGER)) {
				int intValue = token.getIntValue();
				token = lexer.nextToken();

				if (token.checkSymbol(Symbol.RBRACKET))
					token = lexer.nextToken();
				else
					this.syntaxError(makeErrorMessage("]"));

				if(!variableTable.exist(name)) {
					//多重定義の判別
					variableTable.registerNewVariable(Type.ARRAYOFINT,name,intValue);
				}else {
					syntaxError("変数(配列)が登録されています");
				}

			} else if (token.checkSymbol(Symbol.RBRACKET)) {
				ArrayList<Integer> constList =new ArrayList<>();
				token = lexer.nextToken();
				if (token.checkSymbol(Symbol.ASSIGN))
					token = lexer.nextToken();
				else
					this.syntaxError(makeErrorMessage("="));
				if (token.checkSymbol(Symbol.LBRACE))
					token = lexer.nextToken();
				else
					this.syntaxError(makeErrorMessage("{"));
				if (judgeConstant())
					constList = parseConstantList();
				else
					this.syntaxError(makeErrorMessage("ConstantList"));
				if (token.checkSymbol(Symbol.RBRACE))
					token = lexer.nextToken();
				else
					this.syntaxError(makeErrorMessage("}"));
				if(!variableTable.exist(name)) {
					//多重定義の判別
					variableTable.registerNewVariable(Type.ARRAYOFINT,name,constList.size());
				}else {
					syntaxError("変数(配列)が登録されています");
				};
				int address = variableTable.getAddress(name);
				for(int i=0;i<constList.size();++i) {
					iseg.appendCode(Operator.PUSHI,constList.get(i));
					iseg.appendCode(Operator.POP,address+i);
				}


			} else {
				this.syntaxError(makeErrorMessage("]"));
			}

		}else {
			variableTable.registerNewVariable(Type.INT,name,1);
		}

	}
	/**
	 * 構文constantを解析するクラス
	 * @return 整数or文字
	 */
	int parseConstant() {
		// TODO 自動生成されたメソッド・スタブ
		int intValue=0;
		int isMinus =1;
		if (token.checkSymbol(Symbol.CHARACTER)) {
			intValue =token.getIntValue();
			token = lexer.nextToken();
		}else {
			if (token.checkSymbol(Symbol.SUB)) {
				token = lexer.nextToken();
				isMinus = -1;
			}
			if (token.checkSymbol(Symbol.INTEGER)) {
				intValue=token.getIntValue();
				token = lexer.nextToken();
			}else
				syntaxError(makeErrorMessage("character,integer,-", "parseConstant()"));;
			intValue*=isMinus;
		}
		return intValue;

	}
	/**
	 * 構文constantlistを解析するクラス
	 * @return 整数or文字のリスト
	 */
	ArrayList<Integer>  parseConstantList() {
		// 整数or文字のリストを生成
		ArrayList<Integer> constList = new ArrayList<>();
		if (judgeConstant())
			constList.add(parseConstant());
		else
			syntaxError(makeErrorMessage("constant"));
		//二つ以上要素がある場合
		while (token.checkSymbol(Symbol.COMMA)) {
			token = lexer.nextToken();
			if (judgeConstant())
				constList.add(parseConstant());
			else
				syntaxError(makeErrorMessage("constant"));
		}
		return constList;
	}
	/**
	 * 構文name_Listを解析するクラス
	 * nameが連なっている時に使用する
	 */

	private void parseName_List() {
		// TODO 自動生成されたメソッド・スタブ
		if (token.checkSymbol(Symbol.NAME))
			parseName();
		else
			syntaxError(makeErrorMessage("name"));
		while (token.checkSymbol(Symbol.COMMA)) {
			token = lexer.nextToken();
			if (token.checkSymbol(Symbol.NAME))
				parseName();
			else
				syntaxError(makeErrorMessage("name"));
		}
	}
	/**
	 * 構文unsignedFactorのファースト集合内にトークンがおさまったいるか確認するメソッド
	 * @return	収まっていたらtrueを返す
	 */
	boolean judgeUnsignedFactor() {
		if (token.checkSymbol(Symbol.NAME) || token.checkSymbol(Symbol.INC) || token.checkSymbol(Symbol.DEC)
				|| token.checkSymbol(Symbol.INTEGER) ||
				token.checkSymbol(Symbol.CHARACTER) || token.checkSymbol(Symbol.LPAREN)
				|| token.checkSymbol(Symbol.INPUTCHAR) || token.checkSymbol(Symbol.INPUTINT) ||
				token.checkSymbol(Symbol.ADD) || token.checkSymbol(Symbol.MUL))
			return true;
		else
			return false;

	}
	/**
	 * 構文Statementのファースト集合内にトークンがおさまったいるか確認するメソッド
	 * @return	収まっていたらtrueを返す
	 */
	boolean judgeStatement() {
		if (judgeExp() || token.checkSymbol(Symbol.OUTPUTCHAR) || token.checkSymbol(Symbol.OUTPUTINT)
				|| token.checkSymbol(Symbol.BREAK)
				|| token.checkSymbol(Symbol.IF) || token.checkSymbol(Symbol.FOR) || token.checkSymbol(Symbol.WHILE)
				|| token.checkSymbol(Symbol.LPAREN) ||
				token.checkSymbol(Symbol.LBRACE))
			return true;
		return false;
	}
	/**
	 * 構文expのファースト集合内にトークンがおさまったいるか確認するメソッド
	 * @return	収まっていたらtrueを返す
	 */
	boolean judgeExp() {
		if (judgeUnsignedFactor() || token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.NOT))
			return true;
		return false;
	}
	/**
	 * 構文constantのファースト集合内にトークンがおさまったいるか確認するメソッド
	 * @return	収まっていたらtrueを返す
	 */
	boolean judgeConstant() {
		if (token.checkSymbol(Symbol.CHARACTER) || token.checkSymbol(Symbol.SUB) || token.checkSymbol(Symbol.INTEGER))
			return true;
		return false;
	}

	//以降、必要なparse...メソッドを追記する。

	/**
	 * 現在読んでいるファイルを閉じる (lexerのcloseFile()に委譲)
	 */
	void closeFile() {
		lexer.closeFile();
	}

	/**
	 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
	 */
		void dump2file() {
			iseg.dump2file();
		}

		/**
		 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
		 *
		 * @param fileName 出力ファイル名
		 */
		void dump2file(String fileName) {
			iseg.dump2file(fileName);
		}

	/**
	 * エラーメッセージを出力しプログラムを終了する
	 *
	 * @param message 出力エラーメッセージ
	 */
	private void syntaxError(String message) {
		System.out.print(lexer.analyzeAt());
		//下記の文言は自動採点で使用するので変更しないでください。
		System.out.println("で構文解析プログラムが構文エラーを検出");
		System.out.println(message);
		closeFile();
		System.exit(1);
	}
	/**
	 * エラ-メッセージを生成するメソッド
	 * @param error	期待されるトークン
	 * @param in	 構文エラーの起きた構文(メソッド)
	 * @return	エラ-メッセージ
	 */
	private String makeErrorMessage(String error, String in) {
		return error + "が期待されますin" + in;

	}
	/**
	 * エラ〜メッセージを生成するメソッド
	 * @param error	期待されるトークン
	 * @return	エラ〜メッセージ
	 */
	private String makeErrorMessage(String error) {
		return error + "が期待されます";

	}

	/**
	 * 引数で指定したK20言語ファイルを解析する 読み込んだファイルが文法上正しければアセンブラコードを出力する
	 */
	public static void main(String[] args) {
		Kc parser;

		if (args.length == 0) {
			System.out.println("Usage: java kc.Kc20 file [objectfile]");
			System.exit(0);
		}

		parser = new Kc(args[0]);

		parser.parseProgram();
		parser.closeFile();

				if (args.length == 1)
					parser.dump2file();
				else
					parser.dump2file(args[1]);
	}

	public Token getToken() {
		return token;
	}
}
