package org.springframework.faces.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.test.mock.MockFacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.faces.webflow.JSFMockHelper;
import org.springframework.util.ReflectionUtils;

import com.sun.faces.facelets.component.UIRepeat;

import jakarta.faces.component.UIColumn;
import jakarta.faces.component.UICommand;
import jakarta.faces.component.UIData;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

public class SelectionTrackingActionListenerTests {

	/**
	 * JSF Mock Helper
	 */
	private final JSFMockHelper jsfMockHelper = new JSFMockHelper();

	/**
	 * The JSF view to simulate
	 */
	private UIViewRoot viewToTest;

	/**
	 * The list of row data objects to
	 */
	private OneSelectionTrackingListDataModel<Object> dataModel;

	/**
	 * The delegate action listener that should be called
	 */
	private final TestDelegateActionListener delegateListener = new TestDelegateActionListener();

	/**
	 * The class under test
	 */
	private final ActionListener selectionTrackingListener = new SelectionTrackingActionListener(this.delegateListener);

	@BeforeEach
	public void setUp() throws Exception {
		this.jsfMockHelper.setUp();
		this.viewToTest = new UIViewRoot();
		List<Object> rows = new ArrayList<>();
		rows.add(new TestRowData());
		rows.add(new TestRowData());
		rows.add(new TestRowData());
		this.dataModel = new OneSelectionTrackingListDataModel<>(rows);
	}

	@AfterEach
	public void tearDown() throws Exception {
		this.jsfMockHelper.tearDown();
	}

	@Test
	public void testProcessActionWithUIData() {

		UIData dataTable = new UIData();
		dataTable.setValue(this.dataModel);
		UIColumn column = new UIColumn();
		UICommand commandButton = new UICommand();
		column.getChildren().add(commandButton);
		dataTable.getChildren().add(column);
		this.viewToTest.getChildren().add(dataTable);
		dataTable.setRowIndex(1);

		ActionEvent event = new ActionEvent(commandButton);

		this.selectionTrackingListener.processAction(event);

		assertTrue(this.dataModel.isCurrentRowSelected());
		assertSame(this.dataModel.getSelectedRow(), this.dataModel.getRowData());
		assertTrue(this.delegateListener.processedEvent);

		this.dataModel.setRowIndex(2);
		assertFalse(this.dataModel.isCurrentRowSelected());
		assertNotSame(this.dataModel.getSelectedRow(), this.dataModel.getRowData());
	}

	@Test
	public void testProcessActionWithUIRepeat() {

		UIRepeat uiRepeat = new UIRepeat();
		uiRepeat.setValue(this.dataModel);
		UICommand commandButton = new UICommand();
		uiRepeat.getChildren().add(commandButton);
		this.viewToTest.getChildren().add(uiRepeat);

		Method indexMutator = ReflectionUtils.findMethod(UIRepeat.class, "setIndex", FacesContext.class,
				int.class);
		indexMutator.setAccessible(true);

		ReflectionUtils.invokeMethod(indexMutator, uiRepeat, new MockFacesContext(), 1);

		ActionEvent event = new ActionEvent(commandButton);

		this.selectionTrackingListener.processAction(event);

		assertTrue(this.dataModel.isCurrentRowSelected());
		assertSame(this.dataModel.getSelectedRow(), this.dataModel.getRowData());
		assertTrue(this.delegateListener.processedEvent);

		ReflectionUtils.invokeMethod(indexMutator, uiRepeat, new MockFacesContext(), 2);
		assertFalse(this.dataModel.isCurrentRowSelected());
		assertNotSame(this.dataModel.getSelectedRow(), this.dataModel.getRowData());
	}

	private static class TestRowData {

	}

	private static class TestDelegateActionListener implements ActionListener {

		public boolean processedEvent = false;

		public void processAction(ActionEvent event) throws AbortProcessingException {
			this.processedEvent = true;
		}
	}
}
