package asf.medieval.utility;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

/**
 * A table where each actor is expanded to fit the full space of the cell, and each cell
 * has the same width and height. though cells can be given a "colspan" to take up multiple
 * spaces.
 * <p/>
 * Created by daniel on 10/30/15.
 */
public class GridTable extends WidgetGroup {

	//private final Array<Array<Cell>> rows = new Array<Array<Cell>>(true, 1, Array.class);
	private final Array<Row> rows = new Array<Row>(true, 1, Row.class);

	private float minWidth,minHeight,prefWidth,prefHeight,maxWidth,maxHeight;

	public GridTable() {
	}


	public Row row() {
		Row row = new Row();
		rows.add(row);
		return row;
	}


	@Override
	protected void sizeChanged() {
		super.sizeChanged();
		System.out.println("invalidating");
	}

	@Override
	public void layout() {

		computeSize();
		final float width = getWidth();
		final float height = getHeight();

		int numRows = 0;
		int numColumns = 0;

		for (Row row : rows) {
			if (!row.hidden) {
				numRows++;
				int count=0;
				for (Cell cell : row.cells) {
					if(!cell.hidden) count++;
				}
				if (count > numColumns) numColumns = count;
			}
		}

		final float rowHeight = height / numRows;
		final float colWidth = width / numColumns;


		float boundsY = 0;
		for (int y = rows.size - 1; y >= 0; y--) {
			final Row row = rows.items[y];
			if (!row.hidden) {
				float boundsX = 0;
				for (int x = 0; x < row.cells.size; x++) {
					Cell cell = row.cells.items[x];
					if (!cell.hidden) {
						if(!cell.actor.hasParent())
							GridTable.super.addActor(cell.actor);
						final float cellWidth = colWidth * cell.colspan;
						cell.actor.setBounds(boundsX, boundsY, cellWidth, rowHeight);
						boundsX += cellWidth;
					}else{
						cell.actor.remove();
					}
				}
				boundsY += rowHeight;
			}
		}


	}

	private void computeSize()
	{
		float parentWidth = getParent().getWidth();
		float parentHeight = getParent().getHeight();

		final float dimension = UtMath.smallest(parentWidth,parentHeight);
		System.out.println("the dimension: "+dimension);


		final float neededMinWidthToShowAllText = 140 *4f;

		final float buttonWidth = dimension / 4f; // number of cols
		final float buttonHeight = dimension / 7.5f; // number of rows + 0.5f;


		if(dimension < neededMinWidthToShowAllText && parentWidth >= neededMinWidthToShowAllText)
		{
			minWidth = neededMinWidthToShowAllText;
			minHeight = dimension;
		}
		else
		{
			minWidth = dimension;
			minHeight = dimension;
		}

		prefWidth = minWidth;
		prefHeight = minHeight;
		maxWidth = minWidth;
		maxHeight = minHeight;

	}

	public class Row {
		private final Array<Cell> cells = new Array<Cell>(true, 1, Cell.class);
		private boolean hidden = false;

		public Cell add(Actor actor) {
			Cell cell = new Cell();
			cell.actor = actor;
			cells.add(cell);
			GridTable.this.invalidate();
			return cell;
		}

		public Cell get(int index) {
			return cells.get(index);
		}

		public Row hidden(boolean hidden) {
			this.hidden = hidden;
			GridTable.this.invalidate();
			return this;
		}

		public boolean isHidden(){
			return this.hidden;
		}


	}

	public class Cell {
		private Actor actor;
		private float colspan = 1;
		private boolean hidden = false;

		public Cell colspan(float colspan) {
			this.colspan = colspan;
			GridTable.this.invalidate();
			return this;
		}

		public Cell hidden(boolean hidden) {
			this.hidden = hidden;
			GridTable.this.invalidate();
			return this;
		}

		public boolean isHidden(){
			return this.hidden;
		}

	}


	@Override
	public float getMinWidth() {
		return minWidth;
	}

	@Override
	public float getMinHeight() {
		return minHeight;
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public float getMaxWidth() {
		return maxWidth;
	}

	@Override
	public float getMaxHeight() {
		return maxHeight;
	}
}