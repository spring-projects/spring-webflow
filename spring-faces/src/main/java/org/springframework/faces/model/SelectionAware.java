/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.model;

import java.util.List;

import javax.faces.model.DataModel;

/**
 * Interface for {@link DataModel} implementations that need to track selected rows.
 * 
 * @author Jeremy Grelle
 */
public interface SelectionAware {

	public boolean isCurrentRowSelected();

	public void setSelected(boolean rowSelected);

	public void setSelections(List selections);

	public List getSelections();

	public void selectAll();

	public void select(Object rowData);
}
