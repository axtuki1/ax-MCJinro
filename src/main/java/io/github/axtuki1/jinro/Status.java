package io.github.axtuki1.jinro;

/*
 * ゲームの大まかな状態
 * GameStandby: ゲーム開始前
 * GamePlaying: ゲーム進行中
 * GamePause: ゲーム一時停止中
 * GameEnd: ゲーム終了
 */

public enum Status{
	GameStandby, GamePause, GamePlaying, GameEnd;
	
	private static Status s = GameStandby;

	public static Status getStatus() {
	    return s;
	}

	public static void setStatus(Status status) {
	    s = status;
	}
}
