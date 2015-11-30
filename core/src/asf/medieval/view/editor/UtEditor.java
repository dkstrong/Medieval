package asf.medieval.view.editor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by daniel on 11/29/15.
 */
public class UtEditor {




	private UtEditor(){

	}

	protected static Label createLabel(String labelText, Skin skin) {
		Label label = new Label(labelText, skin);
		label.setAlignment(Align.left,Align.left);
		return label;
	}

	protected static TextButton createTextButton(String buttonText, Skin skin, EventListener listener) {
		TextButton textButton = new TextButton(buttonText, skin);
		if(listener!=null)
			textButton.addListener(listener);
		return textButton;
	}

	protected static ImageButton createImageButtonToggle(Texture texture, Skin skin, EventListener listener) {
		TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));

		ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get("toggle",ImageButton.ImageButtonStyle.class));
		style.imageUp = drawable;

		ImageButton imageButton = new ImageButton(style);
		imageButton.getImageCell().maxSize(50).padTop(2).padBottom(2);
		if(listener!=null)
			imageButton.addListener(listener);
		return imageButton;
	}

	protected static ImageButton createImageButtonToggle(String styleName, Skin skin, EventListener listener) {

		ImageButton imageButton = new ImageButton(skin, styleName);

		imageButton.getImageCell().maxSize(50).padTop(2).padBottom(2);
		if(listener!=null)
			imageButton.addListener(listener);
		return imageButton;
	}

	protected static TextButton createTextButtonToggle(String buttonText, Skin skin, EventListener listener) {
		TextButton textButton = new TextButton(buttonText, skin,"toggle");
		if(listener!=null)
			textButton.addListener(listener);
		return textButton;
	}

	protected static Actor createRow(Actor... actors){

		HorizontalGroup hg = new HorizontalGroup();
		for (Actor actor : actors) {
			hg.addActor(actor);
		}
		return hg;

	}

	protected static Tree createTree(Skin skin){
		Tree tree = new Tree(skin);
		return tree;
	}

	protected static Image createImage(Texture texture){
		TextureRegion tr = new TextureRegion(texture);
		tr.setRegionWidth(32);
		tr.setRegionHeight(32);

		Image image = new Image(tr);

		return image;
	}

	protected static Tree.Node createTreeNode(Actor actor){


		Tree.Node treeNode = new Tree.Node(actor);
		treeNode.setSelectable(false);
//		if(icon != null){
//			TextureRegion tr = new TextureRegion(icon);
//			tr.setRegionWidth(32);
//			tr.setRegionHeight(32);
//			TextureRegionDrawable drawable = new TextureRegionDrawable(tr);
//			treeNode.setIcon(drawable);
//		}

		return treeNode;
	}

	protected static Tree.Node createTreeNode(String label, String value, Skin skin){

		Label lbl = new Label(label+":", skin);
		Label vlu = new Label(value, skin);


		Tree.Node treeNode = new Tree.Node(createRow(lbl, vlu));
		treeNode.setSelectable(false);

		return treeNode;
	}
}
