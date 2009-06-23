package org.springframework.faces.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIColumn;
import javax.faces.component.UICommand;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.springframework.util.ReflectionUtils;

import com.sun.facelets.component.UIRepeat;

import junit.framework.TestCase;

public class SelectionTrackingActionListenerTests extends TestCase {

	/**
	 * The JSF view to simulate
	 */
	private UIViewRoot viewToTest;

	/**
	 * The list of row data objects to
	 */
	private OneSelectionTrackingListDataModel dataModel;

	/**
	 * The delegate action listener that should be called
	 */
	private TestDelegateActionListener delegateListener = new TestDelegateActionListener();

	/**
	 * The class under test
	 */
	private ActionListener selectionTrackingListener = new SelectionTrackingActionListener(delegateListener);

	public void setUp() {
		viewToTest = new UIViewRoot();
		List rows = new ArrayList();
		rows.add(new TestRowData());
		rows.add(new TestRowData());
		rows.add(new TestRowData());
		dataModel = new OneSelectionTrackingListDataModel(rows);
	}

	public void testProcessActionWithUIData() {

		UIData dataTable = new UIData();
		dataTable.setValue(dataModel);
		UIColumn column = new UIColumn();
		UICommand commandButton = new UICommand();
		column.getChildren().add(commandButton);
		dataTable.getChildren().add(column);
		viewToTest.getChildren().add(dataTable);
		dataTable.setRowIndex(1);

		ActionEvent event = new ActionEvent(commandButton);

		selectionTrackingListener.processAction(event);

		assertTrue(dataModel.isCurrentRowSelected());
		assertSame(dataModel.getSelectedRow(), dataModel.getRowData());
		assertTrue(delegateListener.processedEvent);

		dataModel.setRowIndex(2);
		assertFalse(dataModel.isCurrentRowSelected());
		assertTrue(dataModel.getSelectedRow() != dataModel.getRowData());
	}

	public void testProcessActionWithUIRepeat() {

		UIRepeat uiRepeat = new UIRepeat();
		uiRepeat.setValue(dataModel);
		UICommand commandButton = new UICommand();
		uiRepeat.getChildren().add(commandButton);
		viewToTest.getChildren().add(uiRepeat);

		Method indexMutator = ReflectionUtils.findMethod(UIRepeat.class, "setIndex", new Class[] { int.class });
		indexMutator.setAccessible(true);

		ReflectionUtils.invokeMethod(indexMutator, uiRepeat, new Object[] { new Integer(1) });

		ActionEvent event = new ActionEvent(commandButton);

		selectionTrackingListener.processAction(event);

		assertTrue(dataModel.isCurrentRowSelected());
		assertSame(dataModel.getSelectedRow(), dataModel.getRowData());
		assertTrue(delegateListener.processedEvent);

		ReflectionUtils.invokeMethod(indexMutator, uiRepeat, new Object[] { new Integer(2) });
		assertFalse(dataModel.isCurrentRowSelected());
		assertTrue(dataModel.getSelectedRow() != dataModel.getRowData());
	}

	private class TestRowData {

	}

	private class TestDelegateActionListener implements ActionListener {

		public boolean processedEvent = false;

		public void processAction(ActionEvent event) throws AbortProcessingException {
			processedEvent = true;
		}
	}
}
