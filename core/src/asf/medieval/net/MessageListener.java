package asf.medieval.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by daniel on 11/16/15.
 */
class MessageListener extends Listener {
	private GameHost gameHost;

	public MessageListener(GameHost gameHost) {
		this.gameHost = gameHost;
	}

	public void connected(final Connection connection) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				gameHost.onConnected(connection);
			}
		});

	}

	public void received(final Connection c, final Object message) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				gameHost.onReceived(c, message);
			}
		});
	}

	public void disconnected(final Connection connection) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				gameHost.onDisconnected(connection);
			}
		});
	}

	@Override
	public void idle(final Connection connection) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				gameHost.onIdle(connection);
			}
		});
	}
}
