package kc;

/**

 * @author atkiyama
 *問題番号2.10
 *提出日 2020/5/27
 *変数を表すクラス
 */
class Var {
	/**
	 * type 変数のタイプの定数
	 * name 変数名
	 * address Dsegのアドレス
	 * size 配列サイズ
	 *
	 */
	private Type type;
	private String name;
	private int address;
	private int size;

	/**
	 * フィールドを初期化
	 * @param type 変数のタイプの定数
	 * @param name 変数名
	 * @param address Dscgのアドレス
	 * @param size 配列サイズ
	 */


	public Var(Type type, String name, int address, int size) {
		this.type = type;
		this.name = name;
		this.address = address;
		this.size = size;
	}
	/**
	 * typeのゲッター
	 * @return type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * nameのゲッター
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * addressのゲッター
	 * @return address
	 */
	public int getAddress() {
		return address;
	}
	/**
	 * sizeのゲッター
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/*
	 * テスト用
	 public static void main(String[] args) {
		 Var var=new Var(Type.INT,"test",0,4);
		 System.out.println(var.getAddress());
		 System.out.println(var.getType());
		 System.out.println(var.getSize());
		 System.out.println(var.getName());

	  }*/

}
