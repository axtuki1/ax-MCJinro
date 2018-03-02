package io.github.axtuki1.jinro;

/*
 * 一日の状態
 * Standby: ゲーム開始前
 * Night: 夜(人狼噛みつき等)
 * Discussion: 朝、昼(議論)
 * Vote: 夕方(投票)
 * Execution: 処刑
 */

public enum Cycle{
	Standby, Night, Discussion, Vote, Execution, VoteAgain;
	
	private static Cycle s = Standby;

	public static Cycle getStatus() {
	    return s;
	}

	public static void setStatus(Cycle status) {
	    s = status;
	}

	public static String getStatusLocalize(){
		switch (getStatus()){
			case Night:
				return "夜";
			case Vote:
			case VoteAgain:
				return "投票時間";
			case Standby:
				return "待機時間";
			case Execution:
				return "処刑時間";
			case Discussion:
				return "議論時間";
		}
		return null;
	}
}
