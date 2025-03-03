package com.cherokeelessons.cll1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.cll1.AbstractGame;
import com.cherokeelessons.cll1.CLL1;
import com.cherokeelessons.cll1.actions.MusicVolumeAction;

public class ScreenPoweredBy extends AbstractScreen {

	private static final Vector2 WORLDSIZE = CLL1.WORLDSIZE;
	private final Array<Image> logo = new Array<Image>();
	private final Array<Texture> textures = new Array<Texture>();

	private final Runnable onDone;

	private final float tvSafePercent = .05f;

	private final Rectangle tvSafe = new Rectangle(WORLDSIZE.x * tvSafePercent, WORLDSIZE.y * tvSafePercent,
			WORLDSIZE.x * (1f - 2f * tvSafePercent), WORLDSIZE.y * (1f - 2f * tvSafePercent));

	private Rectangle logoBox;

	public ScreenPoweredBy(final AbstractGame game, final Runnable onDone) {
		super(game);
		this.onDone = onDone;
		init();
		music.play();
	}

	@Override
	protected void act(final float delta) {
		if (Gdx.input.justTouched()) {
			if (onDone != null) {
				Gdx.app.postRunnable(onDone);
			}
		}
	}

	@Override
	public void dispose() {
		music.dispose();
		for (final Texture texture : textures) {
			texture.dispose();
		}
		super.dispose();
	}

	private Action getAlphaAction() {
		final SequenceAction sa = Actions.sequence();
		sa.addAction(Actions.delay(1f));
		sa.addAction(Actions.alpha(1f, 4f));
		sa.addAction(Actions.delay(4f));
		sa.addAction(Actions.alpha(0f, 2f));
		sa.addAction(Actions.delay(1f));
		return sa;
	}

	private Action getVolumeAction(final Music _music) {
		final SequenceAction sa = Actions.sequence();
		sa.addAction(Actions.delay(1f));
		sa.addAction(new MusicVolumeAction(_music, .7f, 4f));
		sa.addAction(Actions.delay(4f));
		sa.addAction(new MusicVolumeAction(_music, 0f, 2f));
		sa.addAction(Actions.delay(1f));
		if (onDone != null) {
			sa.addAction(Actions.run(onDone));
		}
		return sa;
	}

	@Override
	public void hide() {
		super.hide();
	}

	private void init() {
		music = Gdx.audio.newMusic(Gdx.files.internal("libgdx/atmoseerie03.mp3"));
		music.setVolume(0f);
		for (int i = 0; i < 25; i++) {
			final Texture texture = new Texture(Gdx.files.internal("libgdx/1080p_" + i + ".png"));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textures.add(texture);
			final Image image = new Image(texture);
			image.pack();
			logo.add(image);
		}

		int width = 0;
		int height = 0;
		for (int x = 0; x < 5; x++) {
			height = 0;
			Image img = null;
			for (int y = 0; y < 5; y++) {
				final int z = 4 - y;
				final int p = z * 5 + x;
				img = logo.get(p);
				img.setOrigin(0, 0);
				img.setPosition(width, height);
				height += img.getHeight();
			}
			if (img != null) {
				width += img.getWidth();
			}
		}

		logoBox = new Rectangle(0, 0, width, height);
		logoBox.fitInside(tvSafe);
		float scaleXY = logoBox.height / height;
		if (scaleXY > logoBox.width / width) {
			scaleXY = logoBox.width / width;
		}

		final Group logoGroup = new Group();
		for (final Image img : logo) {
			logoGroup.addActor(img);
		}

		logoGroup.getColor().a = 0f;
		logoGroup.setOrigin(Align.center);
		logoGroup.setScale(scaleXY);
		logoGroup.setTransform(true);

		logoGroup.setPosition(logoBox.x, logoBox.y);

		logoGroup.addAction(Actions.parallel(getAlphaAction(), getVolumeAction(music)));

		stage.addActor(logoGroup);
	}

	@Override
	protected boolean onBack() {
		if (onDone != null) {
			Gdx.app.postRunnable(onDone);
		}
		return true;
	}

	@Override
	protected boolean onMenu() {
		if (onDone != null) {
			Gdx.app.postRunnable(onDone);
		}
		return true;
	}
}
