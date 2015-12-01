package asf.medieval.view.editor;

import asf.medieval.utility.UtFileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileFilter;

/**
 * A file chooser made for working with internal and local assets
 * <p/>
 * Created by daniel on 11/30/15.
 */
public class FileChooser extends Table {


	private Listener listener;
	private boolean canSave;
	private boolean canOpen;
	private boolean canDelete;

	private ScrollPane scrollPane;
	private List<ListItem> list;
	private TextField textField;
	private final InternalTextFieldListener internalTextFieldListener = new InternalTextFieldListener();
	private TextButton saveButton, openButton, deleteButton;

	private String directoryName;
	private String[] allowedFileTypes;

	public FileChooser(boolean canSave, boolean canOpen, boolean canDelete, Listener listener, Skin skin) {
		super(skin);
		this.canSave = canSave;
		this.canOpen = canOpen;
		this.canDelete = canDelete;
		this.listener = listener;

		InternalButtonClickListener internalButtonClickListener = new InternalButtonClickListener();

		list = new List<ListItem>(skin);
		//list.getSelection().setProgrammaticChangeEvents(false);
		list.addListener(internalButtonClickListener);
		list.addListener(new InternalListChangeListener());


		scrollPane = new ScrollPane(list);
		scrollPane.setOverscroll(false, false);

		textField = new TextField("", skin);
		textField.setTextFieldListener(internalTextFieldListener);
		textField.setTextFieldFilter(internalTextFieldListener);


		saveButton = UtEditor.createTextButton("Save", skin, internalButtonClickListener);
		openButton = UtEditor.createTextButton("Open", skin, internalButtonClickListener);
		openButton.setDisabled(true);
		deleteButton = UtEditor.createTextButton("Delete", skin, internalButtonClickListener);
		deleteButton.setDisabled(true);


		final int numCols = (canSave ? 1 : 0) + (canOpen ? 1 : 0) + (canDelete ? 1 : 0);

		row().fill();
		add(scrollPane).colspan(numCols).expand();
		row().fill();
		add(textField).colspan(numCols);
		row().fill();
		if (canSave)
			add(saveButton);
		if (canOpen)
			add(openButton);
		if (canDelete)
			add(deleteButton);

	}

	public void changeDirectory(final String directoryName, final String[] fileTypes, String selected) {
		this.directoryName = directoryName;
		this.allowedFileTypes = fileTypes;

		FileHandle directory = UtFileHandle.relative(directoryName);

		if (selected != null) {
			FileHandle selectedFile = UtFileHandle.relative(directoryName + "/" + selected);
			selected = selectedFile.name();

		}


		ListItem listItemSelected = null;
		final FileHandle[] listDir = directory.list(directoryFilter);
		Array<ListItem> items = new Array<ListItem>(true, listDir.length, ListItem.class);
		for (final FileHandle handle : listDir) {
			ListItem listItem = new ListItem(handle, getSkin());
			items.add(listItem);
			if (listItem.fileName.equals(selected)) {
				listItemSelected = listItem;
			}
		}

		//items.sort(dirListComparator);
		list.setItems(items);

		if (listItemSelected != null) {
			list.getSelection().set(listItemSelected);
			textField.setText(listItemSelected.fileName);
		} else {
			list.getSelection().clear();
			textField.setText("");
		}

	}

	private final FileFilter directoryFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			for (String fileType : allowedFileTypes) {
				if (pathname.getName().endsWith(fileType))
					return true;
			}
			return false;
		}
	};

	private static class ListItem {
		private FileHandle fileHandle;
		private String fileName;

		public ListItem(FileHandle fileHandle, Skin skin) {
			this.fileHandle = fileHandle;
			this.fileName = fileHandle.name();

//			if(fileName.endsWith(".ter")){
//				Image image = new Image(skin.getDrawable("terrain"));
//				addActor(image);
//			}else if(fileName.toLowerCase().endsWith(".png")){
//				Image image = new Image(skin.getDrawable("file-png"));
//				addActor(image);
//			}else if(fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg") ){
//				Image image = new Image(skin.getDrawable("file-jpg"));
//				addActor(image);
//			}


//			Label label = new Label(fileName, skin);
//			addActor(label);
		}

		@Override
		public String toString() {
			return fileName;
		}
	}

	private class InternalListChangeListener implements EventListener {

		public boolean handle(Event event) {
			Actor actor = event.getTarget();
			if (event instanceof ChangeListener.ChangeEvent) {
				if (actor == list) {
					ListItem selected = list.getSelected();
					final boolean disabled = selected == null;
					openButton.setDisabled(disabled);
					deleteButton.setDisabled(disabled);
					return true;
				}
			}
			return false;
		}
	}

	private class InternalTextFieldListener implements TextField.TextFieldFilter, TextField.TextFieldListener {

		@Override
		public boolean acceptChar(TextField textField, char c) {
			return Character.isLetterOrDigit(c) || c=='.' || c=='_' || c=='-';
		}

		@Override
		public void keyTyped(TextField textField, char c) {

			String fname = textField.getText().toLowerCase();

			ListItem currentlySelected = list.getSelected();
			ListItem newSelected = null;

			for (ListItem listItem : list.getItems()) {
				String listItemName = listItem.fileHandle.name().toLowerCase();

				if (listItemName.equals(fname)) {
					newSelected = listItem;
					break;
				}
			}

			if (newSelected != currentlySelected) {
				if (newSelected == null) {
					list.getSelection().clear();
				} else {
					list.getSelection().set(newSelected);
				}
			}
		}


	}

	private class InternalButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();


			if (actor == saveButton) {



				ListItem selected = list.getSelected();
				if (selected != null) {
					FileHandle selectedFh = selected.fileHandle;
					if (listener != null)
						listener.onFileSave(selectedFh);
				} else {
					boolean allowed = false;
					String fname = textField.getText();
					int pos = fname.lastIndexOf(".");
					if (pos > 0) {
						String extension = fname.substring(pos);
						for (String allowedFileType : allowedFileTypes) {
							if (allowedFileType.equalsIgnoreCase(extension)) {
								allowed = true;
								break;
							}
						}

					}

					if (!allowed) {
						// show the user what the file extension will be instead of just saving...
						if(pos>0){
							fname = fname.substring(0, pos) + allowedFileTypes[0];
						}else{
							fname = fname + allowedFileTypes[0];
						}
						textField.setText(fname);
						internalTextFieldListener.keyTyped(textField,' ');
					} else {
						fname = directoryName + "/" + fname;
						FileHandle selectedFh = UtFileHandle.relative(fname);
						if (listener != null)
							listener.onFileSave(selectedFh);
					}
				}




			} else if (actor == openButton) {
				ListItem selected = list.getSelected();
				if(selected != null){
					if (listener != null)
						listener.onFileOpen(selected.fileHandle);
				}


			} else if (actor == deleteButton) {

			} else if (actor == list) {
				ListItem selected = list.getSelected();
				if (selected != null) {
					textField.setText(selected.fileName);
				}
			}

		}
	}


	public interface Listener {
		public void onFileSave(FileHandle fh);

		public void onFileOpen(FileHandle fh);
	}
}
