package kc;

/**
*
* @author atkiyama
*問題番号3.1
*提出日 2020/6/3
*切り出したトークンを格納するクラス
*
*/
class Token {
	private Symbol symbol;
	private int intValue;
	private String strValue;


	 /**
     * 整数、名前、文字列以外のトークンを生成するときのコンストラクタ
     * @param symbol	トークンの種類
     *
     */
    Token(Symbol symbol) {
    		this.symbol=symbol;
    }

    /**
     * 整数のトークンを生成するときのコンストラクタ
     * @param symbol	トークンの種類
     * @param intValue	整数
     */
    Token(Symbol symbol,int intValue) {
    		this.symbol=symbol;
    		this.intValue=intValue;

    }

    /**
     * 文字列と名前のトークンを生成するときのコンストラクタ
     * @param symbol	トークンの種類
     * @param strValue	文字列
     */
    Token(Symbol symbol,String strValue) {
		this.symbol=symbol;
		this.strValue=strValue;
    }


    /**
     *フィールド変数symbolのタイプが引数のものと一致するか調べるメソッド
     * @param Symboltype	フィールドと一致するか調べたいタイプ
     * @return	一致するならtrueを返す
     */
    boolean checkSymbol(Symbol Symboltype) {
    		if(symbol.equals(Symboltype))
    			return true;
    		return false;

    }
    /**
     * フィールドsymbolのゲッター
     * @return	フィールドsymbol
     */
	public Symbol getSymbol() {
		return symbol;
	}
	/**
	 * フィールドintValueのゲッター
	 * @return	 フィールドintValue
	 */
	public int getIntValue() {
		return intValue;
	}
	/**
	 * フィールドstrValueのゲッター
	 * @return	フィールドstrValue
	 */
	public String getStrValue() {
		return strValue;
	}


}
