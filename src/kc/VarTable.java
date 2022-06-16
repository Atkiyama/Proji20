package kc;

import java.util.ArrayList;

/**
 * @author atkiyama
 *問題番号2.11
 *提出日 2020/5/27
 *変数のリストを管理するクラス
 */
class VarTable {

	/**
	 *varList 変数
	 *nextAddress 次に登録されるアドレス
	 */
	private ArrayList<Var> varList;
	private int nextAddress;

	/**
	 * リストを初期化しnextaddressを0にする
	 */
	VarTable() {
		varList = new ArrayList<>();
		nextAddress = 0;
	}

	/**
	 *リストに引数と同じ名前のvarがあったらそのvarを返す
	 *なければnullを返す
	 */
	private Var getVar(String name) {


//		System.out.println(exist(name));
		for (Var var : varList) {
			if (var.getName().equals(name))
				return var;
		}
		return null;

	}

	/**
	 *引数で与えられた名前 name を持つ変数が既に存在するかどうかを調べ，戻り値として返す.
	 */
//	boolean exist(String name) {
//		for (Var var : varList) {
//			if (name.equals(var.getName()))
//				return true;
//		}
//		return false;
//
//	}

	boolean exist(String name) {
		if(getVar(name)==null)
			return false;
		return true;
	}
	/**
	 *引数で与えられた型，名前，サイズ を持つ変数を登録する.登録できたら戻り値 true を返す.既に varList 中に同じ名前の変数が
	 *存在する場合は登録せず，戻り値 false を返す.
	 */
	boolean registerNewVariable(Type type, String name, int size) {
		if (!exist(name)) {
			varList.add(new Var(type, name, nextAddress, size));
			nextAddress+=size;
			return true;
		}
		return false;

	}

	/**
	 *リストの中の引数と同じ名前のvarのaddressを返す
	 */
	int getAddress(String name) {
		if(exist(name))
			return getVar(name).getAddress();
		return -1;
	}

	/**
	 *リストの中の引数と同じ名前のvarのtypeを返す
	 */
	Type getType(String name) {
		if(exist(name))
			return getVar(name).getType();
		return Type.NULL;
	}

	/**
	 *第1引数name で与えられた変数の型が第2引数typeと一致するか判定する
	 */
	boolean checkType(String name, Type type) {
		if (getType(name).equals(type))
			return true;
		return false;

	}

	/**
	 *リストの中の引数と同じ名前のvarのsizeを返す
	 */
	int getSize(String name) {
		if(exist(name))
			return getVar(name).getSize();
		return -1;
	}

	/**
     * 動作確認用のメインメソッド
     * int型変数およびint型配列を表に登録し、その後登録された変数を表示する
     */

    public static void main(String[] args) {
    	VarTable varTable=new VarTable();
    	//INT型のvar0~3を生成
    	for(int i=0;i<4;i++) {
    		varTable.registerNewVariable(Type.INT,"var"+String.valueOf(i),1);
    	}
    	//ARRAYOFINT型のvar4を生成
   	varTable.registerNewVariable(Type.ARRAYOFINT,"var4",10);

     for(int i=0;i<5;i++) {
    	 //INT型変数を表示
    	 	if(varTable.checkType("var"+String.valueOf(i),Type.INT)){
    	 		System.out.print("型:");
    	 		System.out.print(varTable.getType("var"+String.valueOf(i)));
    	 		System.out.print(" アドレス:");
    	 		System.out.print(varTable.getAddress("var"+String.valueOf(i)));
    	 		System.out.println(" の変数です");
    	 	// ARRAYOFINT型変数を表示
    	 	}else if(varTable.checkType("var"+String.valueOf(i),Type.ARRAYOFINT)){
    	 		System.out.print("型:");
    	 		System.out.print(varTable.getType("var"+String.valueOf(i)));
    	 		System.out.print(" アドレス:");
    	 		System.out.print(varTable.getAddress("var"+String.valueOf(i)));
    	 		System.out.println(" の変数です");
    	 	}
    	}



    }
}
